package org.latin.server.flows

import org.latin.server.events.EventHub
import org.latin.server.events.FlowTriggeredEvent
import org.latin.server.events.TriggerEvent
import org.slf4j.LoggerFactory

class FlowRunner(val eventHub: EventHub) {

    private val log = LoggerFactory.getLogger(this::class.java)

    suspend fun run(flow: LatinFlow, input: String): String {
        log.info("Starting Flow: ${flow.id} Steps: ${flow.steps} Input: $input")
        eventHub.publish(FlowTriggeredEvent(correlationId = flow.id, input = input))
        var currentInput = input
        flow.steps.forEach { event ->
            currentInput = eventHub.publishTrigger(
                TriggerEvent(correlationId = flow.id, event = event, input = currentInput),
            )
        }
        return currentInput
    }
}
