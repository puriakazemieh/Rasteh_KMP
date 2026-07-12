package com.kazemieh.domain.marketplace

data class OrderItem(
    val id: Long,
    val productId: Long?,
    val productName: String,
    val unitPrice: Double,
    val quantity: Int,
    val lineTotal: Double,
)

data class Order(
    val id: Long,
    val shopId: Long?,
    val shopName: String?,
    val customerUserId: Long?,
    val customerName: String?,
    val status: String,
    val totalAmount: Double,
    val note: String?,
    val createdAt: String?,
    val items: List<OrderItem>,
)
