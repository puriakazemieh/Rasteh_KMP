package com.kazemieh.network.interaction.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ConversationResponse(
    val id: Long,
    val shopId: Long?,
    val shopName: String?,
    val shopEmoji: String?,
    val customerUserId: Long?,
    val customerName: String?,
    val lastMessageAt: String?,
    val lastMessagePreview: String?,
)
