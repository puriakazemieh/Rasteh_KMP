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
    val price: CanonicalMoneyResponse? = null,
    val saleMode: String,
    val availableQuantity: Int,
    val stale: Boolean = false,
    val lastConfirmedAt: String? = null,
)
