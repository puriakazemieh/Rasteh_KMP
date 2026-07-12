package com.kazemieh.data.marketplace.mapper

import com.kazemieh.domain.marketplace.City
import com.kazemieh.domain.marketplace.MarketplaceLocation
import com.kazemieh.domain.marketplace.Product
import com.kazemieh.domain.marketplace.Rasteh
import com.kazemieh.domain.marketplace.Shop
import com.kazemieh.network.marketplace.dto.response.CityResponse
import com.kazemieh.network.marketplace.dto.response.LocationResponse
import com.kazemieh.network.marketplace.dto.response.ProductResponse
import com.kazemieh.network.marketplace.dto.response.RastehResponse
import com.kazemieh.network.marketplace.dto.response.ShopResponse

fun RastehResponse.toDomain() = Rasteh(
    id = id,
    label = label,
    colorOklch = colorOklch,
    iconKey = iconKey,
    sortOrder = sortOrder,
)

fun CityResponse.toDomain() = City(
    id = id,
    name = name,
    province = province,
)

fun LocationResponse.toDomain() = MarketplaceLocation(
    id = id,
    cityId = cityId,
    cityName = cityName,
    name = name,
    kind = kind,
    address = address,
    floorCount = floorCount,
    mapImageUrl = mapImageUrl,
    lat = lat,
    lng = lng,
)

fun ShopResponse.toDomain() = Shop(
    id = id,
    locationId = locationId,
    locationName = locationName,
    rastehId = rastehId,
    rastehLabel = rastehLabel,
    ownerUserId = ownerUserId,
    name = name,
    category = category,
    floor = floor,
    type = type,
    verified = verified,
    rating = rating,
    reviewsCount = reviewsCount,
    salesCount = salesCount,
    phone = phone,
    hasChat = hasChat,
    acceptsOffers = acceptsOffers,
    about = about,
    workingHoursJson = workingHoursJson,
    address = address,
    emoji = emoji,
    coverStyle = coverStyle,
    coverUrl = coverUrl,
    logoUrl = logoUrl,
    status = status,
)

fun ProductResponse.toDomain() = Product(
    id = id,
    shopId = shopId,
    shopName = shopName,
    name = name,
    description = description,
    price = price,
    oldPrice = oldPrice,
    discountPercent = discountPercent,
    condition = condition,
    stock = stock,
    categoryName = categoryName,
    emoji = emoji,
    imageUrl = imageUrl,
    active = active,
    purchasable = purchasable,
)
