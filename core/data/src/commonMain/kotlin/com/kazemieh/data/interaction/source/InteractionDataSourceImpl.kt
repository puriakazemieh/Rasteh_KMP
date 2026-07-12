package com.kazemieh.data.interaction.source

import com.kazemieh.common.AppResult
import com.kazemieh.data.interaction.mapper.toDomain
import com.kazemieh.domain.interaction.Bookmark
import com.kazemieh.domain.interaction.Conversation
import com.kazemieh.domain.interaction.Message
import com.kazemieh.domain.interaction.Offer
import com.kazemieh.network.common.safeApiCall
import com.kazemieh.network.interaction.InteractionApi
import com.kazemieh.network.interaction.dto.request.CreateBookmarkRequest
import com.kazemieh.network.interaction.dto.request.CreateOfferRequest
import com.kazemieh.network.interaction.dto.request.SendMessageRequest
import com.kazemieh.network.interaction.dto.request.StartConversationRequest

class InteractionDataSourceImpl(
    private val api: InteractionApi
) : InteractionDataSource {

    override suspend fun startConversation(shopId: Long): AppResult<Conversation> = safeApiCall {
        api.startConversation(StartConversationRequest(shopId)).toDomain()
    }
    override suspend fun getConversations(): AppResult<List<Conversation>> = safeApiCall {
        api.getConversations().map { it.toDomain() }
    }
    override suspend fun getMessages(conversationId: Long): AppResult<List<Message>> = safeApiCall {
        api.getMessages(conversationId).map { it.toDomain() }
    }
    override suspend fun sendMessage(conversationId: Long, body: String): AppResult<Message> = safeApiCall {
        api.sendMessage(conversationId, SendMessageRequest(body)).toDomain()
    }
    override suspend fun createOffer(shopId: Long, productId: Long?, amount: Double, message: String?): AppResult<Offer> = safeApiCall {
        api.createOffer(CreateOfferRequest(shopId, productId, amount, message)).toDomain()
    }
    override suspend fun getMyOffers(): AppResult<List<Offer>> = safeApiCall {
        api.getMyOffers().map { it.toDomain() }
    }
    override suspend fun getShopOffers(shopId: Long): AppResult<List<Offer>> = safeApiCall {
        api.getShopOffers(shopId).map { it.toDomain() }
    }
    override suspend fun acceptOffer(id: Long): AppResult<Offer> = safeApiCall { api.acceptOffer(id).toDomain() }
    override suspend fun rejectOffer(id: Long): AppResult<Offer> = safeApiCall { api.rejectOffer(id).toDomain() }
    override suspend fun getBookmarks(): AppResult<List<Bookmark>> = safeApiCall {
        api.getBookmarks().map { it.toDomain() }
    }
    override suspend fun addBookmark(shopId: Long?, productId: Long?): AppResult<Bookmark> = safeApiCall {
        api.addBookmark(CreateBookmarkRequest(shopId, productId)).toDomain()
    }
    override suspend fun removeBookmark(id: Long): AppResult<Unit> = safeApiCall { api.removeBookmark(id) }
}
