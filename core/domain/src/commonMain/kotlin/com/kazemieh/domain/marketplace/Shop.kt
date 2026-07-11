package com.kazemieh.domain.marketplace

import kotlinx.serialization.Serializable

@Serializable
data class Shop(
    val id: Long,
    val locationId: Long?,
    val locationName: String?,
    val rastehId: Long?,
    val rastehLabel: String?,
    val ownerUserId: Long?,
    val name: String,
    val category: String?,
    val floor: String?,
    val type: String,
    val verified: Boolean,
    val rating: Double,
    val reviewsCount: Int,
    val salesCount: Int,
    val phone: String?,
    val hasChat: Boolean,
    val acceptsOffers: Boolean,
    val about: String?,
    val workingHoursJson: String?,
    val address: String?,
    val emoji: String?,
    val coverStyle: String?,
    val coverUrl: String?,
    val logoUrl: String?,
    val status: String,
) {
    val isBuyable: Boolean get() = type == "BUYABLE"
    val isVisitOnly: Boolean get() = type == "VISIT_ONLY"
}
