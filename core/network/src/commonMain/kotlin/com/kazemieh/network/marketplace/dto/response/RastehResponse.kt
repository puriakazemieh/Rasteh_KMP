package com.kazemieh.network.marketplace.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class RastehResponse(
    val id: Long,
    val label: String,
    val colorOklch: String?,
    val iconKey: String?,
    val sortOrder: Int,
)
