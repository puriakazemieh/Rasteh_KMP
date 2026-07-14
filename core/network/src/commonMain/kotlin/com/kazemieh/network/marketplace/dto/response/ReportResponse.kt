package com.kazemieh.network.marketplace.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ReportResponse(
    val id: Long,
    val userId: Long,
    val targetType: String,
    val targetId: Long,
    val reason: String?,
    val status: String,
    val createdAt: String?,
)
