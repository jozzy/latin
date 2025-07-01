package org.latin.server.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eclipse.lmos.arc.agents.AgentProvider
import org.eclipse.lmos.arc.agents.ConversationAgent
import org.eclipse.lmos.arc.agents.dsl.FunctionDefinitionContext
import org.eclipse.lmos.arc.agents.dsl.extensions.info
import org.eclipse.lmos.arc.agents.dsl.get
import org.eclipse.lmos.arc.agents.getAgentByName
import org.eclipse.lmos.arc.core.getOrThrow
import org.eclipse.lmos.arc.core.onFailure
import org.latin.server.modules.LatinModule
import org.latin.server.modules.runModule
import java.util.concurrent.ConcurrentHashMap


fun FunctionDefinitionContext.buildBasicFunctions(eventListeners: ConcurrentHashMap<String, suspend (String) -> String>) {
    function(
        name = "set_timer", description = "Sets a timer to perform a task after a specified duration",
        params = types(
            string("duration", "The duration for the timer in seconds"),
            string("task", "The task to perform after the timer expires")
        )
    ) { (duration, task) ->
        info("Setting a timer for $duration seconds to perform task: $task")
        CoroutineScope(Job()).launch {
            delay(duration.toString().toLong() * 1000)
            val agent = get<AgentProvider>().getAgentByName("latin-agent") as ConversationAgent
            val result = agent.runModule(
                LatinModule(
                    name = "TimerModule",
                    version = "1.0",
                    description = "Module to handle timer tasks",
                    endpoints = setOf("timer"),
                    instructions = task.toString()
                )
            ).onFailure {
                error("Failed to handle timer task: $it")
            }.getOrThrow().also {
                info("Timer task completed: $task")
            }
            info("Result of timer task: $result")
        }
        "timer set"
    }

    function(
        name = "register_event_callback", description = "Registers a callback for an event",
        params = types(
            string("event", "The name of the event to register for. Denoted with a '@', for example @event_name"),
            string("task", "The task to perform when the event occurs")
        )
    ) { (event, task) ->
        info("Register event callback: $event with task: $task")
        eventListeners[event.toString().substringAfter("@").trim()] = { input ->
            val agent = get<AgentProvider>().getAgentByName("latin-agent") as ConversationAgent
            val result = agent.runModule(
                input = input,
                module = LatinModule(
                    name = "EventCallbackModule",
                    version = "1.0",
                    description = "Module to handle event callbacks",
                    endpoints = setOf(event.toString()),
                    instructions = task.toString()
                )
            ).onFailure {
                error("Failed to handle event callback: $it")
            }.getOrThrow().also {
                info("Event callback executed: $task")
            }
            info("Result of event callback: $result")
            result
        }
        "done"
    }
}
