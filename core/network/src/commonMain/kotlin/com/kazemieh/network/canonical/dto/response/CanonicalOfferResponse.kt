package com.kazemieh.network.canonical.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class CanonicalMoneyResponse(
    val minorUnits: Long,
    val currency: String,
)

@Serializable
data class CanonicalOfferResponse(
    val id: String,
    val productId: String,
    val shopLocationId: String,
    val title: String,
    val price: CanonicalMoneyResponse,
    val saleMode: String,
    val availableQuantity: Int,
)
