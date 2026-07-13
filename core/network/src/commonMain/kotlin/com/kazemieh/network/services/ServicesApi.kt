package com.kazemieh.network.services

import com.kazemieh.network.common.safeApiCallRaw
import com.kazemieh.network.services.dto.*
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface ServicesApi {
    suspend fun issueGiftCard(request: CreateGiftCardRequest): GiftCardResponse
    suspend fun redeemGiftCard(request: RedeemGiftCardRequest): GiftCardResponse
    suspend fun getGiftCards(): List<GiftCardResponse>
    suspend fun getAppointments(): List<AppointmentResponse>
    suspend fun bookAppointment(request: CreateAppointmentRequest): AppointmentResponse
}

class ServicesApiImpl(private val client: HttpClient) : ServicesApi {
    override suspend fun issueGiftCard(request: CreateGiftCardRequest): GiftCardResponse = safeApiCallRaw {
        client.post("/api/giftcards") { contentType(ContentType.Application.Json); setBody(request) }
    }
    override suspend fun redeemGiftCard(request: RedeemGiftCardRequest): GiftCardResponse = safeApiCallRaw {
        client.post("/api/giftcards/redeem") { contentType(ContentType.Application.Json); setBody(request) }
    }
    override suspend fun getGiftCards(): List<GiftCardResponse> = safeApiCallRaw { client.get("/api/giftcards/mine") }
    override suspend fun getAppointments(): List<AppointmentResponse> = safeApiCallRaw { client.get("/api/appointments/mine") }
    override suspend fun bookAppointment(request: CreateAppointmentRequest): AppointmentResponse = safeApiCallRaw {
        client.post("/api/appointments") { contentType(ContentType.Application.Json); setBody(request) }
    }
}
