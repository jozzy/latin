package org.latin.server.flows

import java.util.concurrent.ConcurrentHashMap

/**
 * In-Memory-Implementierung des [FlowRepository].
 *
 *Stores [LatinFlow] objects in a map in memory.
 */
class InMemoryFlowRepository : FlowRepository {
    private val flows = ConcurrentHashMap<String, LatinFlow>()

    override suspend fun store(flow: LatinFlow) {
        flows[flow.id] = flow
    }

    override suspend fun fetch(id: String): LatinFlow? {
        return flows[id]
    }
}
