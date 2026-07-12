package com.kazemieh.network.marketplace.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class OrderItemResponse(
    val id: Long,
    val productId: Long?,
    val productName: String,
    val unitPrice: Double,
    val quantity: Int,
    val lineTotal: Double,
)

@Serializable
data class OrderResponse(
    val id: Long,
    val shopId: Long?,
    val shopName: String?,
    val customerUserId: Long?,
    val customerName: String?,
    val status: String,
    val totalAmount: Double,
    val note: String?,
    val createdAt: String?,
    val items: List<OrderItemResponse> = emptyList(),
)
