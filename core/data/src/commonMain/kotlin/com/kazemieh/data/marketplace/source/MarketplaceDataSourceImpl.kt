package com.kazemieh.data.marketplace.source

import com.kazemieh.common.AppResult
import com.kazemieh.data.marketplace.mapper.toDomain
import com.kazemieh.domain.marketplace.City
import com.kazemieh.domain.marketplace.MarketplaceLocation
import com.kazemieh.domain.marketplace.Rasteh
import com.kazemieh.domain.marketplace.Shop
import com.kazemieh.network.common.safeApiCall
import com.kazemieh.network.marketplace.MarketplaceApi
import com.kazemieh.network.marketplace.dto.request.CreateShopRequest

class MarketplaceDataSourceImpl(
    private val api: MarketplaceApi
) : MarketplaceDataSource {

    override suspend fun getRastehs(): AppResult<List<Rasteh>> = safeApiCall {
        api.getRastehs().map { it.toDomain() }
    }

    override suspend fun getLocationsByRasteh(rastehId: Long): AppResult<List<MarketplaceLocation>> = safeApiCall {
        api.getLocationsByRasteh(rastehId).map { it.toDomain() }
    }

    override suspend fun getLocation(id: Long): AppResult<MarketplaceLocation> = safeApiCall {
        api.getLocation(id).toDomain()
    }

    override suspend fun getCities(query: String?): AppResult<List<City>> = safeApiCall {
        api.getCities(query).map { it.toDomain() }
    }

    override suspend fun registerShop(
        name: String, rastehId: Long?, locationId: Long?, category: String?, floor: String?,
        type: String, phone: String?, address: String?, workingHoursJson: String?, about: String?,
        hasChat: Boolean, acceptsOffers: Boolean, emoji: String?, coverStyle: String?,
    ): AppResult<Shop> = safeApiCall {
        api.registerShop(
            CreateShopRequest(
                name = name, rastehId = rastehId, locationId = locationId, category = category,
                floor = floor, type = type, phone = phone, address = address,
                workingHoursJson = workingHoursJson, about = about, hasChat = hasChat,
                acceptsOffers = acceptsOffers, emoji = emoji, coverStyle = coverStyle,
            )
        ).toDomain()
    }

    override suspend fun getMyShops(): AppResult<List<Shop>> = safeApiCall {
        api.getMyShops().map { it.toDomain() }
    }

    override suspend fun getShop(id: Long): AppResult<Shop> = safeApiCall {
        api.getShop(id).toDomain()
    }

    override suspend fun getAdminShops(status: String): AppResult<List<Shop>> = safeApiCall {
        api.getAdminShops(status).map { it.toDomain() }
    }

    override suspend fun approveShop(id: Long): AppResult<Shop> = safeApiCall {
        api.approveShop(id).toDomain()
    }

    override suspend fun rejectShop(id: Long): AppResult<Shop> = safeApiCall {
        api.rejectShop(id).toDomain()
    }

    override suspend fun suspendShop(id: Long): AppResult<Shop> = safeApiCall {
        api.suspendShop(id).toDomain()
    }
}
