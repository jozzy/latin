package org.latin.server.modules


data class LatinModule(
    val name: String,
    val version: String,
    val description: String,
    val endpoints: Set<String>,
    val instructions: String
)