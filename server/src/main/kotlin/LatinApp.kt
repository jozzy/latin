package org.latin.server

import org.eclipse.lmos.arc.agents.agents
import org.koin.core.context.startKoin
import org.koin.logger.slf4jLogger
import org.latin.server.agents.buildLatinAgent
import org.latin.server.events.EventHub
import org.latin.server.events.eventModule
import org.latin.server.flow.flowModule
import org.latin.server.functions.buildBasicFunctions
import org.latin.server.functions.buildEmailFunctions
import org.latin.server.functions.buildMockFunctions
import org.latin.server.modules.latinModule
import org.latin.server.setup.setupModule


/**
 * Main entry point for the Latin server application.
 */
fun main() {
    // Set the OpenAI API as a System property or environment variable.
    // System.setProperty("OPENAI_API_KEY", "****")

    val app = startKoin {
        slf4jLogger()
        modules(
            listOf(
                eventModule,
                flowModule,
                latinModule,
                setupModule,
            ),
        )
    }

    agents(
        eventPublisher = app.koin.get<EventHub>(),
        functions = {
            buildBasicFunctions()
            buildEmailFunctions()
            buildMockFunctions()
        },
    ) {
        buildLatinAgent()
    }.serve(wait = true)
}
