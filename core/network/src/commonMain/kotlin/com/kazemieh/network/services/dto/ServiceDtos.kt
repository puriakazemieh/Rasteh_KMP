package com.kazemieh.network.services.dto

import kotlinx.serialization.Serializable

@Serializable data class GiftCardResponse(val id: Long, val code: String, val initialAmount: Double, val balance: Double, val ownerUserId: Long?, val createdAt: String?)
@Serializable data class CreateGiftCardRequest(val amount: Double)
@Serializable data class RedeemGiftCardRequest(val code: String)
@Serializable data class AppointmentResponse(val id: Long, val shopId: Long?, val shopName: String?, val userId: Long, val scheduledAt: String, val note: String?, val status: String, val createdAt: String?)
@Serializable data class CreateAppointmentRequest(val shopId: Long, val scheduledAt: String, val note: String? = null)
