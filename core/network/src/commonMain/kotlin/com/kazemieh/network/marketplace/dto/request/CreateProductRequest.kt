package com.kazemieh.network.marketplace.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateProductRequest(
    val shopId: Long,
    val name: String,
    val description: String? = null,
    val price: Double,
    val oldPrice: Double? = null,
    val discountPercent: Int? = null,
    val condition: String = "NEW",
    val stock: Int = 0,
    val categoryName: String? = null,
    val emoji: String? = null,
    val imageUrl: String? = null,
)
