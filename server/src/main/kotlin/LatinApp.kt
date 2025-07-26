package org.latin.server

import org.eclipse.lmos.arc.agents.agents
import org.latin.server.agents.buildLatinAgent
import org.latin.server.events.EventHub
import org.latin.server.functions.buildBasicFunctions
import org.latin.server.functions.buildEmailFunctions
import org.latin.server.functions.buildMockFunctions
import org.latin.server.modules.ModuleExecutor
import org.latin.server.modules.ModulesManager
import org.slf4j.LoggerFactory
import reload.ModuleHotReload
import java.io.File

private val log = LoggerFactory.getLogger("LatinApp")

/**
 * Main entry point for the Latin server application.
 */
fun main() {
    // Set the OpenAI API as a System property or environment variable.
    // System.setProperty("OPENAI_API_KEY", "****")

    val eventHub = EventHub()

    val agentSystem = agents(
        eventPublisher = eventHub,
        functions = {
            buildBasicFunctions()
            buildEmailFunctions()
            buildMockFunctions()
        },
    ) {
        buildLatinAgent()
    }

    val modules = ModulesManager()
    val moduleExecutor = ModuleExecutor(eventHub, agentSystem)

    // Load modules from the specified directory.
    ConnectEventsToModules(eventHub = eventHub, modules = modules, moduleExecutor = moduleExecutor)()

    // Hot reload modules from the specified directory.
    log.info("Starting hot reload of modules...")
    ModuleHotReload(modules).start(File("../modules"))

    // Start the agent system with the modules and event hub.
    agentSystem.serve(modules = modules, eventHub = eventHub, wait = true)
}
