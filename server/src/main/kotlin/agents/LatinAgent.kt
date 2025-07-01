package org.latin.server.agents

import org.eclipse.lmos.arc.agents.dsl.AgentDefinitionContext
import org.eclipse.lmos.arc.agents.dsl.AllTools
import org.eclipse.lmos.arc.agents.dsl.extensions.info
import org.eclipse.lmos.arc.agents.dsl.get
import org.eclipse.lmos.arc.agents.llm.ChatCompletionSettings
import org.latin.server.modules.LatinModule
import org.latin.server.modules.findOutputSymbols


fun AgentDefinitionContext.buildLatinAgent() {

    agent {
        name = "latin-init-agent"
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
         - Define task using natural language.
         
         Instructions:
         ${module.instructions}
        """
        }
    }

    agent {
        name = "latin-agent"
        model { "gpt-4o" }
        tools = AllTools
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
         
         Instructions:
         ${module.instructions}
        """
        }
    }
}
