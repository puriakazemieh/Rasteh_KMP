package com.kazemieh.data.advanced

import com.kazemieh.common.AppResult
import com.kazemieh.domain.advanced.*
import com.kazemieh.network.advanced.AdvancedApi
import com.kazemieh.network.advanced.dto.CreateCommentRequest
import com.kazemieh.network.advanced.dto.CreatePostRequest
import com.kazemieh.network.advanced.dto.CreateWarrantyRequest
import com.kazemieh.network.advanced.dto.RedeemReferralRequest
import com.kazemieh.network.common.safeApiCall

class AdvancedRepositoryImpl(private val api: AdvancedApi) : AdvancedRepository {
    override suspend fun getPosts(): AppResult<List<Post>> = safeApiCall {
        api.getPosts().map { Post(it.id, it.authorName, it.body, it.commentCount, it.createdAt) }
    }
    override suspend fun createPost(body: String): AppResult<Post> = safeApiCall {
        api.createPost(CreatePostRequest(body)).let { Post(it.id, it.authorName, it.body, it.commentCount, it.createdAt) }
    }
    override suspend fun getComments(postId: Long): AppResult<List<Comment>> = safeApiCall {
        api.getComments(postId).map { Comment(it.id, it.postId, it.authorName, it.body, it.createdAt) }
    }
    override suspend fun addComment(postId: Long, body: String): AppResult<Comment> = safeApiCall {
        api.addComment(postId, CreateCommentRequest(body)).let { Comment(it.id, it.postId, it.authorName, it.body, it.createdAt) }
    }
    override suspend fun getSubscription(): AppResult<Subscription> = safeApiCall {
        api.getSubscription().let { Subscription(it.plan, it.active, it.expiresAt) }
    }
    override suspend fun subscribe(): AppResult<Subscription> = safeApiCall {
        api.subscribe().let { Subscription(it.plan, it.active, it.expiresAt) }
    }
    override suspend fun getNotifications(): AppResult<List<NotificationItem>> = safeApiCall {
        api.getNotifications().map { NotificationItem(it.id, it.title, it.body, it.read, it.createdAt) }
    }
    override suspend fun markNotificationRead(id: Long): AppResult<Unit> = safeApiCall { api.markNotificationRead(id) }
    override suspend fun getVendorAnalytics(shopId: Long): AppResult<VendorAnalytics> = safeApiCall {
        api.getVendorAnalytics(shopId).let { VendorAnalytics(it.shopId, it.productCount, it.orderCount, it.revenue, it.pendingOffers, it.reviewsCount, it.rating) }
    }
    override suspend fun getEvents(locationId: Long?): AppResult<List<MallEvent>> = safeApiCall {
        api.getEvents(locationId).map { MallEvent(it.id, it.locationId, it.title, it.description, it.eventDate, it.createdAt) }
    }
    override suspend fun getMyReferral(): AppResult<Referral> = safeApiCall {
        api.getMyReferral().let { Referral(it.code, it.invitedCount, it.rewardStatus) }
    }
    override suspend fun redeemReferral(code: String): AppResult<Referral> = safeApiCall {
        api.redeemReferral(RedeemReferralRequest(code)).let { Referral(it.code, it.invitedCount, it.rewardStatus) }
    }
    override suspend fun getWarranties(): AppResult<List<Warranty>> = safeApiCall {
        api.getWarranties().map { Warranty(it.id, it.title, it.serial, it.validUntil, it.createdAt) }
    }
    override suspend fun createWarranty(title: String, serial: String?): AppResult<Warranty> = safeApiCall {
        api.createWarranty(CreateWarrantyRequest(title, serial)).let { Warranty(it.id, it.title, it.serial, it.validUntil, it.createdAt) }
    }
    override suspend fun getActivity(): AppResult<List<ActivityItem>> = safeApiCall {
        api.getActivity().map { ActivityItem(it.type, it.title, it.subtitle, it.createdAt) }
    }
}
