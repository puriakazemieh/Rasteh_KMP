package com.kazemieh.common

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {

    @Serializable
    data object AuthGraph: Screen()

    @Serializable
    data object Login : Screen()

    @Serializable
    data object Register : Screen()

    @Serializable
    data object ForgotPassword : Screen()

    @Serializable
    data class ResetPassword(val token: String) : Screen()

    @Serializable
    data class HomeGraph(val showCart: Boolean = false) : Screen()

    @Serializable
    data class RastehSearch(
        val rastehId: Long,
        val rastehLabel: String,
        val locationId: Long,
        val locationName: String,
    ) : Screen()

    @Serializable
    data object BecomeVendor : Screen()

    @Serializable
    data object AdminShops : Screen()

    @Serializable
    data object VendorPanel : Screen()

    @Serializable
    data object AdminManage : Screen()

    @Serializable
    data class ShopDetail(val shopId: Long) : Screen()

    @Serializable
    data class MarketProductDetail(val productId: Long) : Screen()

    @Serializable
    data class Compare(val productId: Long) : Screen()

    @Serializable
    data class ChatThread(
        val conversationId: Long = 0,
        val shopId: Long = 0,
        val title: String = "",
    ) : Screen()

    @Serializable
    data object ChatList : Screen()

    @Serializable
    data object Bookmarks : Screen()

    @Serializable
    data object MarketOrders : Screen()

    @Serializable
    data object Deals : Screen()

    @Serializable
    data object PriceAlerts : Screen()

    @Serializable
    data object GiftCards : Screen()

    @Serializable
    data object Community : Screen()

    @Serializable
    data object Notifications : Screen()

    @Serializable
    data object FeaturesLauncher : Screen()

    @Serializable
    data object Appointments : Screen()

    @Serializable
    data object Returns : Screen()

    @Serializable
    data object VipSub : Screen()

    @Serializable
    data class Wayfind(val locationId: Long, val locationName: String) : Screen()

    @Serializable
    data object Events : Screen()

    @Serializable
    data object Referral : Screen()

    @Serializable
    data object BlogGraph : Screen()

    @Serializable
    data object BlogList : Screen()

    @Serializable
    data class BlogDetail(val slug: String) : Screen()

    @Serializable
    data object ProductsOverview : Screen()

    @Serializable
    data object Cart : Screen()

    @Serializable
    data object Categories : Screen()

    @Serializable
    data object Search : Screen()

    @Serializable
    data object Profile : Screen()

    @Serializable
    data object MyOrders : Screen()

    @Serializable
    data class OrderDetail(val id: Long) : Screen()

    @Serializable
    data class OrderTracking(val id: Long) : Screen()

    @Serializable
    data object Settings : Screen()

    @Serializable
    data object AdminPanel : Screen()

    @Serializable
    data class ManageProduct(val id: Long? = null) : Screen()

    @Serializable
    data object ManageOrders : Screen()

    @Serializable
    data object ManageOptions : Screen()

    @Serializable
    data object ManageDiscounts : Screen()

    @Serializable
    data object ManageWallets : Screen()

    @Serializable
    data object ManageWithdrawals : Screen()

    @Serializable
    data object ManageStories : Screen()

    @Serializable
    data object AdminBlogList : Screen()

    @Serializable
    data class ManageBlog(val id: Long? = null, val slug: String? = null) : Screen()

    @Serializable
    data object Wallet : Screen()

    @Serializable
    data object Favorites : Screen()

    @Serializable
    data class Checkout(val totalAmount: Double) : Screen()

    @Serializable
    data class PaymentCompleted(val success: Boolean, val error: String? = null) : Screen()

    @Serializable
    data class CategorySearch(val id: Long, val name: String) : Screen()

    @Serializable
    data object ContactUs : Screen()

    @Serializable
    data class ProductDetail(val slug: String) : Screen()

    @Serializable
    data object CustomerClub : Screen()

}
