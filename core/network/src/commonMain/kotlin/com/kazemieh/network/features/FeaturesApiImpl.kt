package com.kazemieh.network.features

import com.kazemieh.network.common.safeApiCallRaw
import com.kazemieh.network.features.dto.request.CreatePriceAlertRequest
import com.kazemieh.network.features.dto.response.FlashSaleResponse
import com.kazemieh.network.features.dto.response.GroupBuyResponse
import com.kazemieh.network.features.dto.response.LoyaltyResponse
import com.kazemieh.network.features.dto.response.PriceAlertResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class FeaturesApiImpl(private val client: HttpClient) : FeaturesApi {
    override suspend fun getFlashSales(): List<FlashSaleResponse> = safeApiCallRaw { client.get("/api/flash") }
    override suspend fun getGroupBuys(): List<GroupBuyResponse> = safeApiCallRaw { client.get("/api/groupbuys") }
    override suspend fun joinGroupBuy(id: Long): GroupBuyResponse = safeApiCallRaw { client.post("/api/groupbuys/$id/join") }
    override suspend fun getLoyalty(): LoyaltyResponse = safeApiCallRaw { client.get("/api/loyalty/me") }
    override suspend fun getPriceAlerts(): List<PriceAlertResponse> = safeApiCallRaw { client.get("/api/pricealerts/mine") }
    override suspend fun createPriceAlert(request: CreatePriceAlertRequest): PriceAlertResponse = safeApiCallRaw {
        client.post("/api/pricealerts") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
    override suspend fun deletePriceAlert(id: Long) = safeApiCallRaw<Unit> { client.delete("/api/pricealerts/$id") }
}
