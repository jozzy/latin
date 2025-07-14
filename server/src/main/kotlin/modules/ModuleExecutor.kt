package org.latin.server.modules

import org.eclipse.lmos.arc.agents.AgentFailedException
import org.eclipse.lmos.arc.agents.ConversationAgent
import org.eclipse.lmos.arc.agents.conversation.AIAgentHandover
import org.eclipse.lmos.arc.agents.conversation.AssistantMessage
import org.eclipse.lmos.arc.agents.conversation.latest
import org.eclipse.lmos.arc.agents.conversation.toConversation
import org.eclipse.lmos.arc.core.Result
import org.eclipse.lmos.arc.core.getOrThrow
import org.eclipse.lmos.arc.core.map
import org.latin.server.modules.storage.ExecutionData
import org.latin.server.modules.storage.ExecutionStorage
import org.slf4j.LoggerFactory

/**
 * Executes modules in the context of a given `ConversationAgent`.
 *
 * This class manages the execution of modules, detects handover instructions
 * in the agent's output, and delegates to other modules if necessary.
 *
 * @constructor Creates a new `ModuleExecutor` with the specified `ModulesManager`.
 * @param modulesManager Manager for available modules.
 */
class ModuleExecutor(
    private val modulesManager: ModulesManager,
    private val executionStorage: ExecutionStorage,
    private val events: Map<String, suspend (String) -> String>
) {
    private val log = LoggerFactory.getLogger(ModulesManager::class.java)

    suspend fun runModule(
        agent: ConversationAgent,
        module: LatinModule,
        input: String = "",
    ): Result<String, AgentFailedException> {
        return agent.execute(input.toConversation(), context = setOf(module))
            .map { conversation ->
                val output = conversation.latest<AssistantMessage>()?.content ?: ""
                val handover = conversation.classification.let { if (it is AIAgentHandover) it.name else null }

                if (handover != null) {
                    log.info("Handover detected: $handover in content: $output")
                    events[handover]?.let { command -> command(output) } ?: run {
                        val module = modulesManager.getModuleByName(handover)!!
                        runModule(agent, input = input, module = module).getOrThrow()
                    }
                } else {
                    executionStorage.save(
                        ExecutionData(
                            module = module,
                            agent = agent.name,
                            output = output,
                            input = input,
                        )
                    )
                    output
                }
            }
    }
}
