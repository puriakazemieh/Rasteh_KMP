package com.kazemieh.domain.marketplace

import kotlinx.serialization.Serializable

@Serializable
data class City(
    val id: Long,
    val name: String,
    val province: String?,
)
