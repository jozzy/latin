package org.latin.server

import io.ktor.http.ContentType.Application.Json
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.ServiceUnavailable
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
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
import org.koin.ktor.ext.inject
import org.latin.server.events.EventHub
import org.latin.server.events.TriggerEvent
import org.latin.server.modules.LatinModule
import org.latin.server.modules.ModulesManager

/**
 * Starts the Ktor server with the specified modules and event hub.
 *
 * @param modules The modules manager containing the loaded modules.
 * @param eventHub The event hub for publishing and subscribing to events.
 * @param wait If true, the server will block the current thread until it is stopped.
 * @param port The port on which the server will listen. Defaults to `EnvConfig.serverPort`.
 */
fun ArcAgents.serve(wait: Boolean = true, port: Int? = null) {
    val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    embeddedServer(CIO, port = port ?: EnvConfig.serverPort) {
        val modules: ModulesManager by inject()
        val eventHub: EventHub by inject()

        install(SSE)

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
                            status = if (modules.isReady) "READY" else "LOADING",
                            triggers = modules.list().flatMap { it.triggers }.toSet(),
                            modules = modules.list(),
                        ),
                    ),
                )
            }

            sse("/runs") {
                eventHub.flow.collect { event ->
                    log.info("Relaying event: ${event.id} to sse clients")
                    send(ServerSentEvent(id = event.id, data = event.output, event = event::class.simpleName))
                }
            }

            post("/events/*") {
                val event = call.request.uri.substringAfterLast("/events/")
                val result = eventHub.publishTrigger(TriggerEvent(event, input = call.receiveText()))
                call.respondText(result)
            }
        }
    }.start(wait = wait)
}

@Serializable
data class Status(val status: String, val triggers: Set<String>, val modules: List<LatinModule>)
