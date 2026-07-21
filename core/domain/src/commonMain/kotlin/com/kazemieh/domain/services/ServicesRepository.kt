package com.kazemieh.domain.services

import com.kazemieh.common.AppResult

interface ServicesRepository {
    suspend fun issueGiftCard(amount: Double): AppResult<GiftCard>
    suspend fun redeemGiftCard(code: String): AppResult<GiftCard>
    suspend fun getGiftCards(): AppResult<List<GiftCard>>
    suspend fun getAppointments(): AppResult<List<Appointment>>
    suspend fun bookAppointment(shopId: Long, scheduledAt: String, note: String?): AppResult<Appointment>
    suspend fun getReturns(): AppResult<List<ReturnRequest>>
    suspend fun createReturn(orderId: Long, reason: String?): AppResult<ReturnRequest>
}
