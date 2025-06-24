package org.latin.server

import kotlinx.coroutines.runBlocking
import org.eclipse.lmos.arc.agents.agents
import org.eclipse.lmos.arc.server.ktor.*

/**
 *
 */
fun main(): Unit = runBlocking {
    // Set the OpenAI API as a System property or environment variable.
    // System.setProperty("OPENAI_API_KEY", "****")
    agents(
        functions = {
            function(
                name = "get_weather",
                description = "Get the weather for a given location.",
            ) {
                "THe weather is sunny"
            }
        },
    ) {
        agent {
            name = "MyAgent"
            model { "gpt-4o" }
            prompt {
                """
                You are a weather assistant. Help the user with their questions about the weather.
                """
            }
        }
    }.serve(devMode = true)
}
