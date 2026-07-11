package com.kazemieh.network.marketplace.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class LocationResponse(
    val id: Long,
    val cityId: Long?,
    val cityName: String?,
    val name: String,
    val kind: String,
    val address: String?,
    val floorCount: Int,
    val mapImageUrl: String?,
    val lat: Double?,
    val lng: Double?,
)
