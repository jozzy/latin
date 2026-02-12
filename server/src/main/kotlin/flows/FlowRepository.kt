package org.latin.server.flows

import kotlinx.serialization.Serializable

interface FlowRepository {

    suspend fun store(flow: LatinFlow)

    suspend fun fetch(id: String): LatinFlow?

    suspend fun fetchAll(): Flows
}

@Serializable
data class Flows(
    val flows: List<LatinFlow>,
)
