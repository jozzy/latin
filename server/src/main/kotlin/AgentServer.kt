package org.latin.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eclipse.lmos.arc.agents.AgentProvider
import org.eclipse.lmos.arc.agents.ConversationAgent
import org.eclipse.lmos.arc.agents.agents
import org.eclipse.lmos.arc.agents.dsl.AllTools
import org.eclipse.lmos.arc.agents.dsl.extensions.info
import org.eclipse.lmos.arc.agents.dsl.get
import org.eclipse.lmos.arc.agents.getAgentByName
import org.eclipse.lmos.arc.core.getOrThrow
import org.eclipse.lmos.arc.core.onFailure
import org.latin.server.modules.LatinModule
import org.latin.server.modules.ModulesManager
import org.latin.server.modules.runModule
import java.io.File

/**
 *
 */
fun main() {
    // Set the OpenAI API as a System property or environment variable.
    // System.setProperty("OPENAI_API_KEY", "****")

    val modules = ModulesManager()

    agents(functions = {
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
    }
    ) {
        agent {
            name = "latin-agent"
            model { "gpt-4o" }
            tools = AllTools
            prompt {
                val module = get<LatinModule>()
                info("Processing module: ${module.name}")
                """
                 ## Goal
                 You are a workflow engine that can execute tasks based on user input.
                
                 ## Instructions
                 - Read the following instructions carefully and perform the tasks as described.
                 - Use the input provided by the user as input for the tasks.
                 
                 Instructions:
                 ${module.instructions}
                """
            }
        }
    }.also {
        CoroutineScope(Job()).launch {
            val agent = it.getAgentByName("latin-agent") as ConversationAgent
            modules.loadModules(File("../modules"), agent)
        }
    }.serve(modules)
}

