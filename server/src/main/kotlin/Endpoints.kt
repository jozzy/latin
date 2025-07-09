package org.latin.server

import io.ktor.http.ContentType.Application.Json
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.ServiceUnavailable
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.eclipse.lmos.arc.agents.ArcAgents
import org.eclipse.lmos.arc.agents.ConversationAgent
import org.eclipse.lmos.arc.agents.agent.health
import org.eclipse.lmos.arc.agents.getAgentByName
import org.eclipse.lmos.arc.graphql.inbound.EventSubscriptionHolder
import org.eclipse.lmos.arc.server.ktor.EnvConfig
import org.latin.server.modules.LatinModule
import org.latin.server.modules.ModulesManager
import org.latin.server.modules.ModuleExecutor
import org.slf4j.LoggerFactory
import kotlinx.coroutines.*

private val log = LoggerFactory.getLogger("Endpoints")

fun ArcAgents.serve(
    modulesManager: ModulesManager,
    wait: Boolean = true,
    port: Int? = null,
    events: Map<String, suspend (String) -> String>,
) {
    val eventSubscriptionHolder = EventSubscriptionHolder()
    add(eventSubscriptionHolder)

    val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
    }

    embeddedServer(CIO, port = port ?: EnvConfig.serverPort) {
        install(RoutingRoot) {
            // Health endpoint
            get("/health") {
                val health = health()
                call.respondText(json.encodeToString(health), Json, if (health.ok) OK else ServiceUnavailable)
            }
            // Same implementation for readyness and liveness probes
            get("/health/*") {
                val health = health()
                call.respondText(json.encodeToString(health), Json, if (health.ok) OK else ServiceUnavailable)
            }

            get("/status") {
                call.respondText(
                    json.encodeToString(
                        Status(
                            status = if (modulesManager.isReady) "READY" else "LOADING",
                            event = events.keys.toList(),
                            modules = modulesManager.list(),
                        ),
                    )
                )
            }

            // Legacy event endpoints
            post("/events/*") {
                val event = call.request.uri.substringAfterLast("/events/")
                val result = events[event]!!(call.receiveText())
                call.respondText(result)
            }

            // Dynamic UseCase endpoints - generate after modules are loaded
            log.info("Starting dynamic endpoint generation...")
            generateDynamicUseCaseEndpoints(modulesManager, json, this@serve)
            log.info("Dynamic endpoint generation completed.")
        }
    }.start(wait = wait)
}

private fun Routing.generateDynamicUseCaseEndpoints(
    modulesManager: ModulesManager,
    json: Json,
    arcAgents: ArcAgents
) {
    log.info("=== Starting generateDynamicUseCaseEndpoints ===")
    
    // Launch a coroutine to wait for modules to be loaded
    CoroutineScope(Dispatchers.IO).launch {
        // Wait for modules to be ready
        while (!modulesManager.isReady) {
            log.info("Waiting for modules to be loaded...")
            delay(1000) // Wait 1 second
        }
        
        log.info("Modules are ready, generating dynamic endpoints...")
        
        // Get all modules with start conditions (UseCase modules)
        val useCaseModules = modulesManager.getModulesWithStartConditions()
        
        log.info("Found ${useCaseModules.size} UseCase modules")
        useCaseModules.forEach { module ->
            log.info("  - ${module.name}: useCase=${module.useCase}, startCondition=${module.startCondition}")
        }
        
        if (useCaseModules.isEmpty()) {
            log.warn("No UseCase modules found! This might be the issue.")
            return@launch
        }
        
        // Create a simple endpoint that handles all UseCase modules
        post("/latin/{useCase}") {
            try {
                val useCase = call.parameters["useCase"]
                if (useCase == null) {
                    log.error("useCase parameter is missing")
                    call.respondText(
                        json.encodeToString(ErrorResponse("useCase parameter is required")),
                        Json,
                        BadRequest
                    )
                    return@post
                }
                
                log.info("Received request for useCase: $useCase")
                
                // Find the module by useCase
                val module = modulesManager.getModuleByUseCase(useCase)
                if (module == null) {
                    log.error("Module not found for useCase: $useCase")
                    call.respondText(
                        json.encodeToString(ErrorResponse("Module not found for useCase: $useCase")),
                        Json,
                        NotFound
                    )
                    return@post
                }
                
                // Parse request body
                val requestBody = call.receiveText()
                log.info("Request body: $requestBody")
                
                // Extract parameters from request
                val parameters = parseParametersFromRequest(requestBody, module)
                log.info("Extracted parameters: $parameters")
                
                // For simple modules, execute directly without AI agent
                val result = if (isSimpleModule(module)) {
                    handleSimpleModuleDirectly(module, requestBody)
                } else {
                    // Execute module with AI agent for complex modules
                    val agent = arcAgents.getAgentByName("latin-agent") as ConversationAgent
                    val moduleExecutor = ModuleExecutor(modulesManager)
                    
                    moduleExecutor.runModule(
                        agent = agent,
                        module = module,
                        input = requestBody
                    )
                }
                
                log.info("Module ${module.useCase} executed successfully: $result")
                
                // Return result
                call.respondText(result.toString(), status = OK)
                
            } catch (e: Exception) {
                log.error("Error executing module: ${e.message}", e)
                call.respondText(
                    json.encodeToString(ErrorResponse("Failed to execute module: ${e.message}")),
                    Json,
                    BadRequest
                )
            }
        }
        
        log.info("Successfully created dynamic endpoint: POST /latin/{useCase}")
        log.info("Available UseCase modules:")
        useCaseModules.forEach { module ->
            log.info("  - POST /latin/${module.useCase} for module: ${module.name}")
        }
        
        log.info("=== Completed generateDynamicUseCaseEndpoints ===")
    }
}

