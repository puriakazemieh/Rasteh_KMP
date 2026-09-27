package com.kazemieh.network.canonical

import com.kazemieh.network.canonical.dto.response.CanonicalOfferResponse
import com.kazemieh.network.common.PageResponse
import com.kazemieh.network.common.safeApiCallRaw
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class CanonicalMarketplaceApiImpl(
    private val client: HttpClient,
) : CanonicalMarketplaceApi {
    override suspend fun getOffers(page: Int, size: Int): PageResponse<CanonicalOfferResponse> = safeApiCallRaw {
        client.get("api/v1/offers") {
            parameter("page", page)
            parameter("size", size)
        }
    }

    override suspend fun getOffer(id: String): CanonicalOfferResponse = safeApiCallRaw {
        client.get("api/v1/offers/$id")
    }
}
