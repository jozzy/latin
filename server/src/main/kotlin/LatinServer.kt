package org.latin.server

import io.ktor.http.*
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.ServiceUnavailable
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import io.ktor.sse.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.eclipse.lmos.arc.agents.ArcAgents
import org.eclipse.lmos.arc.agents.agent.health
import org.eclipse.lmos.arc.server.ktor.EnvConfig
import org.koin.core.context.GlobalContext
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.latin.server.events.EventHub
import org.latin.server.events.TriggerFlowEvent
import org.latin.server.events.TriggerModuleEvent
import org.latin.server.flows.FlowRepository
import org.latin.server.flows.LatinFlow
import org.latin.server.modules.LatinModule
import org.latin.server.modules.ModulesManager
import org.latin.server.modules.extractTools
import java.util.*

/**
 * Starts the Ktor server with the specified modules and event hub.
 */
fun startServer(modules: List<Module>, wait: Boolean = true, port: Int? = null) {
    val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    embeddedServer(CIO, port = port ?: EnvConfig.serverPort) {
        install(Koin) {
            slf4jLogger()
            modules(modules)
        }
        val eventHub: EventHub by inject()

        install(SSE)

        install(CORS) {
            anyHost()
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Put)
        }

        install(RoutingRoot) {
            staticResources("/ui", "/ui") {
                modify { _, call ->
                    call.response.header("Cross-Origin-Embedder-Policy", "credentialless")
                    call.response.header("Cross-Origin-Opener-Policy", "same-origin")
                }
            }

            // Health endpoint
            get("/health") {
                val agents: ArcAgents by injected()
                val health = agents.health()
                call.respondText(json.encodeToString(health), Json, if (health.ok) OK else ServiceUnavailable)
            }
            // Same implementation for readyness and liveness probes
            get("/health/*") {
                val agents: ArcAgents by injected()
                val health = agents.health()
                call.respondText(json.encodeToString(health), Json, if (health.ok) OK else ServiceUnavailable)
            }

            get("/status") {
                val modules: ModulesManager by injected()
                call.respondText(
                    json.encodeToString(
                        Status(
                            status = if (modules.isReady) "READY" else "LOADING",
                            triggers = modules.list().flatMap { it.triggers }.toSet(),
                            modules = modules.list(),
                        ),
                    ),
                )
            }

            get("/modules") {
                val modules: ModulesManager by injected()
                call.respondText(json.encodeToString(modules.list()), Json, OK)
            }

            put("/modules") {
                val modules: ModulesManager by injected()
                val updatedModule = json.decodeFromString<LatinModule>(call.receiveText())
                modules.store(
                    updatedModule.copy(
                        tools = updatedModule.instructions.extractTools(),
                    ),
                )
                call.respond(OK)
            }

            get("/modules/{name}") {
                val modules: ModulesManager by injected()
                val id = call.parameters["name"] ?: return@get call.respondText(
                    "Missing name",
                    status = BadRequest,
                )
                val module = modules.getByName(id) ?: return@get call.respondText(
                    "Module not found",
                    status = NotFound,
                )
                call.respondText(json.encodeToString(module), Json, OK)
            }

            sse("/events") {
                eventHub.flow.collect { event ->
                    send(
                        ServerSentEvent(
                            id = event.id,
                            data = json.encodeToString(event),
                            event = event::class.simpleName!!,
                        ),
                    )
                }
            }

            post("/trigger/{event}") {
                val event = call.parameters["event"] ?: return@post call.respondText(
                    "Missing event",
                    status = BadRequest,
                )
                val correlationId = UUID.randomUUID().toString()
                eventHub.publish(
                    TriggerModuleEvent(
                        event = event,
                        correlationId = correlationId,
                        input = call.receiveText(),
                    ),
                )
                call.respondText(json.encodeToString(TriggerResponse(correlationId)), Json, Created)
            }

            post("/flows") {
                val flowRepository: FlowRepository by injected()
                val flow = json.decodeFromString<LatinFlow>(call.receiveText())
                flowRepository.store(flow)
                call.respond(Created)
            }

            get("/flows") {
                val flowRepository: FlowRepository by injected()
                val flows = flowRepository.fetchAll()
                call.respondText(json.encodeToString(flows), Json, OK)
            }

            post("/start/{id}") {
                val id = call.parameters["id"] ?: return@post call.respondText("Missing id", status = BadRequest)
                val correlationId = UUID.randomUUID().toString()
                eventHub.publish(
                    TriggerFlowEvent(flowId = id, correlationId = correlationId, input = call.receiveText()),
                )
                call.respondText(json.encodeToString(TriggerResponse(correlationId)), Json, Created)
            }
        }
    }.start(wait = wait)
}

@Serializable
data class Status(val status: String, val triggers: Set<String>, val modules: List<LatinModule>)

/**
 * Fix for Koin `inject` for Ktor 3.0.3 using Koin 4.0.4 in Routes (e.g. in Routing.kt)
 * https://github.com/InsertKoinIO/koin/issues/1716
 */
inline fun <reified T : Any> Routing.injected(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
) =
    lazy {
        GlobalContext.getKoinApplicationOrNull()?.koin?.get<T>(qualifier, parameters)
            ?: org.koin.java.KoinJavaComponent.inject<T>(T::class.java).value
    }

@Serializable
data class TriggerResponse(val correlationId: String)
