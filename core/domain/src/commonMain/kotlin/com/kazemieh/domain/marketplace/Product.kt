package com.kazemieh.domain.marketplace

import kotlinx.serialization.Serializable

/** کالا/آگهیِ یک فروشگاه. `purchasable` از سرور می‌آید (BUYABLE + موجودی). */
@Serializable
data class Product(
    val id: Long,
    val shopId: Long?,
    val shopName: String?,
    val shopPhone: String? = null,
    val name: String,
    val description: String?,
    val price: Double,
    val oldPrice: Double?,
    val discountPercent: Int?,
    val condition: String,
    val stock: Int,
    val categoryName: String?,
    val emoji: String?,
    val imageUrl: String?,
    val active: Boolean,
    val purchasable: Boolean,
)
