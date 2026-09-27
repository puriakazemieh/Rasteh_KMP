package com.kazemieh.data.canonical.mapper

import com.kazemieh.domain.canonical.Money
import com.kazemieh.domain.canonical.SaleMode
import com.kazemieh.domain.canonical.ShopOffer
import com.kazemieh.network.canonical.dto.response.CanonicalOfferResponse

fun CanonicalOfferResponse.toDomain(): ShopOffer = ShopOffer(
    id = id,
    productId = productId,
    shopLocationId = shopLocationId,
    title = title,
    price = Money(price.minorUnits, price.currency),
    saleMode = SaleMode.entries.firstOrNull { it.name == saleMode } ?: SaleMode.SHOWCASE,
    availableQuantity = availableQuantity.coerceAtLeast(0),
)
