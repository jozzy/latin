package org.latin.server

import org.eclipse.lmos.arc.agents.agents
import org.eclipse.lmos.arc.agents.dsl.extensions.local

/**
 *
 */
fun main() {
    // Set the OpenAI API as a System property or environment variable.
    // System.setProperty("OPENAI_API_KEY", "****")
    agents {
        agent {
            name = "latin-agent"
            model { "gpt-4o" }
            init {
                // TODO load endpoints
            }
            prompt {
                """
                 ## Goal
                 You are a workflow engine that can execute tasks based on user input.
                
                 ## Instructions
                 - Read the following instructions carefully and perform the tasks as described.
                 - Use the input provided by the user as input for the tasks.
                 
                 Instructions:
                 ${local("modules/instructions.latin")}
                """
            }
        }
    }.serve()
}
