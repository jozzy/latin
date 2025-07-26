package org.latin.server.flows

interface FlowRepository {

    suspend fun store(flow: LatinFlow)

    suspend fun fetch(id: String): LatinFlow?
}
