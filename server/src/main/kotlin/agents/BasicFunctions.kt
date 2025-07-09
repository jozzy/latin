package org.latin.server.agents

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eclipse.lmos.arc.agents.AgentProvider
import org.eclipse.lmos.arc.agents.ConversationAgent
import org.eclipse.lmos.arc.agents.dsl.FunctionDefinitionContext
import org.eclipse.lmos.arc.agents.dsl.extensions.breakWith
import org.eclipse.lmos.arc.agents.dsl.extensions.info
import org.eclipse.lmos.arc.agents.dsl.get
import org.eclipse.lmos.arc.agents.getAgentByName
import org.eclipse.lmos.arc.core.getOrThrow
import org.eclipse.lmos.arc.core.onFailure
import org.latin.server.modules.LatinModule
import org.latin.server.modules.ModuleExecutor
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
            val agent = get<AgentProvider>().getAgentByName("latin-agent") as ConversationAgent
            val result = moduleExecutor.runModule(
                agent,
                LatinModule(
                    name = "TimerModule",
                    useCase = "timer_task",
                    description = "Module to handle timer tasks",
                    version = "1.0.0",
                    startCondition = null,
                    inputParameters = emptySet(),
                    instructions = task.toString(),
                    outputs = null,
                    channels = setOf("http"),
                    endpoints = setOf("timer")
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
        description = "Registers a callback for an event",
        params = types(
            string("event", "The name of the event to register for. Denoted with a '@', for example @event_name"),
            string("task", "The task to perform when the event occurs"),
        ),
    ) { (event, task) ->
        info("Register event callback: $event with task: $task")
        eventListeners[event.toString().substringAfter("@").trim()] = { input ->
            val agent = get<AgentProvider>().getAgentByName("latin-agent") as ConversationAgent
            val result = moduleExecutor.runModule(
                agent,
                input = input,
                module = LatinModule(
                    name = "EventCallbackModule",
                    useCase = "event_callback",
                    description = "Module to handle event callbacks",
                    version = "1.0.0",
                    startCondition = "when $event event arrives",
                    inputParameters = setOf("input"),
                    instructions = task.toString(),
                    outputs = null,
                    channels = setOf("http"),
                    endpoints = setOf(event.toString())
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
        description = "Hands the task over to another flow.",
        params = types(
            string("name", "The name of the flow to handover the task to"),
            string("input", "A summary of the customers problem."),
        ),
    ) { (name, input) ->
        info("Handing over to module: $name with input: $input")
        breakWith("<HANDOVER:$name>")
    }

    function(
        name = "call_module",
        description = "Calls another Latin module with parameters",
        params = types(
            string("module_name", "The name of the module to call"),
            string("parameters", "JSON string of parameters to pass to the module"),
        ),
    ) { (moduleName, parameters) ->
        info("Calling module: $moduleName with parameters: $parameters")
        
        val agent = get<AgentProvider>().getAgentByName("latin-agent") as ConversationAgent
        val result = moduleExecutor.runModule(
            agent,
            input = parameters.toString(),
            module = LatinModule(
                name = "ModuleCallWrapper",
                useCase = "call_$moduleName",
                description = "Wrapper for calling $moduleName",
                version = "1.0.0",
                startCondition = null,
                inputParameters = emptySet(),
                instructions = "Call module $moduleName with the provided parameters",
                outputs = null,
                channels = setOf("http"),
                endpoints = emptySet()
            ),
        ).onFailure {
            error("Failed to call module: $it")
        }.getOrThrow()
        
        info("Module call result: $result")
        result
    }
}
