package com.kazemieh.domain.interaction

data class Message(
    val id: Long,
    val conversationId: Long?,
    val senderUserId: Long,
    val body: String,
    val createdAt: String?,
)
