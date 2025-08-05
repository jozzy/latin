package org.latin.server.agents

import org.eclipse.lmos.arc.agents.AgentProvider
import org.eclipse.lmos.arc.agents.ArcAgents
import org.eclipse.lmos.arc.agents.agents
import org.koin.dsl.binds
import org.koin.dsl.module
import org.latin.server.events.EventHub
import org.latin.server.functions.buildBasicFunctions
import org.latin.server.functions.buildEmailFunctions
import org.latin.server.functions.buildMockFunctions

val agentsModule = module {

    single(createdAtStart = true) {
        agents(
            eventPublisher = get<EventHub>(),
            functions = {
                buildBasicFunctions()
                buildEmailFunctions()
                buildMockFunctions()
            },
        ) {
            buildLatinAgent()
        }
    }.binds(arrayOf(AgentProvider::class, ArcAgents::class))
}
