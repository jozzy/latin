package org.latin.server

import org.eclipse.lmos.arc.agents.agents
import org.eclipse.lmos.arc.agents.dsl.get
import org.latin.server.modules.ModulesManager
import java.io.File

/**
 *
 */
fun main() {
    // Set the OpenAI API as a System property or environment variable.
    // System.setProperty("OPENAI_API_KEY", "****")

    val modules = ModulesManager().also { it.loadModules(File("../modules")) }

    agents {
        agent {
            name = "latin-agent"
            model { "gpt-4o" }
            prompt {
                val endpoint = get<ModuleRequest>().endpoint
                val module = modules.getModuleByEndpoint(endpoint)
                """
                 ## Goal
                 You are a workflow engine that can execute tasks based on user input.
                
                 ## Instructions
                 - Read the following instructions carefully and perform the tasks as described.
                 - Use the input provided by the user as input for the tasks.
                 
                 Instructions:
                 ${module?.instructions}
                """
            }
        }
    }.serve()
}


