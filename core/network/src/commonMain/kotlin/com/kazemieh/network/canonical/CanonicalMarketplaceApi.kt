package com.kazemieh.network.canonical

import com.kazemieh.network.canonical.dto.response.CanonicalOfferResponse
import com.kazemieh.network.common.PageResponse

/** `/api/v1` only. Legacy marketplace/catalog APIs remain isolated during migration. */
interface CanonicalMarketplaceApi {
    suspend fun getOffers(page: Int = 0, size: Int = 20): PageResponse<CanonicalOfferResponse>
    suspend fun getOffer(id: String): CanonicalOfferResponse
}
