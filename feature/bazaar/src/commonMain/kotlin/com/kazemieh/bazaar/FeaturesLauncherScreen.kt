package com.kazemieh.bazaar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kazemieh.designsystem.FontSize
import com.kazemieh.designsystem.AppTheme
import com.kazemieh.designsystem.Resources
import androidx.compose.foundation.layout.size
import org.jetbrains.compose.resources.painterResource
import kotlinx.coroutines.launch

/**
 * لانچرِ «قابلیت‌ها» (features): گریدِ دوستونهٔ کاشی‌ها.
 * کاشی‌های فعال → صفحهٔ مربوطه؛ بقیه «به‌زودی» را نشان می‌دهند.
 * `onOpen` کلیدِ کاشیِ فعال را می‌فرستد و ناوبری در لایهٔ navigation انجام می‌شود.
 */
private data class FeatureTile(val key: String, val label: String, val icon: org.jetbrains.compose.resources.DrawableResource, val enabled: Boolean)

private val FEATURE_TILES = listOf(
    FeatureTile("deals", "پیشنهادها و جوایز", Resources.Icon.Dollar, true),
    FeatureTile("pricealert", "هشدارِ قیمت", Resources.Icon.Warning, true),
    FeatureTile("giftcard", "کارتِ هدیه", Resources.Icon.Orders, true),
    FeatureTile("appointment", "نوبتِ بازدید", Resources.Icon.Clock, true),
    FeatureTile("returns", "بازگشتِ کالا", Resources.Icon.BackArrow, true),
    FeatureTile("vipsub", "بازارچه پلاس", Resources.Icon.Checkmark, true),
    FeatureTile("community", "انجمنِ محله", Resources.Icon.NavChat, true),
    FeatureTile("loyalty", "باشگاهِ وفاداری", Resources.Icon.Dollar, true),
    FeatureTile("events", "رویدادها", Resources.Icon.Clock, true),
    FeatureTile("referral", "دعوتِ دوستان", Resources.Icon.Person, true),
    FeatureTile("warranty", "ضمانت‌نامه", Resources.Icon.Checkmark, true),
    FeatureTile("activity", "فعالیت‌های من", Resources.Icon.Clock, true),
    FeatureTile("tracking", "ردیابیِ سفارش", Resources.Icon.MapPin, true),
    FeatureTile("parking", "پارکینگِ من", Resources.Icon.MapPin, true),
    FeatureTile("concierge", "دستیارِ خرید", Resources.Icon.NavChat, true),
    FeatureTile("live", "لایوشاپینگ", Resources.Icon.Warning, true),
    FeatureTile("escrow", "پرداختِ امانی", Resources.Icon.Unlock, true),
    FeatureTile("visualsearch", "جست‌وجوی تصویری", Resources.Icon.Search, true),
    FeatureTile("wayfind", "مسیریابِ پاساژ", Resources.Icon.MapPin, false),
    FeatureTile("stories", "استوریِ فروشگاه‌ها", Resources.Icon.Book, false),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeaturesLauncherScreen(
    navigateBack: () -> Unit,
    onOpen: (String) -> Unit,
) {
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("قابلیت‌ها", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت") }
                },
            )
        },
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(FEATURE_TILES, key = { it.key }) { tile ->
                FeatureTileCard(
                    tile = tile,
                    onClick = {
                        if (tile.enabled) onOpen(tile.key)
                        else scope.launch { snackbar.showSnackbar("به‌زودی") }
                    },
                )
            }
        }
    }
}

@Composable
private fun FeatureTileCard(tile: FeatureTile, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.1f)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .alpha(if (tile.enabled) 1f else 0.55f)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(AppTheme.colors.accentSoft),
            contentAlignment = Alignment.Center,
        ) { Icon(painterResource(tile.icon), contentDescription = null, tint = AppTheme.colors.primary, modifier = Modifier.size(24.dp)) }
        Spacer(Modifier.height(8.dp))
        Text(
            tile.label,
            fontSize = FontSize.SMALL,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        if (!tile.enabled) {
            Spacer(Modifier.height(4.dp))
            Text("به‌زودی", fontSize = FontSize.EXTRA_SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
