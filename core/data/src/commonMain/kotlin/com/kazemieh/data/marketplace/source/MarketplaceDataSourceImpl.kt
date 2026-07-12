package com.kazemieh.data.marketplace.source

import com.kazemieh.common.AppResult
import com.kazemieh.data.marketplace.mapper.toDomain
import com.kazemieh.domain.marketplace.City
import com.kazemieh.domain.marketplace.MarketplaceLocation
import com.kazemieh.domain.marketplace.Order
import com.kazemieh.domain.marketplace.Product
import com.kazemieh.domain.marketplace.Rasteh
import com.kazemieh.domain.marketplace.Review
import com.kazemieh.domain.marketplace.Shop
import com.kazemieh.network.common.safeApiCall
import com.kazemieh.network.marketplace.MarketplaceApi
import com.kazemieh.network.marketplace.dto.request.CreateOrderRequest
import com.kazemieh.network.marketplace.dto.request.CreateProductRequest
import com.kazemieh.network.marketplace.dto.request.CreateReviewRequest
import com.kazemieh.network.marketplace.dto.request.CreateShopRequest
import com.kazemieh.network.marketplace.dto.request.OrderItemRequest
import com.kazemieh.network.marketplace.dto.request.UpdateOrderStatusRequest
import com.kazemieh.network.marketplace.dto.request.UpdateProductRequest

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

    override suspend fun getShopsByLocation(locationId: Long, rastehId: Long?): AppResult<List<Shop>> = safeApiCall {
        api.getShopsByLocation(locationId, rastehId).map { it.toDomain() }
    }

    override suspend fun getProductsByShop(shopId: Long): AppResult<List<Product>> = safeApiCall {
        api.getProductsByShop(shopId).map { it.toDomain() }
    }

    override suspend fun getProduct(id: Long): AppResult<Product> = safeApiCall {
        api.getProduct(id).toDomain()
    }

    override suspend fun getMyShopProducts(shopId: Long): AppResult<List<Product>> = safeApiCall {
        api.getMyShopProducts(shopId).map { it.toDomain() }
    }

    override suspend fun createProduct(
        shopId: Long, name: String, description: String?, price: Double, oldPrice: Double?,
        discountPercent: Int?, condition: String, stock: Int, categoryName: String?, emoji: String?, imageUrl: String?,
    ): AppResult<Product> = safeApiCall {
        api.createProduct(
            CreateProductRequest(
                shopId = shopId, name = name, description = description, price = price, oldPrice = oldPrice,
                discountPercent = discountPercent, condition = condition, stock = stock,
                categoryName = categoryName, emoji = emoji, imageUrl = imageUrl,
            )
        ).toDomain()
    }

    override suspend fun updateProduct(
        id: Long, name: String?, description: String?, price: Double?, oldPrice: Double?,
        discountPercent: Int?, condition: String?, stock: Int?, categoryName: String?, emoji: String?,
        imageUrl: String?, active: Boolean?,
    ): AppResult<Product> = safeApiCall {
        api.updateProduct(
            id,
            UpdateProductRequest(
                name = name, description = description, price = price, oldPrice = oldPrice,
                discountPercent = discountPercent, condition = condition, stock = stock,
                categoryName = categoryName, emoji = emoji, imageUrl = imageUrl, active = active,
            )
        ).toDomain()
    }

    override suspend fun deleteProduct(id: Long): AppResult<Unit> = safeApiCall {
        api.deleteProduct(id)
    }

    override suspend fun getReviews(shopId: Long): AppResult<List<Review>> = safeApiCall {
        api.getReviews(shopId).map { it.toDomain() }
    }

    override suspend fun createReview(shopId: Long, rating: Int, comment: String?): AppResult<Review> = safeApiCall {
        api.createReview(CreateReviewRequest(shopId, rating, comment)).toDomain()
    }

    override suspend fun searchShops(query: String?, rastehId: Long?, locationId: Long?, type: String?, sort: String?): AppResult<List<Shop>> = safeApiCall {
        api.searchShops(query, rastehId, locationId, type, sort).map { it.toDomain() }
    }

    override suspend fun searchProducts(query: String?, shopId: Long?, condition: String?, minPrice: Long?, maxPrice: Long?, sort: String?): AppResult<List<Product>> = safeApiCall {
        api.searchProducts(query, shopId, condition, minPrice, maxPrice, sort).map { it.toDomain() }
    }

    override suspend fun createOrder(shopId: Long, items: List<Pair<Long, Int>>, note: String?): AppResult<Order> = safeApiCall {
        api.createOrder(
            CreateOrderRequest(shopId, items.map { OrderItemRequest(it.first, it.second) }, note)
        ).toDomain()
    }

    override suspend fun getMyOrders(): AppResult<List<Order>> = safeApiCall {
        api.getMyOrders().map { it.toDomain() }
    }

    override suspend fun getOrder(id: Long): AppResult<Order> = safeApiCall {
        api.getOrder(id).toDomain()
    }

    override suspend fun getVendorOrders(shopId: Long): AppResult<List<Order>> = safeApiCall {
        api.getVendorOrders(shopId).map { it.toDomain() }
    }

    override suspend fun updateOrderStatus(id: Long, status: String): AppResult<Order> = safeApiCall {
        api.updateOrderStatus(id, UpdateOrderStatusRequest(status)).toDomain()
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
