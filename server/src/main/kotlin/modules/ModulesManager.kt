package org.latin.server.modules

import org.eclipse.lmos.arc.agents.AgentFailedException
import org.eclipse.lmos.arc.agents.ConversationAgent
import org.eclipse.lmos.arc.agents.conversation.AssistantMessage
import org.eclipse.lmos.arc.agents.conversation.latest
import org.eclipse.lmos.arc.agents.conversation.toConversation
import org.eclipse.lmos.arc.core.Result
import org.eclipse.lmos.arc.core.getOrThrow
import org.eclipse.lmos.arc.core.map
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class ModulesManager {

    private val log = LoggerFactory.getLogger(ModulesManager::class.java)

    private val modules = ConcurrentHashMap<String, LatinModule>()

    suspend fun loadModules(folder: File, agent: ConversationAgent) {
        log.info("Loading modules from : ${folder.absoluteFile}")
        val moduleFiles = folder.listFiles() ?: return
        moduleFiles.forEach { moduleFile ->
            if (moduleFile.isFile) {
                log.info("Loading module: ${moduleFile.name}")
                val module = parseModuleFile(moduleFile)
                modules[moduleFile.name] = module
                log.info("Loaded module: $module")
                if (module.endpoints.isEmpty()) {
                    agent.runModule(module = module).getOrThrow()
                }
            }
        }
    }

    fun getModuleByEndpoint(endpoint: String): LatinModule? {
        return modules.values.firstOrNull { endpoint in it.endpoints }
    }
}

suspend fun ConversationAgent.runModule(
    module: LatinModule,
    input: String = "",
): Result<String, AgentFailedException> {
    return this.execute(input.toConversation(), context = setOf(module))
        .map { it.latest<AssistantMessage>()?.content ?: "" }
}