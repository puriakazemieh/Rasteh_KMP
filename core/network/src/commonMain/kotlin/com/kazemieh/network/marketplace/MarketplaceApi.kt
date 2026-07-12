package com.kazemieh.network.marketplace

import com.kazemieh.network.marketplace.dto.request.CreateProductRequest
import com.kazemieh.network.marketplace.dto.request.CreateShopRequest
import com.kazemieh.network.marketplace.dto.request.UpdateProductRequest
import com.kazemieh.network.marketplace.dto.response.CityResponse
import com.kazemieh.network.marketplace.dto.response.LocationResponse
import com.kazemieh.network.marketplace.dto.response.ProductResponse
import com.kazemieh.network.marketplace.dto.response.RastehResponse
import com.kazemieh.network.marketplace.dto.response.ShopResponse

interface MarketplaceApi {
    suspend fun getRastehs(): List<RastehResponse>
    suspend fun getLocationsByRasteh(rastehId: Long): List<LocationResponse>
    suspend fun getLocation(id: Long): LocationResponse
    suspend fun getCities(query: String?): List<CityResponse>

    suspend fun registerShop(request: CreateShopRequest): ShopResponse
    suspend fun getMyShops(): List<ShopResponse>
    suspend fun getShop(id: Long): ShopResponse

    // فهرستِ فروشگاه‌های محل (rastehSearch)
    suspend fun getShopsByLocation(locationId: Long, rastehId: Long?): List<ShopResponse>

    // کاتالوگ
    suspend fun getProductsByShop(shopId: Long): List<ProductResponse>
    suspend fun getProduct(id: Long): ProductResponse

    // مدیریتِ کالا (ونـدور)
    suspend fun getMyShopProducts(shopId: Long): List<ProductResponse>
    suspend fun createProduct(request: CreateProductRequest): ProductResponse
    suspend fun updateProduct(id: Long, request: UpdateProductRequest): ProductResponse
    suspend fun deleteProduct(id: Long)

    // admin
    suspend fun getAdminShops(status: String): List<ShopResponse>
    suspend fun approveShop(id: Long): ShopResponse
    suspend fun rejectShop(id: Long): ShopResponse
    suspend fun suspendShop(id: Long): ShopResponse
}
