package com.kazemieh.network.advanced

import com.kazemieh.network.advanced.dto.*
import com.kazemieh.network.common.safeApiCallRaw
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface AdvancedApi {
    suspend fun getPosts(): List<PostResponse>
    suspend fun createPost(request: CreatePostRequest): PostResponse
    suspend fun getComments(postId: Long): List<CommentResponse>
    suspend fun addComment(postId: Long, request: CreateCommentRequest): CommentResponse
    suspend fun getSubscription(): SubscriptionResponse
    suspend fun subscribe(): SubscriptionResponse
    suspend fun getNotifications(): List<NotificationResponse>
    suspend fun markNotificationRead(id: Long)
    suspend fun getVendorAnalytics(shopId: Long): VendorAnalyticsResponse
}

class AdvancedApiImpl(private val client: HttpClient) : AdvancedApi {
    override suspend fun getPosts(): List<PostResponse> = safeApiCallRaw { client.get("/api/community") }
    override suspend fun createPost(request: CreatePostRequest): PostResponse = safeApiCallRaw {
        client.post("/api/community") { contentType(ContentType.Application.Json); setBody(request) }
    }
    override suspend fun getComments(postId: Long): List<CommentResponse> = safeApiCallRaw { client.get("/api/community/$postId/comments") }
    override suspend fun addComment(postId: Long, request: CreateCommentRequest): CommentResponse = safeApiCallRaw {
        client.post("/api/community/$postId/comments") { contentType(ContentType.Application.Json); setBody(request) }
    }
    override suspend fun getSubscription(): SubscriptionResponse = safeApiCallRaw { client.get("/api/subscription/me") }
    override suspend fun subscribe(): SubscriptionResponse = safeApiCallRaw { client.post("/api/subscription/subscribe") }
    override suspend fun getNotifications(): List<NotificationResponse> = safeApiCallRaw { client.get("/api/notifications") }
    override suspend fun markNotificationRead(id: Long) = safeApiCallRaw<Unit> { client.post("/api/notifications/$id/read") }
    override suspend fun getVendorAnalytics(shopId: Long): VendorAnalyticsResponse = safeApiCallRaw { client.get("/api/vendor/analytics/shop/$shopId") }
}
