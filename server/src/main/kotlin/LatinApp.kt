package org.latin.server

import org.latin.server.agents.agentsModule
import org.latin.server.events.eventModule
import org.latin.server.flows.flowModule
import org.latin.server.modules.latinModule
import org.latin.server.setup.setupModule

/**
 * Main entry point for the Latin server application.
 */
fun main() {
    // Set the OpenAI API as a System property or environment variable.
    // System.setProperty("OPENAI_API_KEY", "****")

    startServer(
        listOf(
            eventModule,
            flowModule,
            latinModule,
            setupModule,
            agentsModule,
        ),
        wait = true,
    )
}
