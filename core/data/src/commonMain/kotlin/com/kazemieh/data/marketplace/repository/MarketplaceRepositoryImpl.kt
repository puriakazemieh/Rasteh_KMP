package com.kazemieh.data.marketplace.repository

import com.kazemieh.common.AppResult
import com.kazemieh.data.marketplace.source.MarketplaceDataSource
import com.kazemieh.domain.marketplace.City
import com.kazemieh.domain.marketplace.MarketplaceLocation
import com.kazemieh.domain.marketplace.MarketplaceRepository
import com.kazemieh.domain.marketplace.Rasteh
import com.kazemieh.domain.marketplace.Shop

class MarketplaceRepositoryImpl(
    private val dataSource: MarketplaceDataSource
) : MarketplaceRepository {

    override suspend fun getRastehs(): AppResult<List<Rasteh>> = dataSource.getRastehs()
    override suspend fun getLocationsByRasteh(rastehId: Long): AppResult<List<MarketplaceLocation>> =
        dataSource.getLocationsByRasteh(rastehId)
    override suspend fun getLocation(id: Long): AppResult<MarketplaceLocation> = dataSource.getLocation(id)
    override suspend fun getCities(query: String?): AppResult<List<City>> = dataSource.getCities(query)

    override suspend fun registerShop(
        name: String, rastehId: Long?, locationId: Long?, category: String?, floor: String?,
        type: String, phone: String?, address: String?, workingHoursJson: String?, about: String?,
        hasChat: Boolean, acceptsOffers: Boolean, emoji: String?, coverStyle: String?,
    ): AppResult<Shop> = dataSource.registerShop(
        name, rastehId, locationId, category, floor, type, phone, address,
        workingHoursJson, about, hasChat, acceptsOffers, emoji, coverStyle,
    )

    override suspend fun getMyShops(): AppResult<List<Shop>> = dataSource.getMyShops()
    override suspend fun getShop(id: Long): AppResult<Shop> = dataSource.getShop(id)
    override suspend fun getAdminShops(status: String): AppResult<List<Shop>> = dataSource.getAdminShops(status)
    override suspend fun approveShop(id: Long): AppResult<Shop> = dataSource.approveShop(id)
    override suspend fun rejectShop(id: Long): AppResult<Shop> = dataSource.rejectShop(id)
    override suspend fun suspendShop(id: Long): AppResult<Shop> = dataSource.suspendShop(id)
}
