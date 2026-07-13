package com.kazemieh.network.features.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreatePriceAlertRequest(
    val productId: Long,
    val targetPrice: Double,
)
