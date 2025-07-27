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

    val flow = MutableSharedFlow<TriggerResultEvent>(
        replay = 5,
        extraBufferCapacity = 20,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    init {
        eventHandler.add(object : EventHandler<TriggerResultEvent> {
            override fun onEvent(event: TriggerResultEvent) {
                log.info("Received TriggerResultEvent: $event")
                val emitted = handlers[event.correlationId]?.tryEmit(event.output)
                log.info("TriggerResultEvent emitted: $emitted")
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
        if (event is TriggerResultEvent) {
            val emitted = flow.tryEmit(event)
            log.info("Adding to flow event: $event Emitted: $emitted")
        }
        eventHandler.publish(event)
    }

    override fun add(handler: EventHandler<out Event>) {
        eventHandler.add(handler)
    }
}
