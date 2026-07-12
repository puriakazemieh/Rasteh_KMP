package com.kazemieh.network.marketplace

import com.kazemieh.network.common.safeApiCallRaw
import com.kazemieh.network.marketplace.dto.request.CreateProductRequest
import com.kazemieh.network.marketplace.dto.request.CreateShopRequest
import com.kazemieh.network.marketplace.dto.request.UpdateProductRequest
import com.kazemieh.network.marketplace.dto.response.CityResponse
import com.kazemieh.network.marketplace.dto.response.LocationResponse
import com.kazemieh.network.marketplace.dto.response.ProductResponse
import com.kazemieh.network.marketplace.dto.response.RastehResponse
import com.kazemieh.network.marketplace.dto.response.ShopResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class MarketplaceApiImpl(
    private val client: HttpClient
) : MarketplaceApi {

    override suspend fun getRastehs(): List<RastehResponse> = safeApiCallRaw {
        client.get("/api/rastehs")
    }

    override suspend fun getLocationsByRasteh(rastehId: Long): List<LocationResponse> = safeApiCallRaw {
        client.get("/api/rastehs/$rastehId/locations")
    }

    override suspend fun getLocation(id: Long): LocationResponse = safeApiCallRaw {
        client.get("/api/locations/$id")
    }

    override suspend fun getCities(query: String?): List<CityResponse> = safeApiCallRaw {
        client.get("/api/cities") {
            if (!query.isNullOrBlank()) parameter("query", query)
        }
    }

    override suspend fun registerShop(request: CreateShopRequest): ShopResponse = safeApiCallRaw {
        client.post("/api/shops") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun getMyShops(): List<ShopResponse> = safeApiCallRaw {
        client.get("/api/shops/mine")
    }

    override suspend fun getShop(id: Long): ShopResponse = safeApiCallRaw {
        client.get("/api/shops/$id")
    }

    override suspend fun getShopsByLocation(locationId: Long, rastehId: Long?): List<ShopResponse> = safeApiCallRaw {
        client.get("/api/locations/$locationId/shops") {
            if (rastehId != null) parameter("rastehId", rastehId)
        }
    }

    override suspend fun getProductsByShop(shopId: Long): List<ProductResponse> = safeApiCallRaw {
        client.get("/api/products") {
            parameter("shopId", shopId)
        }
    }

    override suspend fun getProduct(id: Long): ProductResponse = safeApiCallRaw {
        client.get("/api/products/$id")
    }

    override suspend fun getMyShopProducts(shopId: Long): List<ProductResponse> = safeApiCallRaw {
        client.get("/api/vendor/products/shop/$shopId")
    }

    override suspend fun createProduct(request: CreateProductRequest): ProductResponse = safeApiCallRaw {
        client.post("/api/vendor/products") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun updateProduct(id: Long, request: UpdateProductRequest): ProductResponse = safeApiCallRaw {
        client.put("/api/vendor/products/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun deleteProduct(id: Long) = safeApiCallRaw<Unit> {
        client.delete("/api/vendor/products/$id")
    }

    override suspend fun getAdminShops(status: String): List<ShopResponse> = safeApiCallRaw {
        client.get("/api/admin/shops") {
            parameter("status", status)
        }
    }

    override suspend fun approveShop(id: Long): ShopResponse = safeApiCallRaw {
        client.post("/api/admin/shops/$id/approve")
    }

    override suspend fun rejectShop(id: Long): ShopResponse = safeApiCallRaw {
        client.post("/api/admin/shops/$id/reject")
    }

    override suspend fun suspendShop(id: Long): ShopResponse = safeApiCallRaw {
        client.post("/api/admin/shops/$id/suspend")
    }
}
