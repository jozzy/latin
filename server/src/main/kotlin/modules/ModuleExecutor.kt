package org.latin.server.modules

import org.eclipse.lmos.arc.agents.AgentFailedException
import org.eclipse.lmos.arc.agents.AgentProvider
import org.eclipse.lmos.arc.agents.ConversationAgent
import org.eclipse.lmos.arc.agents.conversation.AIAgentHandover
import org.eclipse.lmos.arc.agents.conversation.AssistantMessage
import org.eclipse.lmos.arc.agents.conversation.latest
import org.eclipse.lmos.arc.agents.conversation.toConversation
import org.eclipse.lmos.arc.agents.getAgentByName
import org.eclipse.lmos.arc.core.Result
import org.eclipse.lmos.arc.core.map
import org.latin.server.agents.Agents
import org.latin.server.events.EventHub
import org.latin.server.events.TriggerEvent
import org.slf4j.LoggerFactory

/**
 * Executes modules in the context of a given `ConversationAgent`.
 *
 * This class manages the execution of modules, detects handover instructions
 * in the agent's output, and delegates to other modules if necessary.
 *
 */
class ModuleExecutor(private val eventHub: EventHub, private val agentProvider: AgentProvider) {
    private val log = LoggerFactory.getLogger(ModulesManager::class.java)

    suspend fun runModule(
        module: LatinModule,
        input: String = "",
    ): Result<String, AgentFailedException> {
        log.info("Running module: ${module.name}")
        val agent = agentProvider.getAgentByName(Agents.RUN_MODUL_AGENT) as ConversationAgent
        return agent.execute(input.toConversation(), context = setOf(module))
            .map { conversation ->
                val output = conversation.latest<AssistantMessage>()?.content ?: ""
                val handover = conversation.classification.let { if (it is AIAgentHandover) it.name else null }
                if (handover != null) {
                    log.info("Handover detected: $handover in content: $output")
                    eventHub.publishTrigger(TriggerEvent(handover, input = input))
                } else {
                    output
                }
            }
    }
}
