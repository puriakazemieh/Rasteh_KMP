package com.kazemieh.data.services

import com.kazemieh.common.AppResult
import com.kazemieh.domain.services.Appointment
import com.kazemieh.domain.services.GiftCard
import com.kazemieh.domain.services.ServicesRepository
import com.kazemieh.network.common.safeApiCall
import com.kazemieh.network.services.ServicesApi
import com.kazemieh.network.services.dto.CreateAppointmentRequest
import com.kazemieh.network.services.dto.CreateGiftCardRequest
import com.kazemieh.network.services.dto.RedeemGiftCardRequest

class ServicesRepositoryImpl(private val api: ServicesApi) : ServicesRepository {
    override suspend fun issueGiftCard(amount: Double): AppResult<GiftCard> = safeApiCall {
        api.issueGiftCard(CreateGiftCardRequest(amount)).let { GiftCard(it.id, it.code, it.initialAmount, it.balance, it.ownerUserId, it.createdAt) }
    }
    override suspend fun redeemGiftCard(code: String): AppResult<GiftCard> = safeApiCall {
        api.redeemGiftCard(RedeemGiftCardRequest(code)).let { GiftCard(it.id, it.code, it.initialAmount, it.balance, it.ownerUserId, it.createdAt) }
    }
    override suspend fun getGiftCards(): AppResult<List<GiftCard>> = safeApiCall {
        api.getGiftCards().map { GiftCard(it.id, it.code, it.initialAmount, it.balance, it.ownerUserId, it.createdAt) }
    }
    override suspend fun getAppointments(): AppResult<List<Appointment>> = safeApiCall {
        api.getAppointments().map { Appointment(it.id, it.shopId, it.shopName, it.scheduledAt, it.note, it.status) }
    }
    override suspend fun bookAppointment(shopId: Long, scheduledAt: String, note: String?): AppResult<Appointment> = safeApiCall {
        api.bookAppointment(CreateAppointmentRequest(shopId, scheduledAt, note)).let { Appointment(it.id, it.shopId, it.shopName, it.scheduledAt, it.note, it.status) }
    }
}
