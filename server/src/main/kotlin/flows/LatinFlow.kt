package org.latin.server.flows

import kotlinx.serialization.Serializable

@Serializable
data class LatinFlow(val id: String, val events: List<String>)
