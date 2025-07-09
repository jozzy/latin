package org.latin.server.modules

import kotlinx.serialization.Serializable

@Serializable
data class LatinModule(
    val name: String,
    val useCase: String, // UseCase name, will be set explicitly
    val description: String? = null,
    val version: String = "1.0.0",
    val startCondition: String? = null, // "when recipient and email_body are received"
    val inputParameters: Set<String> = emptySet(), // ["recipient", "email_body"]
    val instructions: String,
    val outputs: Set<String>? = null,
    val channels: Set<String> = setOf("http"), // http, email, kafka, webhook
    val endpoints: Set<String> = emptySet(), // Legacy support for existing modules
)
