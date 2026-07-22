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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kazemieh.common.AppResult
import com.kazemieh.common.util.toFaDigits
import com.kazemieh.designsystem.FontSize
import com.kazemieh.domain.marketplace.Shop
import org.koin.compose.viewmodel.koinViewModel
import org.jetbrains.compose.resources.painterResource
import com.kazemieh.designsystem.Resources

/**
 * مسیریابِ داخلِ محل (wayfind) · MVP: فهرستِ طبقاتِ محل و فروشگاه‌های هر طبقه.
 * فازِ بعدی: نقشهٔ گرافیکی با مختصاتِ mapX/mapY.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WayfindScreen(
    locationId: Long,
    locationName: String,
    navigateBack: () -> Unit,
    navigateToShop: (Long) -> Unit,
    viewModel: WayfindViewModel = koinViewModel(),
) {
    LaunchedEffect(locationId) { viewModel.load(locationId) }
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("مسیریابِ محل", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold)
                        Text(state.location?.name ?: locationName, fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت") }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            state.location?.let { loc ->
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer).padding(16.dp),
                    ) {
                        Text(loc.name, fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        loc.address?.takeIf { it.isNotBlank() }?.let {
                            Spacer(Modifier.height(4.dp))
                            Text(it, fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("${loc.floorCount.toFaDigits()} طبقه", fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }

            when (val s = state.shops) {
                is AppResult.Loading -> item { Box(Modifier.fillMaxWidth().height(120.dp), Alignment.Center) { CircularProgressIndicator() } }
                is AppResult.Error -> item { Text("خطا در دریافتِ فروشگاه‌ها", color = MaterialTheme.colorScheme.error, fontSize = FontSize.SMALL) }
                is AppResult.Success -> {
                    val byFloor = s.data.groupBy { it.floor?.takeIf { f -> f.isNotBlank() } ?: "طبقهٔ نامشخص" }
                    val floors = byFloor.keys.sortedWith(compareBy({ it == "طبقهٔ نامشخص" }, { it }))
                    if (s.data.isEmpty()) {
                        item { Text("فروشگاهی برای این محل ثبت نشده است", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = FontSize.SMALL) }
                    } else {
                        floors.forEach { floor ->
                            val shops = byFloor.getValue(floor)
                            item(key = "floor-$floor") {
                                Text(
                                    "$floor · ${shops.size.toFaDigits()} فروشگاه",
                                    fontSize = FontSize.REGULAR,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.padding(top = 4.dp),
                                )
                            }
                            shops.forEach { shop ->
                                item(key = "shop-${shop.id}") { WayfindShopRow(shop = shop, onClick = { navigateToShop(shop.id) }) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WayfindShopRow(shop: Shop, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant).clickable(onClick = onClick).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center,
        ) { Icon(painterResource(Resources.Icon.StorePin), contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(19.dp)) }
        Column(modifier = Modifier.weight(1f)) {
            Text(shop.name, fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            shop.category?.takeIf { it.isNotBlank() }?.let {
                Text(it, fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
