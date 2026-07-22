package com.kazemieh.main.component

import com.kazemieh.common.Screen
import com.kazemieh.designsystem.Resources
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/**
 * تب‌های نوارِ پایین — مطابقِ دیزاینِ بازارچه (راست‌به‌چپ): خانه، سبد، سفارش‌ها، پروفایل.
 * ترتیبِ enum = ترتیبِ نمایش (در RTL اولین آیتم سمتِ راست).
 */
enum class BottomBarDestination(
    val icon: DrawableResource,
    val title: StringResource,
    val faLabel: String,
    val screen: Screen
) {
    ProductsOverview(
        icon = Resources.Icon.Home,
        title = Resources.String.Home,
        faLabel = "خانه",
        screen = Screen.ProductsOverview
    ),
    Cart(
        icon = Resources.Icon.ShoppingCart,
        title = Resources.String.Cart,
        faLabel = "سبد",
        screen = Screen.Cart
    ),
    Orders(
        icon = Resources.Icon.Orders,
        title = Resources.String.Profile,
        faLabel = "سفارش‌ها",
        screen = Screen.MarketOrders
    ),
    More(
        icon = Resources.Icon.Person,
        title = Resources.String.Profile,
        faLabel = "پروفایل",
        screen = Screen.Categories
    )
}
