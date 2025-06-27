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
import kotlinx.serialization.json.Json
import org.eclipse.lmos.arc.agents.ArcAgents
import org.eclipse.lmos.arc.agents.ConversationAgent
import org.eclipse.lmos.arc.agents.agent.health
import org.eclipse.lmos.arc.agents.getAgentByName
import org.eclipse.lmos.arc.core.getOrThrow
import org.eclipse.lmos.arc.graphql.inbound.EventSubscriptionHolder
import org.eclipse.lmos.arc.server.ktor.EnvConfig
import org.latin.server.modules.ModulesManager
import org.latin.server.modules.runModule

fun ArcAgents.serve(
    modulesManager: ModulesManager,
    wait: Boolean = true,
    port: Int? = null,
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

            post("/events/*") {
                val agent = getAgentByName("latin-agent") as ConversationAgent
                val module = modulesManager.getModuleByEndpoint(call.request.uri.substringAfterLast("/events/"))
                    ?: error("Could not find module")
                val result = agent.runModule(module, input = call.receiveText()).getOrThrow()
                call.respondText(result)
            }
        }
    }.start(wait = wait)
}
