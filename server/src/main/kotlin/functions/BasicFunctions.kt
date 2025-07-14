package org.latin.server.functions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eclipse.lmos.arc.agents.AgentProvider
import org.eclipse.lmos.arc.agents.ConversationAgent
import org.eclipse.lmos.arc.agents.conversation.AIAgentHandover
import org.eclipse.lmos.arc.agents.dsl.FunctionDefinitionContext
import org.eclipse.lmos.arc.agents.dsl.extensions.breakWith
import org.eclipse.lmos.arc.agents.dsl.extensions.info
import org.eclipse.lmos.arc.agents.dsl.get
import org.eclipse.lmos.arc.agents.getAgentByName
import org.eclipse.lmos.arc.core.getOrThrow
import org.eclipse.lmos.arc.core.onFailure
import org.latin.server.agents.Agents.RUN_MODUL_AGENT
import org.latin.server.modules.ModuleExecutor
import org.latin.server.modules.parseModule
import java.util.concurrent.ConcurrentHashMap

fun FunctionDefinitionContext.buildBasicFunctions(
    moduleExecutor: ModuleExecutor,
    eventListeners: ConcurrentHashMap<String, suspend (String) -> String>,
) {
    function(
        name = "set_timer",
        description = "Sets a timer to perform a task after a specified duration",
        params = types(
            string("duration", "The duration for the timer in seconds"),
            string("task", "The task to perform after the timer expires"),
        ),
    ) { (duration, task) ->
        info("Setting a timer for $duration seconds to perform task: $task")
        CoroutineScope(Job()).launch {
            delay(duration.toString().toLong() * 1000)
            val agent = get<AgentProvider>().getAgentByName(RUN_MODUL_AGENT) as ConversationAgent
            val result = moduleExecutor.runModule(
                agent,
                parseModule("TimerModule", task.toString()).copy(
                    description = "Module to handle timer tasks",
                    triggers = setOf("timer"),
                ),
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
        name = "register_event_callback",
        description = "Registers a callback for an event.",
        params = types(
            string(
                "event",
                "The name of the event to register for. Denoted with a '@trigger', for example '@trigger event_name'. Important: An event name can only be registered once."
            ),
            string("task", "The task to perform when the event occurs. Should be in natural language."),
        ),
    ) { (event, task) ->
        info("Register event callback: $event with task: $task")
        eventListeners[event.toString().substringAfter("@trigger ").substringBefore(" ").trim()] = { input ->
            val agent = get<AgentProvider>().getAgentByName(RUN_MODUL_AGENT) as ConversationAgent
            val result = moduleExecutor.runModule(
                agent,
                input = input,
                module = parseModule("EventCallbackModule", task.toString()).copy(
                    description = "Module to handle event callbacks",
                    triggers = setOf(event.toString()),
                ),
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

    function(
        name = "handover_flow",
        description = "Hands the task over to another flow. A handover is denoted with the # symbol, for example #flow_name",
        params = types(
            string("name", "The name of the flow to handover the task to"),
            string("input", "The input to pass to the flow"),
        ),
    ) { (name, input) ->
        info("Handing over to module: $name with input: $input")
        breakWith(
            input.toString(),
            reason = "Handing over to module $name",
            classification = AIAgentHandover(name.toString())
        )
    }

    function(
        name = "add_numbers",
        description = "Adds two numbers together.",
        params = types(
            integer("numberA", "The first number to add."),
            integer("numberB", "The second number to add."),
        ),
    ) { (a, b) ->
        "${(a as Int) + (b as Int)}"
    }

}
