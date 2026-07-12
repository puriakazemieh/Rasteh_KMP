package com.kazemieh.data.interaction.mapper

import com.kazemieh.domain.interaction.Bookmark
import com.kazemieh.domain.interaction.Conversation
import com.kazemieh.domain.interaction.Message
import com.kazemieh.domain.interaction.Offer
import com.kazemieh.network.interaction.dto.response.BookmarkResponse
import com.kazemieh.network.interaction.dto.response.ConversationResponse
import com.kazemieh.network.interaction.dto.response.MessageResponse
import com.kazemieh.network.interaction.dto.response.OfferResponse

fun ConversationResponse.toDomain() = Conversation(
    id = id, shopId = shopId, shopName = shopName, shopEmoji = shopEmoji,
    customerUserId = customerUserId, customerName = customerName,
    lastMessageAt = lastMessageAt, lastMessagePreview = lastMessagePreview,
)

fun MessageResponse.toDomain() = Message(
    id = id, conversationId = conversationId, senderUserId = senderUserId, body = body, createdAt = createdAt,
)

fun OfferResponse.toDomain() = Offer(
    id = id, shopId = shopId, shopName = shopName, productId = productId, productName = productName,
    customerUserId = customerUserId, customerName = customerName, amount = amount, message = message,
    status = status, createdAt = createdAt,
)

fun BookmarkResponse.toDomain() = Bookmark(
    id = id, shopId = shopId, shopName = shopName, shopEmoji = shopEmoji,
    productId = productId, productName = productName, createdAt = createdAt,
)
