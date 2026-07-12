package com.kazemieh.network.interaction.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    val id: Long,
    val conversationId: Long?,
    val senderUserId: Long,
    val body: String,
    val createdAt: String?,
)
