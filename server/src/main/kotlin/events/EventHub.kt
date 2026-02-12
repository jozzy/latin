package org.latin.server.events

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import org.eclipse.lmos.arc.agents.events.BasicEventPublisher
import org.eclipse.lmos.arc.agents.events.Event
import org.eclipse.lmos.arc.agents.events.EventHandler
import org.eclipse.lmos.arc.agents.events.EventListeners
import org.eclipse.lmos.arc.agents.events.EventPublisher
import org.eclipse.lmos.arc.agents.events.LoggingEventHandler
import org.slf4j.LoggerFactory

class EventHub : EventPublisher, EventListeners {

    private val handlers = mutableMapOf<String, MutableSharedFlow<String>>()

    private val log = LoggerFactory.getLogger(javaClass)

    private val eventHandler = BasicEventPublisher(LoggingEventHandler())

    val flow = MutableSharedFlow<LatinEvent>(
        replay = 5,
        extraBufferCapacity = 20,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    init {
        eventHandler.add(object : EventHandler<CompletedEvent> {
            override fun onEvent(event: CompletedEvent) {
                handlers[event.triggerId]?.tryEmit(event.output)
            }
        })
    }

    suspend fun publishTrigger(event: TriggerEvent): String {
        val flow = MutableSharedFlow<String>(replay = 1)
        handlers[event.id] = flow
        publish(event)
        return flow.firstOrNull() ?: ""
    }

    override fun publish(event: Event) {
        log.info("Publishing event: $event")

        // Todo open up to all events, not just CompletedEvent and HandoverEvent
        if (event is CompletedEvent || event is HandoverEvent) {
            val emitted = flow.tryEmit(event)
            log.info("Adding to flow event: $event Emitted: $emitted")
        }
        eventHandler.publish(event)
    }

    override fun add(handler: EventHandler<out Event>) {
        eventHandler.add(handler)
    }
}
