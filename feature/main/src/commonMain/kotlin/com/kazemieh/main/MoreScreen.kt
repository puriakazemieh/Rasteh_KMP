package com.kazemieh.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.kazemieh.common.util.toFaDigits
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kazemieh.designsystem.AppFont
import com.kazemieh.designsystem.AppTheme
import com.kazemieh.designsystem.FontSize
import com.kazemieh.designsystem.Resources
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/** نقشِ فعالِ نمایشِ پروفایل. */
private enum class ProfileRole { CUSTOMER, VENDOR, ADMIN }

/**
 * تبِ پروفایل · دقیقاً مطابقِ بستهٔ طراحیِ «Unified App»:
 * سربرگِ کاربر → سوییچرِ نقش (خریدار/فروشنده/ادمین) → محتوای هر نقش.
 */
@Composable
fun MoreScreen(
    isLoggedIn: Boolean,
    isAdmin: Boolean = false,
    isVendor: Boolean = false,
    userName: String = "",
    userPhone: String = "",
    onLoginClick: () -> Unit,
    onLogout: () -> Unit = {},
    onEditProfileClick: () -> Unit,
    onCustomerClubClick: () -> Unit,
    onSupportClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAdminPanelClick: () -> Unit,
    onBecomeVendorClick: () -> Unit = {},
    onAdminShopsClick: () -> Unit = {},
    onChatsClick: () -> Unit = {},
    onBookmarksClick: () -> Unit = {},
    onMarketOrdersClick: () -> Unit = {},
    onDealsClick: () -> Unit = {},
    onActivityClick: () -> Unit = {},
    onWalletClick: () -> Unit = {},
    onReferralClick: () -> Unit = {},
    onFeaturesClick: () -> Unit = {},
    onVendorPanelClick: () -> Unit = {},
    onAddProductClick: () -> Unit = {},
    onAdminManageClick: () -> Unit = {},
    // بلااستفاده‌ها (سازگاریِ عقب‌رو با فراخوان‌کننده):
    onPriceAlertsClick: () -> Unit = {},
    onGiftCardsClick: () -> Unit = {},
    onCommunityClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
) {
    val colors = AppTheme.colors
    var role by remember { mutableStateOf(ProfileRole.CUSTOMER) }
    val dashVm: com.kazemieh.bazaar.ProfileDashboardViewModel = org.koin.compose.viewmodel.koinViewModel()
    val dash by dashVm.state.collectAsState()
    androidx.compose.runtime.LaunchedEffect(isLoggedIn, isVendor, isAdmin) {
        if (isLoggedIn) dashVm.load(isVendor = isVendor, isAdmin = isAdmin)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp, bottom = 28.dp)
    ) {
        if (!isLoggedIn) {
            LoginPromptCard(onClick = onLoginClick)
            Spacer(Modifier.height(16.dp))
            // میهمان فقط منوی عمومی را می‌بیند.
            MenuGroup {
                MenuRow(Resources.Icon.Categories, "امکانات ویژهٔ بازارچه", onFeaturesClick)
                MenuDivider()
                MenuRow(Resources.Icon.Settings, "تنظیمات حساب", onSettingsClick)
                MenuDivider()
                MenuRow(Resources.Icon.Edit, "پشتیبانی", onSupportClick)
            }
            return@Column
        }

        UserHeader(name = userName, phone = userPhone, onClick = onEditProfileClick)
        Spacer(Modifier.height(16.dp))

        if (isVendor || isAdmin) {
            Text("نمایشِ اپ به‌عنوان", fontFamily = AppFont(), fontSize = FontSize.SMALL, fontWeight = FontWeight.Bold, color = colors.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            RoleSwitcher(role = role, isVendor = isVendor, isAdmin = isAdmin, onRole = { role = it })
            Spacer(Modifier.height(16.dp))
        }

        when (role) {
            ProfileRole.CUSTOMER -> CustomerContent(
                onMarketOrdersClick, onBookmarksClick, onActivityClick, onFeaturesClick,
                onCustomerClubClick, onWalletClick, onReferralClick, onSettingsClick, onSupportClick,
                onBecomeVendorClick, showBecomeVendor = !isVendor && !isAdmin, onLogout = onLogout,
            )
            ProfileRole.VENDOR -> VendorContent(
                dash = dash,
                shopName = dash.vendorShop?.name ?: userName.ifBlank { "فروشگاهِ من" },
                onOrders = onMarketOrdersClick, onPanel = onVendorPanelClick, onDeals = onDealsClick,
                onAddProduct = onAddProductClick,
            )
            ProfileRole.ADMIN -> AdminContent(
                dash = dash,
                onApprove = { dashVm.approve(it) }, onReject = { dashVm.reject(it) },
                onApproveNav = onAdminShopsClick, onManage = onAdminManageClick, onPanel = onAdminPanelClick,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// سربرگ و سوییچرِ نقش
// ---------------------------------------------------------------------------

@Composable
private fun UserHeader(name: String, phone: String, onClick: () -> Unit) {
    val colors = AppTheme.colors
    val displayName = name.ifBlank { "کاربرِ راسته" }
    val initial = displayName.trim().firstOrNull()?.toString() ?: "؟"
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(64.dp).clip(CircleShape)
                .background(Brush.linearGradient(listOf(colors.primary, colors.accent2))),
            contentAlignment = Alignment.Center,
        ) {
            Text(initial, fontFamily = AppFont(), fontSize = FontSize.LARGE, fontWeight = FontWeight.ExtraBold, color = Color.White)
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(displayName, fontFamily = AppFont(), fontSize = FontSize.MEDIUM, fontWeight = FontWeight.ExtraBold, color = colors.onSurface)
            if (phone.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(phone, fontFamily = AppFont(), fontSize = FontSize.REGULAR, color = colors.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun RoleSwitcher(role: ProfileRole, isVendor: Boolean, isAdmin: Boolean, onRole: (ProfileRole) -> Unit) {
    val colors = AppTheme.colors
    val items = buildList {
        add(ProfileRole.CUSTOMER to "خریدار")
        if (isVendor) add(ProfileRole.VENDOR to "فروشنده")
        if (isAdmin) add(ProfileRole.ADMIN to "ادمین")
    }
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(colors.surfaceVariant).padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        items.forEach { (r, label) ->
            val sel = r == role
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                    .background(if (sel) colors.primary else Color.Transparent)
                    .clickable { onRole(r) }.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(label, fontFamily = AppFont(), fontSize = FontSize.SMALL, fontWeight = FontWeight.Bold,
                    color = if (sel) colors.onPrimary else colors.onSurfaceVariant)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// محتوای خریدار
// ---------------------------------------------------------------------------

@Composable
private fun CustomerContent(
    onOrders: () -> Unit, onFollowed: () -> Unit, onActivity: () -> Unit, onFeatures: () -> Unit,
    onReviews: () -> Unit, onWallet: () -> Unit, onReferral: () -> Unit, onSettings: () -> Unit, onSupport: () -> Unit,
    onBecomeVendor: () -> Unit, showBecomeVendor: Boolean, onLogout: () -> Unit,
) {
    MenuGroup {
        MenuRow(Resources.Icon.Orders, "کالاها و سفارش‌های من", onOrders)
        MenuDivider()
        MenuRow(Resources.Icon.StorePin, "فروشگاه‌های دنبال‌شده", onFollowed)
        MenuDivider()
        MenuRow(Resources.Icon.Clock, "فعالیت‌ها", onActivity)
        MenuDivider()
        MenuRow(Resources.Icon.Categories, "امکانات ویژهٔ بازارچه", onFeatures)
        MenuDivider()
        MenuRow(Resources.Icon.Checkmark, "امتیازها و نظرات من", onReviews)
        MenuDivider()
        MenuRow(Resources.Icon.Dollar, "کیف‌پول و امتیاز وفاداری", onWallet)
        MenuDivider()
        MenuRow(Resources.Icon.Person, "دعوت از دوستان", onReferral)
        MenuDivider()
        MenuRow(Resources.Icon.Settings, "تنظیماتِ حساب", onSettings)
        MenuDivider()
        MenuRow(Resources.Icon.Edit, "پشتیبانی", onSupport)
    }
    if (showBecomeVendor) {
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                .background(AppTheme.colors.accentSoft).clickable(onClick = onBecomeVendor).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(painterResource(Resources.Icon.StorePin), null, tint = AppTheme.colors.primary, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("فروشنده شوید", fontFamily = AppFont(), fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = AppTheme.colors.onSurface)
                Text("فروشگاهِ خود را در راسته ثبت کنید", fontFamily = AppFont(), fontSize = FontSize.EXTRA_SMALL, color = AppTheme.colors.onSurfaceVariant)
            }
        }
    }
    Spacer(Modifier.height(14.dp))
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onLogout).padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.AutoMirrored.Filled.Logout, null, tint = AppTheme.colors.sale, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text("خروج از حساب", fontFamily = AppFont(), fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = AppTheme.colors.sale)
    }
}

// ---------------------------------------------------------------------------
// محتوای فروشنده
// ---------------------------------------------------------------------------

@Composable
private fun VendorContent(
    dash: com.kazemieh.bazaar.ProfileDashboardState,
    shopName: String,
    onOrders: () -> Unit, onPanel: () -> Unit, onDeals: () -> Unit, onAddProduct: () -> Unit,
) {
    val colors = AppTheme.colors
    val verified = dash.vendorShop?.verified ?: false
    // کارتِ فروشگاه
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(colors.accentSoft).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(46.dp).clip(RoundedCornerShape(12.dp)).background(colors.surface), contentAlignment = Alignment.Center) {
            Icon(painterResource(Resources.Icon.StorePin), null, tint = colors.primary, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(shopName, fontFamily = AppFont(), fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold, color = colors.onSurface)
            Spacer(Modifier.height(3.dp))
            val rating = dash.vendorShop?.rating ?: 0.0
            val place = listOfNotNull(dash.vendorShop?.floor, dash.vendorShop?.locationName).joinToString("، ")
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (rating > 0) {
                    Icon(Icons.Filled.Star, null, tint = Color(0xFFF2A100), modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(rating.toString().toFaDigits(), fontFamily = AppFont(), fontSize = FontSize.EXTRA_SMALL, fontWeight = FontWeight.Bold, color = colors.onSurfaceVariant)
                    if (place.isNotBlank()) Text(" · ", fontFamily = AppFont(), fontSize = FontSize.EXTRA_SMALL, color = colors.onSurfaceVariant)
                }
                Text(place.ifBlank { "پنلِ مدیریتِ فروشگاه" }, fontFamily = AppFont(), fontSize = FontSize.EXTRA_SMALL, color = colors.onSurfaceVariant, maxLines = 1)
            }
        }
        if (verified) Box(Modifier.clip(RoundedCornerShape(8.dp)).background(colors.ok).padding(horizontal = 10.dp, vertical = 5.dp)) {
            Text("تأییدشده", fontFamily = AppFont(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
    Spacer(Modifier.height(12.dp))
    StatRow(
        StatData(dash.vendorOrderCount, "سفارش‌ها"),
        StatData(dash.myProducts.size, "محصولاتِ من"),
        StatData(dash.myProducts.count { !it.active }, "غیرفعال"),
    )
    Spacer(Modifier.height(12.dp))
    ActionGrid(
        ActionTileData(Resources.Icon.Orders, "سفارش‌ها", onOrders),
        ActionTileData(Resources.Icon.Categories, "مدیریتِ محصولات", onPanel),
        ActionTileData(Resources.Icon.Plus, "افزودنِ محصول", onAddProduct),
        ActionTileData(Resources.Icon.Dollar, "تخفیفِ دنبال‌کننده", onDeals),
        ActionTileData(Resources.Icon.Clock, "آمار و عملکرد", onPanel),
        ActionTileData(Resources.Icon.Edit, "ویرایشِ فروشگاه", onPanel),
        ActionTileData(Resources.Icon.MapPin, "کدِ QR فروشگاه", onPanel),
        columns = 3,
        centered = true,
    )
    if (dash.myProducts.isNotEmpty()) {
        Spacer(Modifier.height(16.dp))
        Text("آخرین محصولاتِ من", fontFamily = AppFont(), fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold, color = colors.onSurface)
        Spacer(Modifier.height(8.dp))
        MenuGroup {
            dash.myProducts.take(4).forEachIndexed { i, p ->
                if (i > 0) MenuDivider()
                Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onPanel).padding(horizontal = 14.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(34.dp).clip(RoundedCornerShape(9.dp)).background(colors.accentSoft), contentAlignment = Alignment.Center) {
                        Icon(painterResource(Resources.Icon.Orders), null, tint = colors.primary, modifier = Modifier.size(17.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(p.name, modifier = Modifier.weight(1f), fontFamily = AppFont(), fontSize = FontSize.SMALL, fontWeight = FontWeight.Medium, color = colors.onSurface, maxLines = 1)
                    Text(if (p.active) "فعال" else "غیرفعال", fontFamily = AppFont(), fontSize = FontSize.EXTRA_SMALL, color = if (p.active) colors.ok else colors.onSurfaceVariant)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// محتوای ادمین
// ---------------------------------------------------------------------------

@Composable
private fun AdminContent(
    dash: com.kazemieh.bazaar.ProfileDashboardState,
    onApprove: (Long) -> Unit, onReject: (Long) -> Unit,
    onApproveNav: () -> Unit, onManage: () -> Unit, onPanel: () -> Unit,
) {
    val colors = AppTheme.colors
    // کارتِ خوش‌آمدِ روشن + نشانِ تیرهٔ «مدیرِ کل پاساژ» (مطابقِ دیزاین).
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(colors.accentSoft).clickable(onClick = onPanel).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text("مدیریتِ پاساژ گلستان", fontFamily = AppFont(), fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold, color = colors.onSurface)
            Text("خوش آمدید، مدیرِ پاساژ", fontFamily = AppFont(), fontSize = FontSize.EXTRA_SMALL, color = colors.onSurfaceVariant)
        }
        Box(Modifier.clip(RoundedCornerShape(8.dp)).background(colors.onSurface).padding(horizontal = 10.dp, vertical = 5.dp)) {
            Text("مدیرِ کل پاساژ", fontFamily = AppFont(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.surface)
        }
    }
    Spacer(Modifier.height(12.dp))
    StatRow(
        StatData(dash.activeShopCount, "فروشگاهِ فعال"),
        StatData(dash.openReportCount, "گزارشِ باز"),
        StatData(dash.pendingShops.size, "در انتظارِ تأیید"),
    )
    Spacer(Modifier.height(12.dp))
    ActionGrid(
        ActionTileData(
            Resources.Icon.Checkmark, "تأییدِ فروشگاه‌ها", onApproveNav,
            subtitle = if (dash.pendingShops.isNotEmpty()) "${dash.pendingShops.size.toString().toFaDigits()} موردِ جدید" else null,
            subtitleColor = colors.sale,
        ),
        ActionTileData(
            Resources.Icon.Warning, "گزارشِ تخلف", onManage,
            subtitle = if (dash.openReportCount > 0) "${dash.openReportCount.toString().toFaDigits()} باز" else null,
            subtitleColor = colors.sale,
        ),
        ActionTileData(Resources.Icon.Plus, "افزودنِ سریعِ فروشگاه", onManage),
        ActionTileData(Resources.Icon.Person, "سوپروایزرها", onManage),
        columns = 2,
        centered = false,
    )
    if (dash.pendingShops.isNotEmpty()) {
        Spacer(Modifier.height(16.dp))
        Text("در انتظارِ تأیید", fontFamily = AppFont(), fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold, color = colors.onSurface)
        Spacer(Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            dash.pendingShops.take(5).forEach { shop ->
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(colors.surface)
                        .border(1.dp, colors.line, RoundedCornerShape(14.dp)).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(colors.accentSoft), contentAlignment = Alignment.Center) {
                        Icon(painterResource(Resources.Icon.StorePin), null, tint = colors.primary, modifier = Modifier.size(19.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(shop.name, fontFamily = AppFont(), fontSize = FontSize.SMALL, fontWeight = FontWeight.Bold, color = colors.onSurface, maxLines = 1)
                        val sub = listOfNotNull(shop.rastehLabel, shop.floor).joinToString(" · ")
                        if (sub.isNotBlank()) Text(sub, fontFamily = AppFont(), fontSize = FontSize.EXTRA_SMALL, color = colors.onSurfaceVariant, maxLines = 1)
                    }
                    if (dash.busyShopId == shop.id) {
                        androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = colors.primary)
                    } else {
                        Box(Modifier.clip(RoundedCornerShape(8.dp)).background(colors.ok).clickable { onApprove(shop.id) }.padding(horizontal = 12.dp, vertical = 7.dp)) {
                            Text("بررسی", fontFamily = AppFont(), fontSize = FontSize.EXTRA_SMALL, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(Modifier.width(6.dp))
                        Box(Modifier.clip(RoundedCornerShape(8.dp)).background(colors.sale.copy(alpha = 0.12f)).clickable { onReject(shop.id) }.padding(horizontal = 10.dp, vertical = 7.dp)) {
                            Text("رد", fontFamily = AppFont(), fontSize = FontSize.EXTRA_SMALL, fontWeight = FontWeight.Bold, color = colors.sale)
                        }
                    }
                }
            }
        }
    }
}

private data class StatData(val value: Int, val label: String)

@Composable
private fun StatRow(vararg stats: StatData) {
    val colors = AppTheme.colors
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        stats.forEach { s ->
            Column(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).background(colors.surface)
                    .border(1.dp, colors.line, RoundedCornerShape(14.dp)).padding(vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(s.value.toString().toFaDigits(), fontFamily = AppFont(), fontSize = FontSize.MEDIUM, fontWeight = FontWeight.ExtraBold, color = colors.primary)
                Spacer(Modifier.height(2.dp))
                Text(s.label, fontFamily = AppFont(), fontSize = FontSize.EXTRA_SMALL, color = colors.onSurfaceVariant)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// اجزای مشترک
// ---------------------------------------------------------------------------

@Composable
private fun MenuGroup(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(AppTheme.colors.surface).border(1.dp, AppTheme.colors.line, RoundedCornerShape(16.dp)),
    ) { content() }
}

@Composable
private fun MenuDivider() {
    HorizontalDivider(color = AppTheme.colors.line, modifier = Modifier.padding(horizontal = 14.dp))
}

@Composable
private fun MenuRow(icon: DrawableResource, title: String, onClick: () -> Unit) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 14.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(icon), null, tint = colors.primary, modifier = Modifier.size(21.dp))
        Spacer(Modifier.width(14.dp))
        Text(title, modifier = Modifier.weight(1f), fontFamily = AppFont(), fontSize = FontSize.REGULAR, fontWeight = FontWeight.Medium, color = colors.onSurface)
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = colors.onSurfaceVariant, modifier = Modifier.size(18.dp))
    }
}

private data class ActionTileData(
    val icon: DrawableResource,
    val label: String,
    val onClick: () -> Unit,
    val subtitle: String? = null,
    val subtitleColor: androidx.compose.ui.graphics.Color? = null,
)

@Composable
private fun ActionGrid(vararg tiles: ActionTileData, columns: Int = 2, centered: Boolean = true) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        tiles.toList().chunked(columns).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { t -> Box(Modifier.weight(1f)) { ActionTile(t, centered) } }
                repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun ActionTile(t: ActionTileData, centered: Boolean) {
    val colors = AppTheme.colors
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(colors.surface)
            .border(1.dp, colors.line, RoundedCornerShape(14.dp)).clickable(onClick = t.onClick).padding(14.dp),
        horizontalAlignment = if (centered) Alignment.CenterHorizontally else Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(Modifier.size(38.dp).clip(RoundedCornerShape(11.dp)).background(colors.accentSoft), contentAlignment = Alignment.Center) {
            Icon(painterResource(t.icon), null, tint = colors.primary, modifier = Modifier.size(19.dp))
        }
        Text(
            t.label, fontFamily = AppFont(), fontSize = FontSize.SMALL, fontWeight = FontWeight.Medium, color = colors.onSurface,
            textAlign = if (centered) TextAlign.Center else TextAlign.Start, maxLines = 2,
        )
        if (t.subtitle != null) {
            Text(t.subtitle, fontFamily = AppFont(), fontSize = FontSize.EXTRA_SMALL, fontWeight = FontWeight.Bold, color = t.subtitleColor ?: colors.onSurfaceVariant)
        }
    }
}

@Composable
private fun LoginPromptCard(onClick: () -> Unit) {
    val colors = AppTheme.colors
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(listOf(colors.primary, colors.accent2)))
            .clickable(onClick = onClick).padding(22.dp),
    ) {
        Text("ورود / ثبت‌نام", fontFamily = AppFont(), fontSize = FontSize.MEDIUM, fontWeight = FontWeight.ExtraBold, color = Color.White)
        Spacer(Modifier.height(4.dp))
        Text("برای دسترسی به حسابِ کاربری وارد شوید", fontFamily = AppFont(), fontSize = FontSize.SMALL, color = Color.White.copy(alpha = 0.9f))
    }
}
