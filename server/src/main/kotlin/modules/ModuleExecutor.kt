package org.latin.server.modules

import org.eclipse.lmos.arc.agents.AgentFailedException
import org.eclipse.lmos.arc.agents.AgentProvider
import org.eclipse.lmos.arc.agents.ConversationAgent
import org.eclipse.lmos.arc.agents.conversation.AIAgentHandover
import org.eclipse.lmos.arc.agents.conversation.AssistantMessage
import org.eclipse.lmos.arc.agents.conversation.Conversation
import org.eclipse.lmos.arc.agents.conversation.latest
import org.eclipse.lmos.arc.agents.conversation.toConversation
import org.eclipse.lmos.arc.agents.getAgentByName
import org.eclipse.lmos.arc.core.Result
import org.eclipse.lmos.arc.core.map
import org.latin.server.agents.Agents
import org.latin.server.events.EventHub
import org.latin.server.events.HandoverEvent
import org.latin.server.events.TriggerModuleEvent
import org.slf4j.LoggerFactory
import java.util.UUID

/**
 * Executes modules in the context of a given `ConversationAgent`.
 *
 * This class manages the execution of modules, detects handover instructions
 * in the agent's output, and delegates to other modules if necessary.
 *
 */
class ModuleExecutor(private val eventHub: EventHub, private val agentProvider: AgentProvider) {
    private val log = LoggerFactory.getLogger(ModulesManager::class.java)

    @Deprecated("Use 'run' instead. This method will be removed in future versions.")
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
                    eventHub.publish(
                        HandoverEvent(
                            fromEvent = handover,
                            toEvent = handover,
                            correlationId = UUID.randomUUID().toString(),
                        ),
                    )
                    eventHub.publishTrigger(
                        TriggerModuleEvent(
                            event = handover,
                            input = input,
                            correlationId = UUID.randomUUID().toString(),
                        ),
                    )
                } else {
                    output
                }
            }
    }

    suspend fun run(module: LatinModule, input: String = ""): Result<Conversation, AgentFailedException> {
        log.info("Running module: ${module.name}")
        val agent = agentProvider.getAgentByName(Agents.RUN_MODUL_AGENT) as ConversationAgent
        return agent.execute(input.toConversation(), context = setOf(module))
    }
}
