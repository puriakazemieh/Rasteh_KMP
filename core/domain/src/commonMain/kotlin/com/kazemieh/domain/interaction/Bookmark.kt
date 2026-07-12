package com.kazemieh.domain.interaction

data class Bookmark(
    val id: Long,
    val shopId: Long?,
    val shopName: String?,
    val shopEmoji: String?,
    val productId: Long?,
    val productName: String?,
    val createdAt: String?,
)
