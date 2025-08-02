package org.latin.server.modules

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.eclipse.lmos.arc.agents.events.EventHandler
import org.eclipse.lmos.arc.core.getOrThrow
import org.latin.server.events.EventHub
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
                    modules.getModuleByTrigger(event.event).forEach { module ->
                        scope.launch {
                            val result: String
                            val timing = measureTime {
                                result = moduleExecutor.runModule(module = module, input = event.input)
                                    .getOrThrow()
                            }
                            eventHub.publish(
                                ModuleCompletedEvent(
                                    correlationId = event.correlationId,
                                    event = event.event,
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
