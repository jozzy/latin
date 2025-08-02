package org.latin.server.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.eclipse.lmos.arc.agents.events.BaseEvent
import org.eclipse.lmos.arc.agents.events.Event
import java.util.*
import kotlin.time.Duration

@Serializable
sealed interface CompletedEvent : Event {
    val id: String
    val correlationId: String
    val triggerId: String
    val input: String
    val output: String
    val duration: Duration
}

@Serializable
sealed interface TriggerEvent : Event {
    val id: String
    val correlationId: String
    val input: String
}

@Serializable
data class TriggerModuleEvent(
    override val id: String = UUID.randomUUID().toString(),
    val event: String,
    override val input: String,
    override val correlationId: String,
) : Event by BaseEvent(), TriggerEvent

@Serializable
data class TriggerFlowEvent(
    override val id: String = UUID.randomUUID().toString(),
    val flowId: String,
    override val input: String,
    override val correlationId: String,
) : Event by BaseEvent(), TriggerEvent

@Serializable
@SerialName("ModuleCompletedEvent")
data class ModuleCompletedEvent(
    override val id: String = UUID.randomUUID().toString(),
    val event: String,
    override val input: String,
    override val triggerId: String,
    override val output: String,
    override val correlationId: String,
    override val duration: Duration,
) : Event by BaseEvent(), CompletedEvent

@Serializable
data class FlowTriggeredEvent(
    val id: String = UUID.randomUUID().toString(),
    val flowId: String,
    val correlationId: String,
    val input: String,
) : Event by BaseEvent()

@Serializable
@SerialName("FlowCompletedEvent")
data class FlowCompletedEvent(
    override val id: String = UUID.randomUUID().toString(),
    val flowId: String,
    override val correlationId: String,
    override val triggerId: String,
    override val input: String,
    override val output: String,
    override val duration: Duration,
) : Event by BaseEvent(), CompletedEvent
