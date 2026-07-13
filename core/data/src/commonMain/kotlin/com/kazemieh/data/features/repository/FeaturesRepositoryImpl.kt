package com.kazemieh.data.features.repository

import com.kazemieh.common.AppResult
import com.kazemieh.data.features.source.FeaturesDataSource
import com.kazemieh.domain.features.FeaturesRepository
import com.kazemieh.domain.features.FlashSale
import com.kazemieh.domain.features.GroupBuy
import com.kazemieh.domain.features.Loyalty
import com.kazemieh.domain.features.PriceAlert

class FeaturesRepositoryImpl(private val dataSource: FeaturesDataSource) : FeaturesRepository {
    override suspend fun getFlashSales(): AppResult<List<FlashSale>> = dataSource.getFlashSales()
    override suspend fun getGroupBuys(): AppResult<List<GroupBuy>> = dataSource.getGroupBuys()
    override suspend fun joinGroupBuy(id: Long): AppResult<GroupBuy> = dataSource.joinGroupBuy(id)
    override suspend fun getLoyalty(): AppResult<Loyalty> = dataSource.getLoyalty()
    override suspend fun getPriceAlerts(): AppResult<List<PriceAlert>> = dataSource.getPriceAlerts()
    override suspend fun createPriceAlert(productId: Long, targetPrice: Double): AppResult<PriceAlert> = dataSource.createPriceAlert(productId, targetPrice)
    override suspend fun deletePriceAlert(id: Long): AppResult<Unit> = dataSource.deletePriceAlert(id)
}
