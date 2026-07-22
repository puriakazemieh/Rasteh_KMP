package com.kazemieh.main.component

import com.kazemieh.common.Screen
import com.kazemieh.designsystem.Resources
import org.jetbrains.compose.resources.DrawableResource

/**
 * تب‌های نوارِ پایین — دقیقاً مطابقِ بستهٔ طراحیِ «Unified App» (راست‌به‌چپ):
 * خانه، جست‌وجو، پیام‌ها، ذخیره‌ها، پروفایل — پنج تب با آیکونِ خطیِ منطبق بر دیزاین.
 * ترتیبِ enum = ترتیبِ نمایش (در RTL اولین آیتم سمتِ راست).
 * برچسب‌ها هاردکدِ فارسی‌اند تا روی وب مستقل از locale درست نمایش داده شوند.
 */
enum class BottomBarDestination(
    val icon: DrawableResource,
    val faLabel: String,
    val screen: Screen
) {
    Home(
        icon = Resources.Icon.NavHome,
        faLabel = "خانه",
        screen = Screen.ProductsOverview
    ),
    Search(
        icon = Resources.Icon.NavSearch,
        faLabel = "جست‌وجو",
        screen = Screen.Search
    ),
    Messages(
        icon = Resources.Icon.NavChat,
        faLabel = "پیام‌ها",
        screen = Screen.ChatList
    ),
    Bookmarks(
        icon = Resources.Icon.NavBookmark,
        faLabel = "ذخیره‌ها",
        screen = Screen.Bookmarks
    ),
    Profile(
        icon = Resources.Icon.NavUser,
        faLabel = "پروفایل",
        screen = Screen.Categories
    )
}
