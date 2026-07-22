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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    onAdminManageClick: () -> Unit = {},
    // بلااستفاده‌ها (سازگاریِ عقب‌رو با فراخوان‌کننده):
    onPriceAlertsClick: () -> Unit = {},
    onGiftCardsClick: () -> Unit = {},
    onCommunityClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
) {
    val colors = AppTheme.colors
    var role by remember { mutableStateOf(ProfileRole.CUSTOMER) }

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
                shopName = userName.ifBlank { "فروشگاهِ من" },
                onOrders = onMarketOrdersClick, onPanel = onVendorPanelClick, onDeals = onDealsClick,
            )
            ProfileRole.ADMIN -> AdminContent(
                onApprove = onAdminShopsClick, onManage = onAdminManageClick, onPanel = onAdminPanelClick,
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
private fun VendorContent(shopName: String, onOrders: () -> Unit, onPanel: () -> Unit, onDeals: () -> Unit) {
    val colors = AppTheme.colors
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
            Text("پنلِ مدیریتِ فروشگاه", fontFamily = AppFont(), fontSize = FontSize.EXTRA_SMALL, color = colors.onSurfaceVariant)
        }
        Box(Modifier.clip(RoundedCornerShape(8.dp)).background(colors.ok).padding(horizontal = 10.dp, vertical = 5.dp)) {
            Text("تأییدشده", fontFamily = AppFont(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
    Spacer(Modifier.height(14.dp))
    ActionGrid(
        ActionTileData(Resources.Icon.Orders, "سفارش‌ها", onOrders),
        ActionTileData(Resources.Icon.Categories, "مدیریتِ محصولات", onPanel),
        ActionTileData(Resources.Icon.Plus, "افزودنِ محصول", onPanel),
        ActionTileData(Resources.Icon.Dollar, "تخفیفِ دنبال‌کننده", onDeals),
        ActionTileData(Resources.Icon.Clock, "آمار و عملکرد", onPanel),
        ActionTileData(Resources.Icon.Edit, "ویرایشِ فروشگاه", onPanel),
    )
}

// ---------------------------------------------------------------------------
// محتوای ادمین
// ---------------------------------------------------------------------------

@Composable
private fun AdminContent(onApprove: () -> Unit, onManage: () -> Unit, onPanel: () -> Unit) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(colors.primary).clickable(onClick = onPanel).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(46.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
            Icon(painterResource(Resources.Icon.Unlock), null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text("مدیریتِ بازارچه", fontFamily = AppFont(), fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold, color = Color.White)
            Text("خوش آمدید، مدیرِ پاساژ", fontFamily = AppFont(), fontSize = FontSize.EXTRA_SMALL, color = Color.White.copy(alpha = 0.85f))
        }
    }
    Spacer(Modifier.height(14.dp))
    ActionGrid(
        ActionTileData(Resources.Icon.Checkmark, "تأییدِ فروشگاه‌ها", onApprove),
        ActionTileData(Resources.Icon.Warning, "گزارشِ تخلف", onManage),
        ActionTileData(Resources.Icon.Person, "سوپروایزرها", onManage),
        ActionTileData(Resources.Icon.Plus, "افزودنِ سریعِ فروشگاه", onManage),
    )
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

private data class ActionTileData(val icon: DrawableResource, val label: String, val onClick: () -> Unit)

@Composable
private fun ActionGrid(vararg tiles: ActionTileData) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        tiles.toList().chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { t -> Box(Modifier.weight(1f)) { ActionTile(t) } }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ActionTile(t: ActionTileData) {
    val colors = AppTheme.colors
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(colors.surface)
            .border(1.dp, colors.line, RoundedCornerShape(14.dp)).clickable(onClick = t.onClick).padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(11.dp)).background(colors.accentSoft), contentAlignment = Alignment.Center) {
            Icon(painterResource(t.icon), null, tint = colors.primary, modifier = Modifier.size(20.dp))
        }
        Text(t.label, fontFamily = AppFont(), fontSize = FontSize.SMALL, fontWeight = FontWeight.Medium, color = colors.onSurface)
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
