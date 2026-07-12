package com.kazemieh.bazaar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kazemieh.bazaar.component.ProductCard
import com.kazemieh.common.AppResult
import com.kazemieh.common.util.toFaDigits
import com.kazemieh.designsystem.FontSize
import com.kazemieh.domain.marketplace.Shop
import org.koin.compose.viewmodel.koinViewModel

/**
 * صفحهٔ فروشگاه (shopDetail v2): سربرگ + «پیام»/«تماس» + فهرستِ کالاها.
 * (چتِ واقعیِ «پیام» در فازِ ۳ فعال می‌شود.)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopDetailScreen(
    shopId: Long,
    navigateBack: () -> Unit,
    viewModel: ShopDetailViewModel = koinViewModel(),
) {
    LaunchedEffect(shopId) { viewModel.load(shopId) }
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("فروشگاه", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val shop = state.shop) {
                is AppResult.Loading -> Center { CircularProgressIndicator() }
                is AppResult.Error -> Center {
                    Text("خطا در دریافتِ فروشگاه", color = MaterialTheme.colorScheme.error, fontSize = FontSize.REGULAR)
                }
                is AppResult.Success -> Content(shop = shop.data, products = state.products)
            }
        }
    }
}

@Composable
private fun Content(shop: Shop, products: AppResult<List<com.kazemieh.domain.marketplace.Product>>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { ShopHeader(shop) }
        item {
            Text(
                text = "کالاها",
                fontSize = FontSize.EXTRA_REGULAR,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        when (products) {
            is AppResult.Loading -> item {
                Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is AppResult.Error -> item {
                Text("خطا در دریافتِ کالاها", color = MaterialTheme.colorScheme.error, fontSize = FontSize.SMALL)
            }
            is AppResult.Success -> {
                if (products.data.isEmpty()) {
                    item {
                        Text(
                            "کالایی ثبت نشده است",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = FontSize.SMALL,
                        )
                    }
                } else {
                    items(products.data, key = { it.id }) { product ->
                        ProductCard(product = product)
                    }
                }
            }
        }
    }
}

@Composable
private fun ShopHeader(shop: Shop) {
    var showPhone by remember { mutableStateOf(false) }
    var showChatNote by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) { Text(text = shop.emoji ?: "🏬", fontSize = FontSize.EXTRA_MEDIUM) }
            Spacer(Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(shop.name, fontSize = FontSize.MEDIUM, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    if (shop.verified) {
                        Spacer(Modifier.size(6.dp))
                        Text("✔", fontSize = FontSize.REGULAR, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(Modifier.height(4.dp))
                val loc = listOfNotNull(shop.rastehLabel, shop.locationName, shop.floor).joinToString(" · ")
                if (loc.isNotBlank()) {
                    Text(loc, fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    "★ ${shop.rating.toString().toFaDigits()} · ${shop.reviewsCount.toFaDigits()} نظر",
                    fontSize = FontSize.EXTRA_SMALL,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        shop.about?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (shop.hasChat) {
                Button(
                    onClick = { showChatNote = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(Icons.AutoMirrored.Filled.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.size(8.dp))
                    Text("پیام")
                }
            }
            OutlinedButton(
                onClick = { showPhone = true },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.size(8.dp))
                Text("تماس")
            }
        }

        if (showPhone) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = shop.phone?.let { "شمارهٔ تماس: ${it.toFaDigits()}" } ?: "شماره‌ای ثبت نشده است",
                fontSize = FontSize.SMALL,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        if (showChatNote) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = "چتِ درون‌برنامه در به‌روزرسانیِ بعدی فعال می‌شود.",
                fontSize = FontSize.SMALL,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Center(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) { content() }
}
