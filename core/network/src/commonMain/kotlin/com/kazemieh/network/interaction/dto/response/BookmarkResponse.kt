package com.kazemieh.network.interaction.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class BookmarkResponse(
    val id: Long,
    val shopId: Long?,
    val shopName: String?,
    val shopEmoji: String?,
    val productId: Long?,
    val productName: String?,
    val createdAt: String?,
)
