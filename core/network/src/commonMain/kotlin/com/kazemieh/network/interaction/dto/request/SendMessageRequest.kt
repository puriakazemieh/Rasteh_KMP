package com.kazemieh.network.interaction.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class SendMessageRequest(val body: String)
