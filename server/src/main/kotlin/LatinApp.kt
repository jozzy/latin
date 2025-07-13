package org.latin.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.eclipse.lmos.arc.agents.ConversationAgent
import org.eclipse.lmos.arc.agents.agents
import org.eclipse.lmos.arc.agents.getAgentByName
import org.latin.server.agents.Agents
import org.latin.server.agents.buildLatinAgent
import org.latin.server.functions.buildBasicFunctions
import org.latin.server.functions.buildEmailFunctions
import org.latin.server.functions.buildMockFunctions
import org.latin.server.modules.ModuleExecutor
import org.latin.server.modules.ModulesManager
import org.latin.server.modules.storage.InMemoryExecutionStorage
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 *
 */
fun main() {
    // Set the OpenAI API as a System property or environment variable.
    // System.setProperty("OPENAI_API_KEY", "****")

    val modules = ModulesManager()
    val executionStorage = InMemoryExecutionStorage()
    val moduleExecutor = ModuleExecutor(modules, executionStorage)
    val events = ConcurrentHashMap<String, suspend (String) -> String>()

    agents(functions = {
        buildBasicFunctions(moduleExecutor, events)
        buildEmailFunctions(moduleExecutor, events)
        buildMockFunctions(moduleExecutor, events)
    }) {
        buildLatinAgent()
    }.also {
        CoroutineScope(Job()).launch {
            val agent = it.getAgentByName(Agents.INITIALISE_MODUL_AGENT) as ConversationAgent
            modules.loadModules(File("../modules"), agent, moduleExecutor)
        }
    }.serve(
        modules,
        events = events,
        moduleExecutor = moduleExecutor,
        executionStorage = executionStorage
    )
}
