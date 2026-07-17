package com.kazemieh.bazaar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kazemieh.bazaar.component.LocationPickerSheet
import com.kazemieh.bazaar.component.ProductCard
import com.kazemieh.bazaar.component.RastehCard
import com.kazemieh.bazaar.component.ShopCard
import com.kazemieh.common.AppResult
import com.kazemieh.common.util.toFaPrice
import com.kazemieh.designsystem.FontSize
import com.kazemieh.domain.interaction.Bookmark
import com.kazemieh.domain.marketplace.Product
import com.kazemieh.domain.marketplace.Shop
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

/**
 * خانهٔ v3 (design_handoff_final): سرچ + گریدِ راسته (۵ ستون) + تب‌های فروشگاه/محصول
 * + نشان‌شده‌ها + جدیدترین. بدونِ سربرگِ سنگین (search-first).
 */
@Composable
fun RastehHomeScreen(
    navigateToRastehSearch: (rastehId: Long, rastehLabel: String, locationId: Long, locationName: String) -> Unit,
    navigateToShop: (Long) -> Unit,
    navigateToProduct: (Long) -> Unit,
    navigateToBookmarks: () -> Unit,
    viewModel: RastehHomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is RastehHomeEffect.NavigateToRastehSearch ->
                    navigateToRastehSearch(effect.rastehId, effect.rastehLabel, effect.locationId, effect.locationName)
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        // سرچ‌بار
        item {
            SearchField(
                query = state.query,
                onQueryChange = { viewModel.handleIntent(RastehHomeIntent.OnQueryChange(it)) },
            )
        }

        // گریدِ راسته‌ها (۵ ستونه)
        item { SectionTitle("راسته‌های بازارچه") }
        item {
            when (val rastehs = state.rastehs) {
                is AppResult.Loading -> CenterBox(90) { CircularProgressIndicator() }
                is AppResult.Error -> CenterBox(90) {
                    Text("خطا در دریافتِ راسته‌ها", color = MaterialTheme.colorScheme.error, fontSize = FontSize.REGULAR)
                }
                is AppResult.Success -> {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        rastehs.data.chunked(5).forEach { rowItems ->
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                rowItems.forEach { rasteh ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        RastehCard(
                                            rasteh = rasteh,
                                            onClick = { viewModel.handleIntent(RastehHomeIntent.OnRastehClick(rasteh)) },
                                        )
                                    }
                                }
                                repeat(5 - rowItems.size) { Spacer(Modifier.weight(1f)) }
                            }
                        }
                    }
                }
            }
        }

        // تب‌های سگمنتد فروشگاه/محصول
        item {
            Spacer(Modifier.height(12.dp))
            SegmentedTabs(
                selected = state.homeTab,
                onSelect = { viewModel.handleIntent(RastehHomeIntent.OnHomeTab(it)) },
            )
        }

        // نشان‌شده‌ها (فقط فروشگاه‌های نشان‌شده)
        val shopBookmarks = state.bookmarks.filter { it.shopId != null }
        if (shopBookmarks.isNotEmpty()) {
            item {
                SectionHeaderWithAll(title = "نشان‌شده‌ها", onAll = navigateToBookmarks)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(shopBookmarks, key = { "bm-${it.id}" }) { bm ->
                        BookmarkChip(bm = bm, onClick = { bm.shopId?.let(navigateToShop) })
                    }
                }
            }
        }

        // به‌تازگی دیده‌شده (محلی)
        if (state.recentProducts.isNotEmpty()) {
            item {
                SectionTitle("به‌تازگی دیده‌شده")
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(state.recentProducts, key = { "rv-${it.id}" }) { product ->
                        RecentlyViewedChip(product = product, onClick = { navigateToProduct(product.id) })
                    }
                }
            }
        }

        // جدیدترین‌ها (بر اساسِ تب)
        item { SectionTitle(if (state.homeTab == HomeTab.SHOPS) "جدیدترین فروشگاه‌ها" else "جدیدترین محصولات") }

        if (state.homeTab == HomeTab.SHOPS) {
            when (val shops = state.newestShops) {
                is AppResult.Loading -> item { CenterBox(80) { CircularProgressIndicator() } }
                is AppResult.Error -> item { PadText("خطا در دریافتِ فروشگاه‌ها") }
                is AppResult.Success -> {
                    if (shops.data.isEmpty()) item { PadText("فروشگاهی نیست") }
                    else items(shops.data, key = { "s-${it.id}" }) { shop ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            ShopCard(shop = shop, onClick = { navigateToShop(shop.id) })
                        }
                    }
                }
            }
        } else {
            when (val products = state.newestProducts) {
                is AppResult.Loading -> item { CenterBox(80) { CircularProgressIndicator() } }
                is AppResult.Error -> item { PadText("خطا در دریافتِ محصولات") }
                is AppResult.Success -> {
                    if (products.data.isEmpty()) item { PadText("محصولی نیست") }
                    else items(products.data.chunked(2), key = { row -> "p-${row.first().id}" }) { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            rowItems.forEach { product ->
                                Box(modifier = Modifier.weight(1f)) {
                                    ProductCard(product = product, onClick = { navigateToProduct(product.id) })
                                }
                            }
                            if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    val sheetRasteh = state.sheetRasteh
    if (sheetRasteh != null) {
        LocationPickerSheet(
            rastehLabel = sheetRasteh.label,
            locations = state.locations,
            onDismiss = { viewModel.handleIntent(RastehHomeIntent.DismissSheet) },
            onLocationClick = { viewModel.handleIntent(RastehHomeIntent.OnLocationClick(it)) },
        )
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp).clip(RoundedCornerShape(11.dp)),
        placeholder = { Text("جست‌وجوی فروشگاه یا محصول…", fontSize = FontSize.REGULAR) },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
    )
}

@Composable
private fun SegmentedTabs(selected: HomeTab, onSelect: (HomeTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        HomeTab.entries.forEach { tab ->
            val isSel = tab == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSel) MaterialTheme.colorScheme.surface else Color.Transparent)
                    .clickable { onSelect(tab) }
                    .padding(vertical = 9.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (tab == HomeTab.SHOPS) "فروشگاه‌ها" else "محصولات",
                    fontSize = FontSize.SMALL,
                    fontWeight = FontWeight.Bold,
                    color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun BookmarkChip(bm: Bookmark, onClick: () -> Unit) {
    Column(
        modifier = Modifier.width(64.dp).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(58.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) { Text(bm.shopEmoji ?: "🏬", fontSize = FontSize.MEDIUM) }
        Spacer(Modifier.height(4.dp))
        Text(
            text = bm.shopName ?: "فروشگاه",
            fontSize = FontSize.EXTRA_SMALL,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
        )
    }
}

@Composable
private fun RecentlyViewedChip(product: Product, onClick: () -> Unit) {
    Column(
        modifier = Modifier.width(96.dp).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) { Text(product.emoji ?: "🛍️", fontSize = FontSize.MEDIUM) }
        Spacer(Modifier.height(4.dp))
        Text(
            text = product.name,
            fontSize = FontSize.EXTRA_SMALL,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
        )
        Text(
            text = "${product.price.toLong().toFaPrice()} تومان",
            fontSize = FontSize.EXTRA_SMALL,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        fontSize = FontSize.EXTRA_REGULAR,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun SectionHeaderWithAll(title: String, onAll: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, modifier = Modifier.weight(1f), fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text("همه ‹", modifier = Modifier.clickable(onClick = onAll), fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun PadText(text: String) {
    Text(text, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = FontSize.SMALL)
}

@Composable
private fun CenterBox(heightDp: Int, content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(heightDp.dp), contentAlignment = Alignment.Center) { content() }
}
