package org.latin.server

import io.ktor.http.ContentType.Application.Json
import io.ktor.http.HttpStatusCode.Companion.Created
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
import org.koin.core.context.GlobalContext
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.latin.server.events.EventHub
import org.latin.server.events.TriggerEvent
import org.latin.server.flows.FlowRepository
import org.latin.server.flows.FlowRunner
import org.latin.server.flows.LatinFlow
import org.latin.server.modules.LatinModule
import org.latin.server.modules.ModulesManager

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

        install(RoutingRoot) {
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

            sse("/events") {
                eventHub.flow.collect { event ->
                    log.info("Relaying event: ${event.id} to sse clients")
                    send(ServerSentEvent(id = event.id, data = event.output, event = event::class.simpleName))
                }
            }

            post("/trigger/*") {
                val event = call.request.uri.substringAfterLast("/trigger/")
                val result = eventHub.publishTrigger(TriggerEvent(event, input = call.receiveText()))
                call.respondText(result)
            }

            post("/flows") {
                val flowRepository: FlowRepository by injected()
                val flow = json.decodeFromString<LatinFlow>(call.receiveText())
                flowRepository.store(flow)
                call.respond(Created)
            }

            post("/start/*") {
                val flowRunner: FlowRunner by injected()
                val flowRepository: FlowRepository by injected()
                val id = call.request.uri.substringAfterLast("/start/")
                val input = call.receiveText()
                val flow = flowRepository.fetch(id) ?: error("Unknown flow $id")
                val result = flowRunner.run(flow, input)
                call.respondText(result)
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
    noinline parameters: ParametersDefinition? = null
) =
    lazy {
        GlobalContext.getKoinApplicationOrNull()?.koin?.get<T>(qualifier, parameters)
            ?: org.koin.java.KoinJavaComponent.inject<T>(T::class.java).value
    }