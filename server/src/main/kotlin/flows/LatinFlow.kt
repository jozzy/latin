package org.latin.server.flows

import kotlinx.serialization.Serializable

@Serializable
data class LatinFlow(val id: String, val name: String, val description: String, val steps: List<String>)
