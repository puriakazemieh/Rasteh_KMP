package com.kazemieh.network.marketplace.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ReviewResponse(
    val id: Long,
    val shopId: Long?,
    val userId: Long,
    val authorName: String?,
    val rating: Int,
    val comment: String?,
    val createdAt: String?,
)
