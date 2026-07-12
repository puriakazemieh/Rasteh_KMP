package com.kazemieh.domain.interaction

import com.kazemieh.common.AppResult

interface InteractionRepository {
    // chat
    suspend fun startConversation(shopId: Long): AppResult<Conversation>
    suspend fun getConversations(): AppResult<List<Conversation>>
    suspend fun getMessages(conversationId: Long): AppResult<List<Message>>
    suspend fun sendMessage(conversationId: Long, body: String): AppResult<Message>

    // offers
    suspend fun createOffer(shopId: Long, productId: Long?, amount: Double, message: String?): AppResult<Offer>
    suspend fun getMyOffers(): AppResult<List<Offer>>
    suspend fun getShopOffers(shopId: Long): AppResult<List<Offer>>
    suspend fun acceptOffer(id: Long): AppResult<Offer>
    suspend fun rejectOffer(id: Long): AppResult<Offer>

    // bookmarks
    suspend fun getBookmarks(): AppResult<List<Bookmark>>
    suspend fun addBookmark(shopId: Long?, productId: Long?): AppResult<Bookmark>
    suspend fun removeBookmark(id: Long): AppResult<Unit>
}
