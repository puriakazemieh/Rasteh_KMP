package com.kazemieh.domain.marketplace

data class Report(
    val id: Long,
    val userId: Long,
    val targetType: String,
    val targetId: Long,
    val reason: String?,
    val status: String,
    val createdAt: String?,
)
