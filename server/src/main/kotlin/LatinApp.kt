package org.latin.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.eclipse.lmos.arc.agents.ConversationAgent
import org.eclipse.lmos.arc.agents.agents
import org.eclipse.lmos.arc.agents.getAgentByName
import org.latin.server.agents.Agents
import org.latin.server.agents.buildBasicFunctions
import org.latin.server.agents.buildLatinAgent
import org.latin.server.modules.ModuleExecutor
import org.latin.server.modules.ModulesManager
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 *
 */
fun main() {
    // Set the OpenAI API as a System property or environment variable.
    // System.setProperty("OPENAI_API_KEY", "****")

    val modules = ModulesManager()
    val moduleExecutor = ModuleExecutor(modules)
    val events = ConcurrentHashMap<String, suspend (String) -> String>()

    agents(functions = { buildBasicFunctions(moduleExecutor, events) }) { buildLatinAgent() }.also {
        CoroutineScope(Job()).launch {
            val agent = it.getAgentByName(Agents.INITIALISE_MODUL_AGENT) as ConversationAgent
            modules.loadModules(File("../modules"), agent, moduleExecutor)
        }
    }.serve(modules, events = events, moduleExecutor = moduleExecutor)
}
