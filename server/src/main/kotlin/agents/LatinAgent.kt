package org.latin.server.agents

import org.eclipse.lmos.arc.agents.dsl.AgentDefinitionContext
import org.eclipse.lmos.arc.agents.dsl.extensions.info
import org.eclipse.lmos.arc.agents.dsl.extensions.then
import org.eclipse.lmos.arc.agents.dsl.get
import org.eclipse.lmos.arc.agents.llm.ChatCompletionSettings
import org.latin.server.modules.LatinModule
import org.latin.server.modules.findOutputSymbols

/**
 * Contains constants for the names of the Latin agents.
 */
object Agents {
    /** Name of the initialization agent. */
    const val INITIALISE_MODUL_AGENT = "initialise-modul-agent"

    /** Name of the execution agent. */
    const val RUN_MODUL_AGENT = "run-modul-agent"
}

/**
 * Builds and registers the Latin agents in the current [AgentDefinitionContext].
 *
 * Defines two agents:
 * - INITIALISE_MODUL_AGENT: Initializes a module and prepares tasks.
 * - RUN_MODUL_AGENT: Executes tasks and filters the output.
 */
fun AgentDefinitionContext.buildLatinAgent() {
    agent {
        name = Agents.INITIALISE_MODUL_AGENT
        model { "gpt-4o" }
        settings = { ChatCompletionSettings(temperature = 0.0, seed = 42) }
        tools {
            +"register_event_callback"
            +"set_timer"
        }
        prompt {
            val module = get<LatinModule>()
            info("Processing module: ${module.name}")
            """
         ## Goal
         You are a workflow engine that can execute tasks based on user input.
        
         ## Instructions
         - Read the following instructions carefully and setup the workflow using the available functions.
         - Always define tasks using natural language.
         - Optimize task so that an LLM can understand and execute it.
         
         Instructions:
         ${module.instructions}
        """
        }
    }

    agent {
        name = Agents.RUN_MODUL_AGENT
        model { "gpt-4o" }
        tools {
            val module = get<LatinModule>()
            module.tools.forEach { +it }
        }
        settings = { ChatCompletionSettings(temperature = 0.0, seed = 42) }
        filterOutput {
            val module = get<LatinModule>()
            if (module.outputSymbols != null && module.outputSymbols.isNotEmpty()) {
                info("Filtering output symbols for $message")
                message = message.findOutputSymbols().firstOrNull() ?: "Error"
            }
        }
        prompt {
            val module = get<LatinModule>()
            info("Processing module: ${module.name}")
            setLocal("module", module)
            """
         ## Goal
         You are a workflow engine that can execute tasks based on user input.
        
         ## Instructions
         - Read the following instructions carefully and perform the tasks as described.
         - Use the input provided by the user as input for the tasks.
         
         Instructions:
         ${module.instructions}
         
         ${module.output then "### Output"}
         ${module.output}
        
        """
        }
    }
}
