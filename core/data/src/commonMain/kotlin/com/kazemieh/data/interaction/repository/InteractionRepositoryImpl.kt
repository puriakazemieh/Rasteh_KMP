package com.kazemieh.data.interaction.repository

import com.kazemieh.common.AppResult
import com.kazemieh.data.interaction.source.InteractionDataSource
import com.kazemieh.domain.interaction.Bookmark
import com.kazemieh.domain.interaction.Conversation
import com.kazemieh.domain.interaction.InteractionRepository
import com.kazemieh.domain.interaction.Message
import com.kazemieh.domain.interaction.Offer

class InteractionRepositoryImpl(
    private val dataSource: InteractionDataSource
) : InteractionRepository {
    override suspend fun startConversation(shopId: Long): AppResult<Conversation> = dataSource.startConversation(shopId)
    override suspend fun getConversations(): AppResult<List<Conversation>> = dataSource.getConversations()
    override suspend fun getMessages(conversationId: Long): AppResult<List<Message>> = dataSource.getMessages(conversationId)
    override suspend fun sendMessage(conversationId: Long, body: String): AppResult<Message> = dataSource.sendMessage(conversationId, body)
    override suspend fun createOffer(shopId: Long, productId: Long?, amount: Double, message: String?): AppResult<Offer> =
        dataSource.createOffer(shopId, productId, amount, message)
    override suspend fun getMyOffers(): AppResult<List<Offer>> = dataSource.getMyOffers()
    override suspend fun getShopOffers(shopId: Long): AppResult<List<Offer>> = dataSource.getShopOffers(shopId)
    override suspend fun acceptOffer(id: Long): AppResult<Offer> = dataSource.acceptOffer(id)
    override suspend fun rejectOffer(id: Long): AppResult<Offer> = dataSource.rejectOffer(id)
    override suspend fun getBookmarks(): AppResult<List<Bookmark>> = dataSource.getBookmarks()
    override suspend fun addBookmark(shopId: Long?, productId: Long?): AppResult<Bookmark> = dataSource.addBookmark(shopId, productId)
    override suspend fun removeBookmark(id: Long): AppResult<Unit> = dataSource.removeBookmark(id)
}
