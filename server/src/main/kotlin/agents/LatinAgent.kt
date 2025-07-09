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
            +"call_module"
        }
        prompt {
            val module = get<LatinModule>()
            info("Processing module: ${module.name} (UseCase: ${module.useCase})")
            
            val parametersInfo = if (module.inputParameters.isNotEmpty()) {
                "\n\nExpected Parameters: ${module.inputParameters.joinToString(", ")}"
            } else ""
            
            val startConditionInfo = if (module.startCondition != null) {
                "\n\nStart Condition: ${module.startCondition}"
            } else ""
            
            """
         ## Goal
         You are a workflow engine that can execute tasks based on user input.
        
         ## Module Information
         - Name: ${module.name}
         - UseCase: ${module.useCase}
         - Description: ${module.description ?: "No description provided"}
         - Version: ${module.version}$parametersInfo$startConditionInfo
         
         ## Instructions
         - Read the following instructions carefully and setup the workflow using the available functions.
         - Define tasks using natural language.
         - Use the call_module function to invoke other modules when needed.
         - Use register_event_callback for event-driven workflows.
         
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
            info("Processing module: ${module.name} (UseCase: ${module.useCase})")
            setLocal("module", module)
            
            val parametersInfo = if (module.inputParameters.isNotEmpty()) {
                "\n\nExpected Parameters: ${module.inputParameters.joinToString(", ")}"
            } else ""
            
            val startConditionInfo = if (module.startCondition != null) {
                "\n\nStart Condition: ${module.startCondition}"
            } else ""
            
            val outputsInfo = if (module.outputs != null) {
                "\n\nExpected Outputs: ${module.outputs.joinToString(", ")}"
            } else ""
            
            """
         ## Goal
         You are a workflow engine that can execute tasks based on user input.
        
         ## Module Information
         - Name: ${module.name}
         - UseCase: ${module.useCase}
         - Description: ${module.description ?: "No description provided"}
         - Version: ${module.version}$parametersInfo$startConditionInfo$outputsInfo
         
         ## Instructions
         - Read the following instructions carefully and perform the tasks as described.
         - Use the input provided by the user as input for the tasks.
         - Use the handover_flow function whenever a handover is specified and return the result.
         - Use register_event_callback whenever an event or trigger is specified using the @ symbol.
         - Use call_module to invoke other Latin modules when needed.
         - When responding, use @respond format if specified in the instructions.
         
         Instructions:
         ${module.instructions}
        """
        }
    }
}
