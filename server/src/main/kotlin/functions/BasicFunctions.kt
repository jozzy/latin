package org.latin.server.functions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eclipse.lmos.arc.agents.conversation.AIAgentHandover
import org.eclipse.lmos.arc.agents.dsl.FunctionDefinitionContext
import org.eclipse.lmos.arc.agents.dsl.extensions.breakWith
import org.eclipse.lmos.arc.agents.dsl.extensions.info
import org.eclipse.lmos.arc.agents.dsl.get
import org.eclipse.lmos.arc.core.getOrThrow
import org.eclipse.lmos.arc.core.onFailure
import org.latin.server.modules.ModuleExecutor
import org.latin.server.modules.parseModule

fun FunctionDefinitionContext.buildBasicFunctions() {
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
            val moduleExecutor = get<ModuleExecutor>()
            val result = moduleExecutor.runModule(
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
        name = "handover_flow",
        description = "Hands the task over to another flow. A handover is denoted with the # symbol, for example #flow_name",
        params = types(
            string("name", "The name of the flow to handover the task to"),
        ),
    ) { (name) ->
        info("Handing over to module: $name")
        breakWith(
            message = "Handing over to module $name",
            reason = "Handing over to module $name",
            classification = AIAgentHandover(name.toString().replace("#", "").replace(""""""", "").trim()),
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
