package com.kazemieh.domain.features

import com.kazemieh.common.AppResult

interface FeaturesRepository {
    suspend fun getFlashSales(): AppResult<List<FlashSale>>
    suspend fun getGroupBuys(): AppResult<List<GroupBuy>>
    suspend fun joinGroupBuy(id: Long): AppResult<GroupBuy>
    suspend fun getLoyalty(): AppResult<Loyalty>
    suspend fun getPriceAlerts(): AppResult<List<PriceAlert>>
    suspend fun createPriceAlert(productId: Long, targetPrice: Double): AppResult<PriceAlert>
    suspend fun deletePriceAlert(id: Long): AppResult<Unit>
}
