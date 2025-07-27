package org.latin.server.events

import org.eclipse.lmos.arc.agents.events.BaseEvent
import org.eclipse.lmos.arc.agents.events.Event
import java.util.*
import kotlin.time.Duration

data class TriggerEvent(
    val id: String = UUID.randomUUID().toString(),
    val event: String,
    val input: String,
    val correlationId: String,
) : Event by BaseEvent()

data class TriggerResultEvent(
    val id: String = UUID.randomUUID().toString(),
    val event: String,
    val output: String,
    val correlationId: String,
    val duration: Duration,
) : Event by BaseEvent()

data class FlowTriggeredEvent(
    val id: String = UUID.randomUUID().toString(),
    val correlationId: String,
    val input: String,
) : Event by BaseEvent()
