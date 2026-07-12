package com.kazemieh.network.interaction

import com.kazemieh.network.interaction.dto.request.CreateBookmarkRequest
import com.kazemieh.network.interaction.dto.request.CreateOfferRequest
import com.kazemieh.network.interaction.dto.request.SendMessageRequest
import com.kazemieh.network.interaction.dto.request.StartConversationRequest
import com.kazemieh.network.interaction.dto.response.BookmarkResponse
import com.kazemieh.network.interaction.dto.response.ConversationResponse
import com.kazemieh.network.interaction.dto.response.MessageResponse
import com.kazemieh.network.interaction.dto.response.OfferResponse

interface InteractionApi {
    // chat
    suspend fun startConversation(request: StartConversationRequest): ConversationResponse
    suspend fun getConversations(): List<ConversationResponse>
    suspend fun getMessages(conversationId: Long): List<MessageResponse>
    suspend fun sendMessage(conversationId: Long, request: SendMessageRequest): MessageResponse

    // offers
    suspend fun createOffer(request: CreateOfferRequest): OfferResponse
    suspend fun getMyOffers(): List<OfferResponse>
    suspend fun getShopOffers(shopId: Long): List<OfferResponse>
    suspend fun acceptOffer(id: Long): OfferResponse
    suspend fun rejectOffer(id: Long): OfferResponse

    // bookmarks
    suspend fun getBookmarks(): List<BookmarkResponse>
    suspend fun addBookmark(request: CreateBookmarkRequest): BookmarkResponse
    suspend fun removeBookmark(id: Long)
}
