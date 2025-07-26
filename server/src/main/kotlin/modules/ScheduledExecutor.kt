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
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService


/**
 * TODO
 */
class ScheduledExecutor(private val eventHub: EventHub, private val agentProvider: AgentProvider) {
    private val log = LoggerFactory.getLogger(ModulesManager::class.java)

    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

    suspend fun runModule(
        module: LatinModule,
        input: String = "",
    ) {
        log.info("Schedule module: ${module.name}")

    }
}