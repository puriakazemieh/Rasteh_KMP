package com.kazemieh.data.features.mapper

import com.kazemieh.domain.features.FlashSale
import com.kazemieh.domain.features.GroupBuy
import com.kazemieh.domain.features.Loyalty
import com.kazemieh.domain.features.PriceAlert
import com.kazemieh.network.features.dto.response.FlashSaleResponse
import com.kazemieh.network.features.dto.response.GroupBuyResponse
import com.kazemieh.network.features.dto.response.LoyaltyResponse
import com.kazemieh.network.features.dto.response.PriceAlertResponse

fun FlashSaleResponse.toDomain() = FlashSale(id, productId, productName, shopId, basePrice, discountPercent, salePrice, startsAt, endsAt, stockLimit, soldCount)
fun GroupBuyResponse.toDomain() = GroupBuy(id, productId, productName, shopId, unitPrice, targetCount, currentCount, endsAt, joined)
fun LoyaltyResponse.toDomain() = Loyalty(points, tier)
fun PriceAlertResponse.toDomain() = PriceAlert(id, productId, productName, targetPrice, currentPrice, active, createdAt)
