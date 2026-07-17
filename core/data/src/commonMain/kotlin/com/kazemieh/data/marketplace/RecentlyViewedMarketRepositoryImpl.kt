package com.kazemieh.data.marketplace

import com.kazemieh.domain.marketplace.Product
import com.kazemieh.domain.marketplace.RecentlyViewedMarketRepository
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/**
 * پیاده‌سازیِ محلیِ تاریخچهٔ محصولاتِ بازارچه روی `multiplatform-settings`.
 * فهرست به‌صورتِ JSON ذخیره می‌شود؛ جدیدترین اول، بدونِ تکرار، حداکثر [MAX] مورد.
 */
class RecentlyViewedMarketRepositoryImpl(settings: Settings) : RecentlyViewedMarketRepository {

    private val observable: ObservableSettings =
        (settings as? ObservableSettings) ?: settings.makeObservable()

    @OptIn(ExperimentalSettingsApi::class)
    private val flowSettings = observable.toFlowSettings()

    private val json = Json { ignoreUnknownKeys = true }
    private val serializer = ListSerializer(Product.serializer())

    @OptIn(ExperimentalSettingsApi::class)
    override fun observe(): Flow<List<Product>> =
        flowSettings.getStringFlow(KEY, EMPTY).map(::decode)

    override suspend fun add(product: Product) {
        val current = decode(observable.getStringOrNull(KEY) ?: EMPTY)
        val updated = (listOf(product) + current.filter { it.id != product.id }).take(MAX)
        observable.putString(KEY, json.encodeToString(serializer, updated))
    }

    override suspend fun clear() {
        observable.putString(KEY, EMPTY)
    }

    private fun decode(raw: String): List<Product> =
        runCatching { json.decodeFromString(serializer, raw) }.getOrDefault(emptyList())

    private companion object {
        const val KEY = "rasteh_recently_viewed_products"
        const val EMPTY = "[]"
        const val MAX = 12
    }
}
