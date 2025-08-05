package org.latin.server.flows

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.eclipse.lmos.arc.agents.events.EventHandler
import org.latin.server.events.EventHub
import org.latin.server.events.TriggerFlowEvent
import org.slf4j.LoggerFactory

class ConnectEventsToFlows(
    private val flowRunner: FlowRunner,
    private val flowRepository: FlowRepository,
    private val eventHub: EventHub,
) {

    private val scope = CoroutineScope(SupervisorJob())
    private val log = LoggerFactory.getLogger(javaClass)

    fun connect(): ConnectEventsToFlows {
        log.info("Connecting events to flows...")
        eventHub.add(object : EventHandler<TriggerFlowEvent> {
            override fun onEvent(event: TriggerFlowEvent) {
                scope.launch {
                    val flow = flowRepository.fetch(event.flowId) ?: error("Unknown flow id: ${event.flowId}")
                    flowRunner.run(flow, event.input, event.correlationId)
                }
            }
        })
        return this
    }
}
