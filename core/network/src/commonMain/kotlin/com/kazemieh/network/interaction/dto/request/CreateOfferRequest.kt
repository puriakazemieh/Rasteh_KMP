package com.kazemieh.network.interaction.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateOfferRequest(
    val shopId: Long,
    val productId: Long? = null,
    val amount: Double,
    val message: String? = null,
)
