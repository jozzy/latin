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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.eclipse.lmos.arc.agents.ArcAgents
import org.eclipse.lmos.arc.agents.ConversationAgent
import org.eclipse.lmos.arc.agents.agent.health
import org.eclipse.lmos.arc.agents.getAgentByName
import org.eclipse.lmos.arc.core.getOrThrow
import org.eclipse.lmos.arc.graphql.inbound.EventSubscriptionHolder
import org.eclipse.lmos.arc.server.ktor.EnvConfig
import org.latin.server.agents.Agents
import org.latin.server.modules.LatinModule
import org.latin.server.modules.ModuleExecutor
import org.latin.server.modules.ModulesManager
import org.latin.server.modules.storage.ExecutionStorage

fun ArcAgents.serve(
    modulesManager: ModulesManager,
    moduleExecutor: ModuleExecutor,
    executionStorage: ExecutionStorage,
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
        encodeDefaults = true
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
                    ),
                )
            }

            get("/runs") {
                call.respondText(
                    json.encodeToString(
                        executionStorage.fetch(100).getOrThrow()
                    ),
                )
            }

            post("/events/*") {
                val event = call.request.uri.substringAfterLast("/events/")
                val result = events[event]!!(call.receiveText())
                call.respondText(result)
            }

            post("/modules/*") {
                val moduleName = call.request.uri.substringAfterLast("/modules/")
                val module = modulesManager.getModuleByName(moduleName)!!
                val agent = getAgentByName(Agents.RUN_MODUL_AGENT) as ConversationAgent
                val result = moduleExecutor.runModule(agent, module, call.receiveText())
                call.respondText(result.getOrThrow())
            }
        }
    }.start(wait = wait)
}

@Serializable
data class Status(val status: String, val event: List<String>, val modules: List<LatinModule>)
