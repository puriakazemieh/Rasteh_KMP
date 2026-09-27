package com.kazemieh.data.canonical.repository

import com.kazemieh.common.AppResult
import com.kazemieh.data.canonical.mapper.toDomain
import com.kazemieh.domain.canonical.CanonicalMarketplaceRepository
import com.kazemieh.domain.canonical.CanonicalOfferPage
import com.kazemieh.domain.canonical.ShopOffer
import com.kazemieh.network.canonical.CanonicalMarketplaceApi
import com.kazemieh.network.common.safeApiCall

class CanonicalMarketplaceRepositoryImpl(
    private val api: CanonicalMarketplaceApi,
) : CanonicalMarketplaceRepository {
    override suspend fun getOffers(page: Int, size: Int): AppResult<CanonicalOfferPage> = safeApiCall {
        api.getOffers(page, size).let { response ->
            CanonicalOfferPage(
                items = response.items.map { it.toDomain() },
                page = response.page,
                size = response.size,
                totalElements = response.totalElements,
                totalPages = response.totalPages,
            )
        }
    }

    override suspend fun getOffer(id: String): AppResult<ShopOffer> = safeApiCall {
        api.getOffer(id).toDomain()
    }
}
