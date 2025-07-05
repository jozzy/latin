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

class ModuleExecutor(
    private val modulesManager: ModulesManager,
) {
    private val handover = "<HANDOVER:(.*?)>".toRegex(RegexOption.IGNORE_CASE)
    private val log = LoggerFactory.getLogger(ModulesManager::class.java)

    suspend fun runModule(
        agent: ConversationAgent,
        module: LatinModule,
        input: String = "",
    ): Result<String, AgentFailedException> {
        return agent.execute(input.toConversation(), context = setOf(module))
            .map { it.latest<AssistantMessage>()?.content ?: "" }
            .map { content ->
                val handovers = handover.findAll(content).map { it.groupValues[1] }
                    .filter { it.isNotBlank() }
                    .toSet()
                if (handovers.isNotEmpty()) {
                    log.info("Handover detected: $handovers")
                    val module = modulesManager.getModuleByName(handovers.first())!!
                    runModule(agent, input = input, module = module).getOrThrow()
                } else {
                    content
                }
            }
    }
}