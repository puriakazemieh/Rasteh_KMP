package com.kazemieh.network.marketplace.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class OrderItemRequest(val productId: Long, val quantity: Int)

@Serializable
data class CreateOrderRequest(
    val shopId: Long,
    val items: List<OrderItemRequest>,
    val note: String? = null,
)

@Serializable
data class UpdateOrderStatusRequest(val status: String)
