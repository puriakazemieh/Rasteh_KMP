package com.kazemieh.bazaar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kazemieh.bazaar.component.ProductCard
import com.kazemieh.bazaar.component.ShopCard
import com.kazemieh.common.AppResult
import com.kazemieh.designsystem.FontSize
import com.kazemieh.designsystem.responsiveMaxWidth
import org.koin.compose.viewmodel.koinViewModel

/**
 * جست‌وجوی درون‌محلی (rastehSearch v3): دو تبِ فروشگاه/محصول.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RastehSearchScreen(
    rastehId: Long,
    rastehLabel: String,
    locationId: Long,
    locationName: String,
    navigateBack: () -> Unit,
    navigateToShop: (Long) -> Unit,
    navigateToProduct: (Long) -> Unit,
    navigateToWayfind: (Long, String) -> Unit = { _, _ -> },
    viewModel: RastehSearchViewModel = koinViewModel(),
) {
    LaunchedEffect(locationId, rastehId) {
        viewModel.load(locationId, rastehId.takeIf { it > 0 })
    }
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = rastehLabel, fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold)
                        Text(text = locationName, fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
                actions = {
                    IconButton(onClick = { navigateToWayfind(locationId, locationName) }) {
                        Icon(imageVector = Icons.Default.Place, contentDescription = "مسیریابِ محل")
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // تب‌های فروشگاه/محصول
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                HomeTab.entries.forEach { tab ->
                    val isSel = tab == state.tab
                    Box(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) MaterialTheme.colorScheme.surface else Color.Transparent)
                            .clickable { viewModel.onTab(tab) }.padding(vertical = 9.dp),
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

            Box(modifier = Modifier.fillMaxSize()) {
                if (state.tab == HomeTab.SHOPS) {
                    when (val s = state.shops) {
                        is AppResult.Loading -> Center { CircularProgressIndicator() }
                        is AppResult.Error -> Center { Text("خطا در دریافتِ فروشگاه‌ها", color = MaterialTheme.colorScheme.error, fontSize = FontSize.REGULAR) }
                        is AppResult.Success -> {
                            if (s.data.isEmpty()) Center { Text("فروشگاهی در این محل نیست", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = FontSize.REGULAR) }
                            else LazyColumn(
                                modifier = Modifier.fillMaxSize().responsiveMaxWidth(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                items(s.data, key = { it.id }) { shop -> ShopCard(shop = shop, onClick = { navigateToShop(shop.id) }) }
                            }
                        }
                    }
                } else {
                    when (val p = state.products) {
                        is AppResult.Loading -> Center { CircularProgressIndicator() }
                        is AppResult.Error -> Center { Text("خطا در دریافتِ محصولات", color = MaterialTheme.colorScheme.error, fontSize = FontSize.REGULAR) }
                        is AppResult.Success -> {
                            if (p.data.isEmpty()) Center { Text("محصولی در این محل نیست", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = FontSize.REGULAR) }
                            else LazyColumn(
                                modifier = Modifier.fillMaxSize().responsiveMaxWidth(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                items(p.data, key = { it.id }) { product ->
                                    ProductCard(product = product, onClick = { navigateToProduct(product.id) })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Center(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) { content() }
}
