package com.kazemieh.network.marketplace.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateReviewRequest(
    val shopId: Long,
    val rating: Int,
    val comment: String? = null,
)
