package org.latin.server.events

import org.eclipse.lmos.arc.agents.events.BaseEvent
import org.eclipse.lmos.arc.agents.events.Event
import java.util.*
import kotlin.time.Duration

data class TriggerEvent(
    val event: String,
    val input: String,
    val id: String = UUID.randomUUID().toString(),
) : Event by BaseEvent()

data class TriggerResultEvent(
    val event: String,
    val output: String,
    val id: String,
    val duration: Duration,
) : Event by BaseEvent()
