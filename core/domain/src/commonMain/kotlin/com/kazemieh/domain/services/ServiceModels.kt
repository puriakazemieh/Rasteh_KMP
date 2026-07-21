package com.kazemieh.domain.services

data class GiftCard(val id: Long, val code: String, val initialAmount: Double, val balance: Double, val ownerUserId: Long?, val createdAt: String?)
data class Appointment(val id: Long, val shopId: Long?, val shopName: String?, val scheduledAt: String, val note: String?, val status: String)
data class ReturnRequest(val id: Long, val orderId: Long, val reason: String?, val status: String, val createdAt: String?)
