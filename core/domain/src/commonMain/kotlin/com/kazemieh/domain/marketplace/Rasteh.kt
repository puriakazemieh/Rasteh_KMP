package com.kazemieh.domain.marketplace

import kotlinx.serialization.Serializable

/** راسته (صنف) — محورِ اصلیِ کشف در خانه. */
@Serializable
data class Rasteh(
    val id: Long,
    val label: String,
    val colorOklch: String?,
    val iconKey: String?,
    val sortOrder: Int,
)
