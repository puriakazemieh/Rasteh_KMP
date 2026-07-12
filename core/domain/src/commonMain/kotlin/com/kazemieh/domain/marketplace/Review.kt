package com.kazemieh.domain.marketplace

data class Review(
    val id: Long,
    val shopId: Long?,
    val userId: Long,
    val authorName: String?,
    val rating: Int,
    val comment: String?,
    val createdAt: String?,
)
