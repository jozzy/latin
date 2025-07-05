package org.latin.server.modules

import kotlinx.serialization.Serializable

@Serializable
data class LatinModule(
    val name: String,
    val version: String,
    val description: String,
    val endpoints: Set<String>,
    val instructions: String,
    val outputs: Set<String>? = null,
)
