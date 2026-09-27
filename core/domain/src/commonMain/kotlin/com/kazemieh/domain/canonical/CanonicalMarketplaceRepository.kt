package com.kazemieh.domain.canonical

import com.kazemieh.common.AppResult

interface CanonicalMarketplaceRepository {
    suspend fun getOffers(page: Int = 0, size: Int = 20): AppResult<CanonicalOfferPage>
    suspend fun getOffer(id: String): AppResult<ShopOffer>
}

data class CanonicalOfferPage(
    val items: List<ShopOffer>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)
