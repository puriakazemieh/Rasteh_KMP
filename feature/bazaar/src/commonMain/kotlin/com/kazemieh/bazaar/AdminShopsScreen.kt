package com.kazemieh.bazaar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kazemieh.common.AppResult
import com.kazemieh.designsystem.FontSize
import com.kazemieh.domain.marketplace.Shop
import org.koin.compose.viewmodel.koinViewModel

/** صفحهٔ صفِ تأییدِ فروشندگان برای ادمین. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminShopsScreen(
    navigateBack: () -> Unit,
    viewModel: AdminShopsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    val statuses = listOf(
        "PENDING" to "در انتظار",
        "APPROVED" to "تأییدشده",
        "SUSPENDED" to "معلق",
        "REJECTED" to "ردشده",
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // هدرِ تیرهٔ «پنلِ مدیریتِ پاساژ» مطابقِ دیزاین.
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFF1E1B2E)).padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = navigateBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = Color.White)
                }
                Spacer(Modifier.width(4.dp))
                Column {
                    Text("پنلِ مدیریتِ پاساژ", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("سطحِ دسترسی: مدیرِ کل پاساژ", fontSize = FontSize.EXTRA_SMALL, color = Color.White.copy(alpha = 0.7f))
                }
            }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                statuses.forEach { (value, label) ->
                    FilterChip(
                        selected = state.status == value,
                        onClick = { viewModel.handleIntent(AdminShopsIntent.OnStatusChange(value)) },
                        label = { Text(label, fontSize = FontSize.SMALL) },
                    )
                }
            }

            when (val shops = state.shops) {
                is AppResult.Loading -> CenterBox { CircularProgressIndicator() }
                is AppResult.Error -> CenterBox {
                    Text("خطا در دریافتِ فهرست", color = MaterialTheme.colorScheme.error, fontSize = FontSize.REGULAR)
                }
                is AppResult.Success -> {
                    if (shops.data.isEmpty()) {
                        CenterBox {
                            Text("موردی در این وضعیت نیست", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = FontSize.REGULAR)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(shops.data, key = { it.id }) { shop ->
                                ShopReviewCard(
                                    shop = shop,
                                    acting = state.actingId == shop.id,
                                    onApprove = { viewModel.handleIntent(AdminShopsIntent.Approve(shop.id)) },
                                    onReject = { viewModel.handleIntent(AdminShopsIntent.Reject(shop.id)) },
                                    onSuspend = { viewModel.handleIntent(AdminShopsIntent.Suspend(shop.id)) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShopReviewCard(
    shop: Shop,
    acting: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onSuspend: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
    ) {
        Text(text = shop.name, fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(4.dp))
        val subtitle = listOfNotNull(shop.rastehLabel, shop.locationName).joinToString(" · ")
        if (subtitle.isNotBlank()) {
            Text(text = subtitle, fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(2.dp))
        }
        Text(
            text = if (shop.isBuyable) "خرید آنلاین" else "فقط حضوری",
            fontSize = FontSize.EXTRA_SMALL,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(12.dp))

        if (acting) {
            Box(modifier = Modifier.fillMaxWidth().height(40.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.height(22.dp))
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                when (shop.status) {
                    "PENDING" -> {
                        FilledActionButton("تأیید", Color(0xFF157A3A), Color.White, Modifier.weight(1f), onApprove)
                        OutlineActionButton("رد", MaterialTheme.colorScheme.error, Modifier.weight(1f), onReject)
                    }
                    "APPROVED" -> {
                        OutlineActionButton("تعلیق", MaterialTheme.colorScheme.error, Modifier.fillMaxWidth(), onSuspend)
                    }
                    "SUSPENDED", "REJECTED" -> {
                        FilledActionButton("تأییدِ مجدد", Color(0xFF157A3A), Color.White, Modifier.fillMaxWidth(), onApprove)
                    }
                }
            }
        }
    }
}

@Composable
private fun FilledActionButton(label: String, bg: Color, fg: Color, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(10.dp)).background(bg).clickable(onClick = onClick).padding(vertical = 11.dp),
        contentAlignment = Alignment.Center,
    ) { Text(label, fontSize = FontSize.SMALL, fontWeight = FontWeight.Bold, color = fg) }
}

@Composable
private fun OutlineActionButton(label: String, tint: Color, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(10.dp)).border(1.dp, tint.copy(alpha = 0.6f), RoundedCornerShape(10.dp)).clickable(onClick = onClick).padding(vertical = 11.dp),
        contentAlignment = Alignment.Center,
    ) { Text(label, fontSize = FontSize.SMALL, fontWeight = FontWeight.Bold, color = tint) }
}

@Composable
private fun CenterBox(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) { content() }
}
