package com.kazemieh.domain.advanced

import com.kazemieh.common.AppResult

interface AdvancedRepository {
    suspend fun getPosts(): AppResult<List<Post>>
    suspend fun createPost(body: String): AppResult<Post>
    suspend fun getComments(postId: Long): AppResult<List<Comment>>
    suspend fun addComment(postId: Long, body: String): AppResult<Comment>
    suspend fun getSubscription(): AppResult<Subscription>
    suspend fun subscribe(): AppResult<Subscription>
    suspend fun getNotifications(): AppResult<List<NotificationItem>>
    suspend fun markNotificationRead(id: Long): AppResult<Unit>
    suspend fun getVendorAnalytics(shopId: Long): AppResult<VendorAnalytics>
}
