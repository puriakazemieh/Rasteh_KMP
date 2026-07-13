package com.kazemieh.data.features.source

import com.kazemieh.common.AppResult
import com.kazemieh.data.features.mapper.toDomain
import com.kazemieh.domain.features.FlashSale
import com.kazemieh.domain.features.GroupBuy
import com.kazemieh.domain.features.Loyalty
import com.kazemieh.domain.features.PriceAlert
import com.kazemieh.network.common.safeApiCall
import com.kazemieh.network.features.FeaturesApi
import com.kazemieh.network.features.dto.request.CreatePriceAlertRequest

class FeaturesDataSource(private val api: FeaturesApi) {
    suspend fun getFlashSales(): AppResult<List<FlashSale>> = safeApiCall { api.getFlashSales().map { it.toDomain() } }
    suspend fun getGroupBuys(): AppResult<List<GroupBuy>> = safeApiCall { api.getGroupBuys().map { it.toDomain() } }
    suspend fun joinGroupBuy(id: Long): AppResult<GroupBuy> = safeApiCall { api.joinGroupBuy(id).toDomain() }
    suspend fun getLoyalty(): AppResult<Loyalty> = safeApiCall { api.getLoyalty().toDomain() }
    suspend fun getPriceAlerts(): AppResult<List<PriceAlert>> = safeApiCall { api.getPriceAlerts().map { it.toDomain() } }
    suspend fun createPriceAlert(productId: Long, targetPrice: Double): AppResult<PriceAlert> = safeApiCall {
        api.createPriceAlert(CreatePriceAlertRequest(productId, targetPrice)).toDomain()
    }
    suspend fun deletePriceAlert(id: Long): AppResult<Unit> = safeApiCall { api.deletePriceAlert(id) }
}
