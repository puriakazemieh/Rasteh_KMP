package com.kazemieh.network.marketplace.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProductRequest(
    val name: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val oldPrice: Double? = null,
    val discountPercent: Int? = null,
    val condition: String? = null,
    val stock: Int? = null,
    val categoryName: String? = null,
    val emoji: String? = null,
    val imageUrl: String? = null,
    val active: Boolean? = null,
)
