package org.latin.server.modules

import kotlinx.serialization.Serializable

@Serializable
data class LatinModule(
    val name: String,
    val version: String,
    val description: String,
    val output: String? = null,
    val triggers: Set<String>,
    val instructions: String,
    val outputSymbols: Set<String>? = null,
    val handovers: Set<String> = emptySet(),
    val tools: Set<String> = emptySet(),
    val inputTemplate: String? = null,
)
