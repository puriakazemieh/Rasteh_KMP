package com.kazemieh.network.auth.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class LogoutRequest(
    val refreshToken: String,
)
