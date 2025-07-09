package org.latin.server.modules

import org.eclipse.lmos.arc.agents.AgentFailedException
import org.eclipse.lmos.arc.agents.ConversationAgent
import org.eclipse.lmos.arc.agents.conversation.AssistantMessage
import org.eclipse.lmos.arc.agents.conversation.latest
import org.eclipse.lmos.arc.agents.conversation.toConversation
import org.eclipse.lmos.arc.core.Result
import org.eclipse.lmos.arc.core.getOrThrow
import org.eclipse.lmos.arc.core.map
import org.slf4j.LoggerFactory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.eclipse.lmos.arc.core.Success




class ModuleExecutor(
    private val modulesManager: ModulesManager,
) {
    private val handover = "<HANDOVER:(.*?)>".toRegex(RegexOption.IGNORE_CASE)
    private val log = LoggerFactory.getLogger(ModuleExecutor::class.java)

    suspend fun runModule(
        agent: ConversationAgent,
        module: LatinModule,
        input: String = "",
    ): Result<String, AgentFailedException> {
        log.info("Executing module: ${module.name} (UseCase: ${module.useCase})")
        log.info("Input: $input")
        
        // For simple modules (like send_email), handle them directly without AI
        if (isSimpleModule(module)) {
            return handleSimpleModule(module, input)
        }
        
        // Enhance input with module context for UseCase modules
        val enhancedInput = if (module.startCondition != null && module.inputParameters.isNotEmpty()) {
            buildEnhancedInput(input, module)
        } else {
            input
        }
        
        log.info("Enhanced input: $enhancedInput")
        
        return agent.execute(enhancedInput.toConversation(), context = setOf(module))
            .map { it.latest<AssistantMessage>()?.content ?: "" }
            .map { content ->
                val handovers = handover.findAll(content).map { it.groupValues[1] }
                    .filter { it.isNotBlank() }
                    .toSet()
                if (handovers.isNotEmpty()) {
                    log.info("Handover detected: $handovers")
                    val targetModule = modulesManager.getModuleByName(handovers.first())
                    if (targetModule != null) {
                        runModule(agent, input = input, module = targetModule).getOrThrow()
                    } else {
                        log.error("Target module not found: ${handovers.first()}")
                        "Error: Target module ${handovers.first()} not found"
                    }
                } else {
                    content
                }
            }
    }

    private fun isSimpleModule(module: LatinModule): Boolean {
        // Simple modules are those with basic instructions that don't require AI processing
        val simpleKeywords = listOf("nohting")
        val instructions = module.instructions.lowercase()
        
        return simpleKeywords.any { keyword -> instructions.contains(keyword) } &&
               !instructions.contains("@") // No special AI commands
    }

    private fun handleSimpleModule(module: LatinModule, input: String): Success<String> {
        log.info("Handling simple module: ${module.name}")
        
        val parameters = parseInputParameters(input, module)
        
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
        return Success(response)

    }

    private fun buildEnhancedInput(input: String, module: LatinModule): String {
        val parameters = parseInputParameters(input, module)
        
        val contextBuilder = StringBuilder()
        contextBuilder.append("Module: ${module.useCase}\n")
        contextBuilder.append("Description: ${module.description ?: "No description"}\n")
        contextBuilder.append("Start Condition: ${module.startCondition}\n")
        contextBuilder.append("Expected Parameters: ${module.inputParameters.joinToString(", ")}\n")
        
        if (parameters.isNotEmpty()) {
            contextBuilder.append("Provided Parameters:\n")
            parameters.forEach { (key, value) ->
                contextBuilder.append("  $key: $value\n")
            }
        }
        
        contextBuilder.append("\nInstructions:\n")
        contextBuilder.append(module.instructions)
        
        return contextBuilder.toString()
    }

    private fun parseInputParameters(input: String, module: LatinModule): Map<String, String> {
        val parameters = mutableMapOf<String, String>()
        
        try {
            // Try to parse as JSON
            val json = Json.parseToJsonElement(input)
            if (json is JsonObject) {
                val jsonObject = json
                module.inputParameters.forEach { param ->
                    jsonObject[param]?.let { value ->
                        parameters[param] = value.toString().trim('\'')
                    }
                }
            }

        } catch (e: Exception) {
            // If not JSON, treat as plain text
            log.info("Input is not JSON, treating as plain text")
            parameters["input"] = input
        }
        
        return parameters
    }
}