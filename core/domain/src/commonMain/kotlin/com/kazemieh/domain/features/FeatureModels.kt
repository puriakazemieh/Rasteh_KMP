package com.kazemieh.domain.features

data class FlashSale(
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

data class GroupBuy(
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

data class Loyalty(val points: Int, val tier: String)

data class PriceAlert(
    val id: Long,
    val productId: Long?,
    val productName: String?,
    val targetPrice: Double,
    val currentPrice: Double?,
    val active: Boolean,
    val createdAt: String?,
)
