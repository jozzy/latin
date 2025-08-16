package org.latin.server.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.eclipse.lmos.arc.agents.events.Event
import org.slf4j.MDC
import java.time.Instant
import java.util.*
import kotlin.time.Duration

@Serializable
sealed interface LatinEvent : Event {
    val id: String
}

class BaseLatinEvent(
    override val id: String = UUID.randomUUID().toString(),
    override val timestamp: Instant = Instant.now(),
    override val context: Map<String, String> = MDC.getCopyOfContextMap() ?: emptyMap(),
) : LatinEvent

@Serializable
sealed interface CompletedEvent : LatinEvent {
    override val id: String
    val correlationId: String
    val triggerId: String
    val input: String
    val output: String
    val duration: Duration
}

@Serializable
sealed interface TriggerEvent : LatinEvent {
    override val id: String
    val correlationId: String
    val input: String
}

@Serializable
data class TriggerModuleEvent(
    val moduleId: String,
    override val input: String,
    override val correlationId: String,
) : LatinEvent by BaseLatinEvent(), TriggerEvent

@Serializable
data class HandoverEvent(
    val fromModule: String,
    val toModule: String,
    val correlationId: String,
) : LatinEvent by BaseLatinEvent()

@Serializable
data class TriggerFlowEvent(
    val flowId: String,
    override val input: String,
    override val correlationId: String,
) : LatinEvent by BaseLatinEvent(), TriggerEvent

@Serializable
@SerialName("ModuleCompletedEvent")
data class ModuleCompletedEvent(
    val moduleId: String,
    override val input: String,
    override val triggerId: String,
    override val output: String,
    override val correlationId: String,
    override val duration: Duration,
) : LatinEvent by BaseLatinEvent(), CompletedEvent

@Serializable
data class FlowTriggeredEvent(
    val flowId: String,
    val correlationId: String,
    val input: String,
) : LatinEvent by BaseLatinEvent()

@Serializable
@SerialName("FlowCompletedEvent")
data class FlowCompletedEvent(
    val flowId: String,
    override val correlationId: String,
    override val triggerId: String,
    override val input: String,
    override val output: String,
    override val duration: Duration,
) : LatinEvent by BaseLatinEvent(), CompletedEvent
