package com.kazemieh.network.interaction

import com.kazemieh.network.common.safeApiCallRaw
import com.kazemieh.network.interaction.dto.request.CreateBookmarkRequest
import com.kazemieh.network.interaction.dto.request.CreateOfferRequest
import com.kazemieh.network.interaction.dto.request.SendMessageRequest
import com.kazemieh.network.interaction.dto.request.StartConversationRequest
import com.kazemieh.network.interaction.dto.response.BookmarkResponse
import com.kazemieh.network.interaction.dto.response.ConversationResponse
import com.kazemieh.network.interaction.dto.response.MessageResponse
import com.kazemieh.network.interaction.dto.response.OfferResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class InteractionApiImpl(
    private val client: HttpClient
) : InteractionApi {

    override suspend fun startConversation(request: StartConversationRequest): ConversationResponse = safeApiCallRaw {
        client.post("/api/chat/conversations") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun getConversations(): List<ConversationResponse> = safeApiCallRaw {
        client.get("/api/chat/conversations")
    }

    override suspend fun getMessages(conversationId: Long): List<MessageResponse> = safeApiCallRaw {
        client.get("/api/chat/conversations/$conversationId/messages")
    }

    override suspend fun sendMessage(conversationId: Long, request: SendMessageRequest): MessageResponse = safeApiCallRaw {
        client.post("/api/chat/conversations/$conversationId/messages") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun createOffer(request: CreateOfferRequest): OfferResponse = safeApiCallRaw {
        client.post("/api/offers") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun getMyOffers(): List<OfferResponse> = safeApiCallRaw {
        client.get("/api/offers/mine")
    }

    override suspend fun getShopOffers(shopId: Long): List<OfferResponse> = safeApiCallRaw {
        client.get("/api/offers/shop/$shopId")
    }

    override suspend fun acceptOffer(id: Long): OfferResponse = safeApiCallRaw {
        client.post("/api/offers/$id/accept")
    }

    override suspend fun rejectOffer(id: Long): OfferResponse = safeApiCallRaw {
        client.post("/api/offers/$id/reject")
    }

    override suspend fun getBookmarks(): List<BookmarkResponse> = safeApiCallRaw {
        client.get("/api/bookmarks")
    }

    override suspend fun addBookmark(request: CreateBookmarkRequest): BookmarkResponse = safeApiCallRaw {
        client.post("/api/bookmarks") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun removeBookmark(id: Long) = safeApiCallRaw<Unit> {
        client.delete("/api/bookmarks/$id")
    }
}
