package com.kazemieh.data.marketplace.source

import com.kazemieh.common.AppResult
import com.kazemieh.domain.marketplace.City
import com.kazemieh.domain.marketplace.MarketplaceLocation
import com.kazemieh.domain.marketplace.Rasteh
import com.kazemieh.domain.marketplace.Shop

interface MarketplaceDataSource {
    suspend fun getRastehs(): AppResult<List<Rasteh>>
    suspend fun getLocationsByRasteh(rastehId: Long): AppResult<List<MarketplaceLocation>>
    suspend fun getLocation(id: Long): AppResult<MarketplaceLocation>
    suspend fun getCities(query: String?): AppResult<List<City>>
    suspend fun registerShop(
        name: String, rastehId: Long?, locationId: Long?, category: String?, floor: String?,
        type: String, phone: String?, address: String?, workingHoursJson: String?, about: String?,
        hasChat: Boolean, acceptsOffers: Boolean, emoji: String?, coverStyle: String?,
    ): AppResult<Shop>
    suspend fun getMyShops(): AppResult<List<Shop>>
    suspend fun getShop(id: Long): AppResult<Shop>
    suspend fun getAdminShops(status: String): AppResult<List<Shop>>
    suspend fun approveShop(id: Long): AppResult<Shop>
    suspend fun rejectShop(id: Long): AppResult<Shop>
    suspend fun suspendShop(id: Long): AppResult<Shop>
}
