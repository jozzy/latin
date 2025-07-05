package org.latin.server.modules

import org.eclipse.lmos.arc.agents.AgentFailedException
import org.eclipse.lmos.arc.agents.AgentProvider
import org.eclipse.lmos.arc.agents.ConversationAgent
import org.eclipse.lmos.arc.agents.conversation.AssistantMessage
import org.eclipse.lmos.arc.agents.conversation.Conversation
import org.eclipse.lmos.arc.agents.conversation.UserMessage
import org.eclipse.lmos.arc.agents.conversation.latest
import org.eclipse.lmos.arc.agents.conversation.toConversation
import org.eclipse.lmos.arc.agents.dsl.extensions.info
import org.eclipse.lmos.arc.agents.dsl.get
import org.eclipse.lmos.arc.agents.getAgentByName
import org.eclipse.lmos.arc.core.Result
import org.eclipse.lmos.arc.core.getOrThrow
import org.eclipse.lmos.arc.core.map
import org.eclipse.lmos.arc.core.onFailure
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import kotlin.toString

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
                modules[moduleFile.nameWithoutExtension] = module
                log.info("Loaded module: $module")
                if (module.endpoints.isEmpty()) {
                    agent.runModule(module = module, modulesManager = this).getOrThrow()
                }
            }
        }
    }

    fun getModuleByEndpoint(endpoint: String): LatinModule? {
        return modules.values.firstOrNull { endpoint in it.endpoints }
    }

    fun getModuleByName(name: String): LatinModule? {
        return modules[name]
    }

    fun list(): List<LatinModule> {
        return modules.values.toList()
    }
}


private val handover = "<HANDOVER:(.*?)>".toRegex(RegexOption.IGNORE_CASE)
private val log = LoggerFactory.getLogger(ModulesManager::class.java)
suspend fun ConversationAgent.runModule(
    module: LatinModule,
    input: String = "",
    modulesManager: ModulesManager,
): Result<String, AgentFailedException> {
    return this.execute(input.toConversation(), context = setOf(module))
        .map { it.latest<AssistantMessage>()?.content ?: "" }
        .map { content ->
            val handovers = handover.findAll(content).map { it.groupValues[1] }
                .filter { it.isNotBlank() }
                .toSet()
            if (handovers.isNotEmpty()) {
                log.info("Handover detected: $handovers")
                val module = modulesManager.getModuleByName(handovers.first())!!
                runModule(input = input, module = module, modulesManager = modulesManager).getOrThrow()
            } else {
                content
            }
        }
}
