package org.latin.server.modules

import org.eclipse.lmos.arc.agents.AgentProvider
import org.latin.server.events.EventHub
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
