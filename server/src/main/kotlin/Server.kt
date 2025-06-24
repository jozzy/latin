package org.latin.server

import io.ktor.http.ContentType.Application.Json
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.ServiceUnavailable
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respondText
import io.ktor.server.cio.*
import io.ktor.server.request.receiveText
import io.ktor.server.routing.RoutingRoot
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.serialization.json.Json
import org.eclipse.lmos.arc.agents.ArcAgents
import org.eclipse.lmos.arc.agents.ConversationAgent
import org.eclipse.lmos.arc.agents.agent.ask
import org.eclipse.lmos.arc.agents.agent.health
import org.eclipse.lmos.arc.agents.getAgentByName
import org.eclipse.lmos.arc.core.getOrThrow
import org.eclipse.lmos.arc.graphql.InjectToolsFromRequest
import org.eclipse.lmos.arc.graphql.inbound.AgentQuery
import org.eclipse.lmos.arc.graphql.inbound.AgentSubscription
import org.eclipse.lmos.arc.graphql.inbound.EventSubscription
import org.eclipse.lmos.arc.graphql.inbound.EventSubscriptionHolder
import org.eclipse.lmos.arc.server.ktor.AgentCard
import org.eclipse.lmos.arc.server.ktor.Capabilities
import org.eclipse.lmos.arc.server.ktor.EnvConfig

fun ArcAgents.serve(
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
                val result = agent.ask(call.receiveText()).getOrThrow()
                call.respondText(result) // Assuming the result is a String, adjust as necessary
            }
        }
    }.start(wait = wait)
}
