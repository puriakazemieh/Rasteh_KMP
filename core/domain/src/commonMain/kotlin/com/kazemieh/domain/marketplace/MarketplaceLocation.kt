package com.kazemieh.domain.marketplace

import kotlinx.serialization.Serializable

/** محل · پاساژ/بازار/راستهٔ فیزیکی. (نامِ MarketplaceLocation برای پرهیز از تداخل با نوعِ سکوی iOS.) */
@Serializable
data class MarketplaceLocation(
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
