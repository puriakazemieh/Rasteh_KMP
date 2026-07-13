package com.kazemieh.network.features.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class FlashSaleResponse(
    val id: Long,
    val productId: Long?,
    val productName: String?,
    val shopId: Long?,
    val basePrice: Double?,
    val discountPercent: Int,
    val salePrice: Double?,
    val startsAt: String,
    val endsAt: String,
    val stockLimit: Int,
    val soldCount: Int,
)

@Serializable
data class GroupBuyResponse(
    val id: Long,
    val productId: Long?,
    val productName: String?,
    val shopId: Long?,
    val unitPrice: Double,
    val targetCount: Int,
    val currentCount: Int,
    val endsAt: String,
    val joined: Boolean,
)

@Serializable
data class LoyaltyResponse(
    val points: Int,
    val tier: String,
)

@Serializable
data class PriceAlertResponse(
    val id: Long,
    val productId: Long?,
    val productName: String?,
    val targetPrice: Double,
    val currentPrice: Double?,
    val active: Boolean,
    val createdAt: String?,
)
