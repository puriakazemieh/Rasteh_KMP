package com.kazemieh.domain.advanced

data class Post(val id: Long, val authorName: String?, val body: String, val commentCount: Int, val createdAt: String?)
data class Comment(val id: Long, val postId: Long, val authorName: String?, val body: String, val createdAt: String?)
data class Subscription(val plan: String, val active: Boolean, val expiresAt: String?)
data class NotificationItem(val id: Long, val title: String, val body: String?, val read: Boolean, val createdAt: String?)
data class VendorAnalytics(val shopId: Long, val productCount: Int, val orderCount: Int, val revenue: Double, val pendingOffers: Int, val reviewsCount: Int, val rating: Double)
data class MallEvent(val id: Long, val locationId: Long?, val title: String, val description: String?, val eventDate: String, val createdAt: String?)
data class Referral(val code: String, val invitedCount: Long, val rewardStatus: String)
