package org.latin.server.agents

import org.eclipse.lmos.arc.agents.dsl.AgentDefinitionContext
import org.eclipse.lmos.arc.agents.dsl.extensions.info
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
            +"set_timer"
            +"register_event_callback"
        }
        prompt {
            val module = get<LatinModule>()
            info("Processing module: ${module.name}")
            """
         ## Goal
         You are a workflow engine that can execute tasks based on user input.
        
         ## Instructions
         - Read the following instructions carefully and setup the workflow using the available functions.
         - Define tasks using natural language.
         
         Instructions:
         ${module.instructions}
        """
        }
    }

    agent {
        name = Agents.RUN_MODUL_AGENT
        model { "gpt-4o" }
        tools {
            if (get<LatinModule>().handovers.isNotEmpty()) +"handover_flow"
            +"add_numbers"
        }
        settings = { ChatCompletionSettings(temperature = 0.0, seed = 42) }
        filterOutput {
            val module = get<LatinModule>()
            if (module.outputs != null) {
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
         - Use the handover_flow function whenever a handover is specified with # and return the result.
         - Use register_event_callback whenever an event or trigger is specified using the @ symbol.
         
         Instructions:
         ${module.instructions}
        """
        }
    }
}
