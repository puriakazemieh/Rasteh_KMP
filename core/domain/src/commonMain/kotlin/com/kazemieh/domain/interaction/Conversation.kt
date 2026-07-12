package com.kazemieh.domain.interaction

data class Conversation(
    val id: Long,
    val shopId: Long?,
    val shopName: String?,
    val shopEmoji: String?,
    val customerUserId: Long?,
    val customerName: String?,
    val lastMessageAt: String?,
    val lastMessagePreview: String?,
)
