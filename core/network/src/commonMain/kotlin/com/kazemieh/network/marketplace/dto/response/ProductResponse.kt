package com.kazemieh.network.marketplace.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val id: Long,
    val shopId: Long?,
    val shopName: String?,
    val shopPhone: String? = null,
    val name: String,
    val description: String?,
    val price: Double,
    val oldPrice: Double?,
    val discountPercent: Int?,
    val condition: String,
    val stock: Int,
    val categoryName: String?,
    val emoji: String?,
    val imageUrl: String?,
    val active: Boolean,
    val purchasable: Boolean,
    val createdAt: String?,
)
