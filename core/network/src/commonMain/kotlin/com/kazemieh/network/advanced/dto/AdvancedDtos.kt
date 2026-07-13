package com.kazemieh.network.advanced.dto

import kotlinx.serialization.Serializable

@Serializable data class PostResponse(val id: Long, val userId: Long, val authorName: String?, val body: String, val commentCount: Int, val createdAt: String?)
@Serializable data class CommentResponse(val id: Long, val postId: Long, val userId: Long, val authorName: String?, val body: String, val createdAt: String?)
@Serializable data class CreatePostRequest(val body: String)
@Serializable data class CreateCommentRequest(val body: String)
@Serializable data class SubscriptionResponse(val plan: String, val active: Boolean, val expiresAt: String?)
@Serializable data class NotificationResponse(val id: Long, val title: String, val body: String?, val read: Boolean, val createdAt: String?)
@Serializable data class VendorAnalyticsResponse(val shopId: Long, val productCount: Int, val orderCount: Int, val revenue: Double, val pendingOffers: Int, val reviewsCount: Int, val rating: Double)
