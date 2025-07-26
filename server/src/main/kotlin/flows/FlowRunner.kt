package org.latin.server.flows

import org.latin.server.events.EventHub
import org.latin.server.events.FlowTriggeredEvent
import org.latin.server.events.TriggerEvent

class FlowRunner(val eventHub: EventHub) {

    suspend fun run(flow: LatinFlow, input: String): String {
        eventHub.publish(FlowTriggeredEvent(id = flow.id, input = input))
        var currentInput = input
        flow.events.forEach { event ->
            currentInput = eventHub.publishTrigger(
                TriggerEvent(id = flow.id, event = event, input = currentInput),
            )
        }
        return currentInput
    }
}