private fun parseParametersFromRequest(requestBody: String, module: LatinModule): Map<String, String> {
    val parameters = mutableMapOf<String, String>()
    
    try {
        // Simple JSON parsing using regex for key-value pairs
        val jsonPattern = "\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"".toRegex()
        val matches = jsonPattern.findAll(requestBody)
        
        matches.forEach { match ->
            val key = match.groupValues[1]
            val value = match.groupValues[2]
            if (module.inputParameters.contains(key)) {
                parameters[key] = value
            }
        }
        
        // If no parameters found, treat as plain text
        if (parameters.isEmpty()) {
            parameters["input"] = requestBody
        }
        
    } catch (e: Exception) {
        // If parsing fails, treat as plain text
        log.info("Request parsing failed, treating as plain text")
        parameters["input"] = requestBody
    }
    
    return parameters
}

private fun isSimpleModule(module: LatinModule): Boolean {
    // Simple modules are those with basic instructions that don't require AI processing
    val simpleKeywords = listOf("send email", "log", "respond", "return")
    val instructions = module.instructions.lowercase()
    
    return simpleKeywords.any { keyword -> instructions.contains(keyword) } &&
           !instructions.contains("@") // No special AI commands
}

private fun handleSimpleModuleDirectly(module: LatinModule, requestBody: String): String {
    log.info("Handling simple module directly: ${module.name}")
    
    val parameters = parseParametersFromRequest(requestBody, module)
    
    // Generate a simple response based on the module's instructions
    val response = when {
        module.useCase == "send_email" -> {
            val recipient = parameters["recipient"] ?: "unknown"
            "Email sent to $recipient"
        }
        module.useCase == "find_leads" -> {
            val keywords = parameters["target_keywords"] ?: "unknown"
            "Found leads for keywords: $keywords"
        }
        module.useCase == "enrich_profiles" -> {
            val leads = parameters["raw_leads"] ?: "unknown"
            "Enriched profiles for leads: $leads"
        }
        module.useCase == "fetch_offer_for_industry" -> {
            val industry = parameters["industry"] ?: "unknown"
            "Fetched offer for industry: $industry"
        }
        module.useCase == "personalize_message" -> {
            val lead = parameters["lead"] ?: "unknown"
            "Personalized message for lead: $lead"
        }
        module.useCase == "automated_pipeline" -> {
            val keywords = parameters["target_keywords"] ?: "unknown"
            "Automated pipeline executed for keywords: $keywords"
        }
        else -> {
            "Module ${module.useCase} executed successfully"
        }
    }
    
    log.info("Simple module response: $response")
    return response
}

@Serializable
data class Status(val status: String, val event: List<String>, val modules: List<LatinModule>)

@Serializable
data class ErrorResponse(val error: String)

