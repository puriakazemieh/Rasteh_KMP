package com.kazemieh.domain.interaction

data class Offer(
    val id: Long,
    val shopId: Long?,
    val shopName: String?,
    val productId: Long?,
    val productName: String?,
    val customerUserId: Long?,
    val customerName: String?,
    val amount: Double,
    val message: String?,
    val status: String,
    val createdAt: String?,
)
