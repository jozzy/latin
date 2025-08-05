package org.latin.server.flows

import org.latin.server.events.EventHub
import org.latin.server.events.FlowCompletedEvent
import org.latin.server.events.FlowTriggeredEvent
import org.latin.server.events.TriggerModuleEvent
import org.slf4j.LoggerFactory
import kotlin.time.measureTime

class FlowRunner(val eventHub: EventHub) {

    private val log = LoggerFactory.getLogger(this::class.java)

    suspend fun run(flow: LatinFlow, input: String, correlationId: String): String {
        log.info("Starting Flow: ${flow.id} Steps: ${flow.steps} Input: $input")
        val triggerEvent = FlowTriggeredEvent(flowId = flow.id, correlationId = correlationId, input = input)
        eventHub.publish(triggerEvent)
        var currentInput = input

        val duration = measureTime {
            try {
                flow.steps.forEach { event ->
                    currentInput = eventHub.publishTrigger(
                        TriggerModuleEvent(correlationId = correlationId, event = event, input = currentInput),
                    )
                }
            } catch (e: Exception) {
                log.error("Error running flow ${flow.id} with input $input", e)
            }
        }
        eventHub.publish(
            FlowCompletedEvent(
                flowId = flow.id,
                correlationId = correlationId,
                input = input,
                output = currentInput,
                duration = duration,
                triggerId = triggerEvent.id,
            ),
        )
        return currentInput
    }
}
