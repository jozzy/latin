package org.latin.server.modules

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.eclipse.lmos.arc.agents.conversation.AIAgentHandover
import org.eclipse.lmos.arc.agents.conversation.AssistantMessage
import org.eclipse.lmos.arc.agents.conversation.latest
import org.eclipse.lmos.arc.agents.events.EventHandler
import org.eclipse.lmos.arc.core.getOrThrow
import org.latin.server.events.EventHub
import org.latin.server.events.HandoverEvent
import org.latin.server.events.ModuleCompletedEvent
import org.latin.server.events.TriggerModuleEvent
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.time.measureTime

class ConnectEventsToModules(
    private val modulesFolder: File,
    private val modules: ModulesManager,
    private val moduleExecutor: ModuleExecutor,
    private val eventHub: EventHub,
) {

    private val scope = CoroutineScope(SupervisorJob())
    private val log = LoggerFactory.getLogger(javaClass)

    fun connect(): ConnectEventsToModules {
        scope.launch {
            modules.loadModules(modulesFolder)
            log.info("Connecting events to modules...")
            eventHub.add(object : EventHandler<TriggerModuleEvent> {
                override fun onEvent(event: TriggerModuleEvent) {
                    modules.getModuleById(event.moduleId)?.let { module ->
                        scope.launch {
                            val result: String
                            val timing = measureTime {
                                val conversation = moduleExecutor.run(module = module, input = event.input)
                                    .getOrThrow()
                                val output = conversation.latest<AssistantMessage>()?.content ?: ""
                                val handover =
                                    conversation.classification.let { if (it is AIAgentHandover) it.name else null }
                                result = if (handover != null) {
                                    log.info("Handover detected: $handover in content: $output")
                                    eventHub.publish(
                                        HandoverEvent(
                                            fromModule = event.moduleId,
                                            toModule = handover,
                                            correlationId = event.correlationId,
                                        ),
                                    )
                                    eventHub.publishTrigger(
                                        TriggerModuleEvent(
                                            moduleId = handover,
                                            input = event.input,
                                            correlationId = event.correlationId,
                                        ),
                                    )
                                } else {
                                    output
                                }
                            }
                            eventHub.publish(
                                ModuleCompletedEvent(
                                    correlationId = event.correlationId,
                                    moduleId = event.moduleId,
                                    input = event.input,
                                    output = result,
                                    duration = timing,
                                    triggerId = event.id,
                                ),
                            )
                        }
                    }
                }
            })
        }
        return this
    }
}
