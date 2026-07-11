package com.kazemieh.network.marketplace.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class CityResponse(
    val id: Long,
    val name: String,
    val province: String?,
)
