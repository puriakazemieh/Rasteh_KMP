package com.kazemieh.domain.marketplace

import com.kazemieh.common.AppResult

interface MarketplaceRepository {
    suspend fun getRastehs(): AppResult<List<Rasteh>>
    suspend fun getLocationsByRasteh(rastehId: Long): AppResult<List<MarketplaceLocation>>
    suspend fun getLocation(id: Long): AppResult<MarketplaceLocation>
    suspend fun getCities(query: String?): AppResult<List<City>>

    suspend fun registerShop(
        name: String,
        rastehId: Long?,
        locationId: Long?,
        category: String?,
        floor: String?,
        type: String,
        phone: String?,
        address: String?,
        workingHoursJson: String?,
        about: String?,
        hasChat: Boolean,
        acceptsOffers: Boolean,
        emoji: String?,
        coverStyle: String?,
    ): AppResult<Shop>

    suspend fun getMyShops(): AppResult<List<Shop>>
    suspend fun getShop(id: Long): AppResult<Shop>

    // فهرستِ فروشگاه‌های محل (rastehSearch)
    suspend fun getShopsByLocation(locationId: Long, rastehId: Long?): AppResult<List<Shop>>

    // کاتالوگ
    suspend fun getProductsByShop(shopId: Long): AppResult<List<Product>>
    suspend fun getProduct(id: Long): AppResult<Product>

    // مدیریتِ کالا (ونـدور)
    suspend fun getMyShopProducts(shopId: Long): AppResult<List<Product>>
    suspend fun createProduct(
        shopId: Long,
        name: String,
        description: String?,
        price: Double,
        oldPrice: Double?,
        discountPercent: Int?,
        condition: String,
        stock: Int,
        categoryName: String?,
        emoji: String?,
        imageUrl: String?,
    ): AppResult<Product>

    suspend fun updateProduct(
        id: Long,
        name: String?,
        description: String?,
        price: Double?,
        oldPrice: Double?,
        discountPercent: Int?,
        condition: String?,
        stock: Int?,
        categoryName: String?,
        emoji: String?,
        imageUrl: String?,
        active: Boolean?,
    ): AppResult<Product>

    suspend fun deleteProduct(id: Long): AppResult<Unit>

    // نظرات
    suspend fun getReviews(shopId: Long): AppResult<List<Review>>
    suspend fun createReview(shopId: Long, rating: Int, comment: String?): AppResult<Review>

    // جست‌وجو
    suspend fun searchShops(query: String?, rastehId: Long?, locationId: Long?, type: String?, sort: String?): AppResult<List<Shop>>
    suspend fun searchProducts(query: String?, shopId: Long?, condition: String?, minPrice: Long?, maxPrice: Long?, sort: String?): AppResult<List<Product>>

    // سفارش
    suspend fun createOrder(shopId: Long, items: List<Pair<Long, Int>>, note: String?): AppResult<Order>
    suspend fun getMyOrders(): AppResult<List<Order>>
    suspend fun getOrder(id: Long): AppResult<Order>
    suspend fun getVendorOrders(shopId: Long): AppResult<List<Order>>
    suspend fun updateOrderStatus(id: Long, status: String): AppResult<Order>

    // admin
    suspend fun getAdminShops(status: String): AppResult<List<Shop>>
    suspend fun approveShop(id: Long): AppResult<Shop>
    suspend fun rejectShop(id: Long): AppResult<Shop>
    suspend fun suspendShop(id: Long): AppResult<Shop>
}
