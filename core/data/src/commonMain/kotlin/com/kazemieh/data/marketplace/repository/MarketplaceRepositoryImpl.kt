package com.kazemieh.data.marketplace.repository

import com.kazemieh.common.AppResult
import com.kazemieh.data.marketplace.source.MarketplaceDataSource
import com.kazemieh.domain.marketplace.City
import com.kazemieh.domain.marketplace.MarketplaceLocation
import com.kazemieh.domain.marketplace.MarketplaceRepository
import com.kazemieh.domain.marketplace.Product
import com.kazemieh.domain.marketplace.Rasteh
import com.kazemieh.domain.marketplace.Shop

class MarketplaceRepositoryImpl(
    private val dataSource: MarketplaceDataSource
) : MarketplaceRepository {

    override suspend fun getRastehs(): AppResult<List<Rasteh>> = dataSource.getRastehs()
    override suspend fun getLocationsByRasteh(rastehId: Long): AppResult<List<MarketplaceLocation>> =
        dataSource.getLocationsByRasteh(rastehId)
    override suspend fun getLocation(id: Long): AppResult<MarketplaceLocation> = dataSource.getLocation(id)
    override suspend fun getCities(query: String?): AppResult<List<City>> = dataSource.getCities(query)

    override suspend fun registerShop(
        name: String, rastehId: Long?, locationId: Long?, category: String?, floor: String?,
        type: String, phone: String?, address: String?, workingHoursJson: String?, about: String?,
        hasChat: Boolean, acceptsOffers: Boolean, emoji: String?, coverStyle: String?,
    ): AppResult<Shop> = dataSource.registerShop(
        name, rastehId, locationId, category, floor, type, phone, address,
        workingHoursJson, about, hasChat, acceptsOffers, emoji, coverStyle,
    )

    override suspend fun getMyShops(): AppResult<List<Shop>> = dataSource.getMyShops()
    override suspend fun getShop(id: Long): AppResult<Shop> = dataSource.getShop(id)

    override suspend fun getShopsByLocation(locationId: Long, rastehId: Long?): AppResult<List<Shop>> =
        dataSource.getShopsByLocation(locationId, rastehId)

    override suspend fun getProductsByShop(shopId: Long): AppResult<List<Product>> =
        dataSource.getProductsByShop(shopId)

    override suspend fun getProduct(id: Long): AppResult<Product> = dataSource.getProduct(id)

    override suspend fun getMyShopProducts(shopId: Long): AppResult<List<Product>> =
        dataSource.getMyShopProducts(shopId)

    override suspend fun createProduct(
        shopId: Long, name: String, description: String?, price: Double, oldPrice: Double?,
        discountPercent: Int?, condition: String, stock: Int, categoryName: String?, emoji: String?, imageUrl: String?,
    ): AppResult<Product> = dataSource.createProduct(
        shopId, name, description, price, oldPrice, discountPercent, condition, stock, categoryName, emoji, imageUrl,
    )

    override suspend fun updateProduct(
        id: Long, name: String?, description: String?, price: Double?, oldPrice: Double?,
        discountPercent: Int?, condition: String?, stock: Int?, categoryName: String?, emoji: String?,
        imageUrl: String?, active: Boolean?,
    ): AppResult<Product> = dataSource.updateProduct(
        id, name, description, price, oldPrice, discountPercent, condition, stock, categoryName, emoji, imageUrl, active,
    )

    override suspend fun deleteProduct(id: Long): AppResult<Unit> = dataSource.deleteProduct(id)

    override suspend fun getAdminShops(status: String): AppResult<List<Shop>> = dataSource.getAdminShops(status)
    override suspend fun approveShop(id: Long): AppResult<Shop> = dataSource.approveShop(id)
    override suspend fun rejectShop(id: Long): AppResult<Shop> = dataSource.rejectShop(id)
    override suspend fun suspendShop(id: Long): AppResult<Shop> = dataSource.suspendShop(id)
}
