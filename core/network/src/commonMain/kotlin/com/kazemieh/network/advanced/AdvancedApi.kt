package com.kazemieh.network.advanced

import com.kazemieh.network.advanced.dto.*
import com.kazemieh.network.common.safeApiCallRaw
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
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
    suspend fun getEvents(locationId: Long?): List<EventResponse>
    suspend fun getMyReferral(): ReferralResponse
    suspend fun redeemReferral(request: RedeemReferralRequest): ReferralResponse
    suspend fun getWarranties(): List<WarrantyResponse>
    suspend fun createWarranty(request: CreateWarrantyRequest): WarrantyResponse
    suspend fun getActivity(): List<ActivityItemResponse>
    suspend fun getParking(): List<ParkingResponse>
    suspend fun checkinParking(request: CheckinParkingRequest): ParkingResponse
    suspend fun payParking(id: Long): ParkingResponse
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
    override suspend fun getEvents(locationId: Long?): List<EventResponse> = safeApiCallRaw {
        client.get("/api/events") { if (locationId != null) parameter("locationId", locationId) }
    }
    override suspend fun getMyReferral(): ReferralResponse = safeApiCallRaw { client.get("/api/referral/me") }
    override suspend fun redeemReferral(request: RedeemReferralRequest): ReferralResponse = safeApiCallRaw {
        client.post("/api/referral/redeem") { contentType(ContentType.Application.Json); setBody(request) }
    }
    override suspend fun getWarranties(): List<WarrantyResponse> = safeApiCallRaw { client.get("/api/warranties/mine") }
    override suspend fun createWarranty(request: CreateWarrantyRequest): WarrantyResponse = safeApiCallRaw {
        client.post("/api/warranties") { contentType(ContentType.Application.Json); setBody(request) }
    }
    override suspend fun getActivity(): List<ActivityItemResponse> = safeApiCallRaw { client.get("/api/activity") }
    override suspend fun getParking(): List<ParkingResponse> = safeApiCallRaw { client.get("/api/parking/mine") }
    override suspend fun checkinParking(request: CheckinParkingRequest): ParkingResponse = safeApiCallRaw {
        client.post("/api/parking/checkin") { contentType(ContentType.Application.Json); setBody(request) }
    }
    override suspend fun payParking(id: Long): ParkingResponse = safeApiCallRaw { client.post("/api/parking/$id/pay") }
}
