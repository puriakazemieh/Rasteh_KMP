package com.kazemieh.network.marketplace.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateReportRequest(val targetType: String, val targetId: Long, val reason: String? = null)

@Serializable
data class ResolveReportRequest(val status: String)

@Serializable
data class CreateRastehRequest(val label: String, val colorOklch: String? = null, val iconKey: String? = null, val sortOrder: Int = 0)

@Serializable
data class CreateLocationRequest(val cityId: Long, val name: String, val kind: String = "PASSAGE", val address: String? = null, val floorCount: Int = 1)
