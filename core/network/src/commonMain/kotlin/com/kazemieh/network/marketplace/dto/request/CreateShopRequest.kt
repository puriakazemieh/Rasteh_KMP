package com.kazemieh.network.marketplace.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateShopRequest(
    val name: String,
    val rastehId: Long? = null,
    val locationId: Long? = null,
    val category: String? = null,
    val floor: String? = null,
    val type: String = "BUYABLE",
    val phone: String? = null,
    val address: String? = null,
    val workingHoursJson: String? = null,
    val about: String? = null,
    val hasChat: Boolean = true,
    val acceptsOffers: Boolean = false,
    val emoji: String? = null,
    val coverStyle: String? = null,
)
