package com.kazemieh.network.features

import com.kazemieh.network.features.dto.request.CreatePriceAlertRequest
import com.kazemieh.network.features.dto.response.FlashSaleResponse
import com.kazemieh.network.features.dto.response.GroupBuyResponse
import com.kazemieh.network.features.dto.response.LoyaltyResponse
import com.kazemieh.network.features.dto.response.PriceAlertResponse

interface FeaturesApi {
    suspend fun getFlashSales(): List<FlashSaleResponse>
    suspend fun getGroupBuys(): List<GroupBuyResponse>
    suspend fun joinGroupBuy(id: Long): GroupBuyResponse
    suspend fun getLoyalty(): LoyaltyResponse
    suspend fun getPriceAlerts(): List<PriceAlertResponse>
    suspend fun createPriceAlert(request: CreatePriceAlertRequest): PriceAlertResponse
    suspend fun deletePriceAlert(id: Long)
}
