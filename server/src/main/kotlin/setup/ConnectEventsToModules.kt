package org.latin.server.setup

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.eclipse.lmos.arc.agents.events.EventHandler
import org.eclipse.lmos.arc.core.getOrThrow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.latin.server.events.EventHub
import org.latin.server.events.TriggerEvent
import org.latin.server.events.TriggerResultEvent
import org.latin.server.modules.ModuleExecutor
import org.latin.server.modules.ModulesManager
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.getValue
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
            log.info("Connecting trigger events to modules...")
            eventHub.add(object : EventHandler<TriggerEvent> {
                override fun onEvent(event: TriggerEvent) {
                    modules.getModuleByTrigger(event.event).forEach { module ->
                        scope.launch {
                            val result: String
                            val timing = measureTime {
                                result = moduleExecutor.runModule(module = module, input = event.input)
                                    .getOrThrow()
                            }
                            eventHub.publish(
                                TriggerResultEvent(
                                    id = event.id,
                                    event = event.event,
                                    output = result,
                                    duration = timing,
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
