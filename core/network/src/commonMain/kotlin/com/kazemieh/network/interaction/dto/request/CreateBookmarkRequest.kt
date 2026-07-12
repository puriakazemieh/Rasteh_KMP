package com.kazemieh.network.interaction.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateBookmarkRequest(
    val shopId: Long? = null,
    val productId: Long? = null,
)
