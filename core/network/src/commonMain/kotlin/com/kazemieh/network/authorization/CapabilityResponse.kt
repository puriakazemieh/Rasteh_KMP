package com.kazemieh.network.authorization

import kotlinx.serialization.Serializable

@Serializable data class CapabilityResponse(val capabilities: List<String> = emptyList())
