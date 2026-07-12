package com.kazemieh.bazaar

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.kazemieh.bazaar.component.ProductCard
import com.kazemieh.common.AppResult
import com.kazemieh.common.util.toFaDigits
import com.kazemieh.designsystem.FontSize
import com.kazemieh.domain.marketplace.Product
import com.kazemieh.domain.marketplace.Shop
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

/**
 * صفحهٔ فروشگاه (shopDetail v2): سربرگ + نشان‌کردن + «پیام»/«تماس»/«پیشنهادِ قیمت» + فهرستِ کالاها.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopDetailScreen(
    shopId: Long,
    navigateBack: () -> Unit,
    navigateToChat: (conversationId: Long, title: String) -> Unit,
    viewModel: ShopDetailViewModel = koinViewModel(),
) {
    LaunchedEffect(shopId) { viewModel.load(shopId) }
    val state by viewModel.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { eff ->
            when (eff) {
                is ShopDetailEffect.OpenChat -> navigateToChat(eff.conversationId, eff.title)
            }
        }
    }
    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    var showOffer by remember { mutableStateOf(false) }
    var showReview by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("فروشگاه", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleBookmark() }, enabled = !state.bookmarkBusy) {
                        val marked = state.bookmarkId != null
                        Icon(
                            if (marked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "نشان‌کردن",
                            tint = if (marked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
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
                is AppResult.Success -> Content(
                    shop = shop.data,
                    products = state.products,
                    reviews = state.reviews,
                    orderBusyProductId = state.orderBusyProductId,
                    onMessage = { viewModel.startChat(shop.data.name) },
                    onOffer = { showOffer = true },
                    onBuy = { viewModel.quickOrder(it) },
                    onAddReview = { showReview = true },
                )
            }
        }
    }

    if (showOffer) {
        OfferDialog(
            submitting = state.offerSubmitting,
            onDismiss = { showOffer = false },
            onSubmit = { amount, msg ->
                viewModel.submitOffer(amount, msg)
                showOffer = false
            },
        )
    }
    if (showReview) {
        ReviewDialog(
            submitting = state.reviewSubmitting,
            onDismiss = { showReview = false },
            onSubmit = { rating, comment ->
                viewModel.submitReview(rating, comment)
                showReview = false
            },
        )
    }
}

@Composable
private fun Content(
    shop: Shop,
    products: AppResult<List<Product>>,
    reviews: AppResult<List<com.kazemieh.domain.marketplace.Review>>,
    orderBusyProductId: Long?,
    onMessage: () -> Unit,
    onOffer: () -> Unit,
    onBuy: (Product) -> Unit,
    onAddReview: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { ShopHeader(shop = shop, onMessage = onMessage, onOffer = onOffer) }
        item {
            Text("کالاها", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
        when (products) {
            is AppResult.Loading -> item {
                Box(Modifier.fillMaxWidth().height(80.dp), Alignment.Center) { CircularProgressIndicator() }
            }
            is AppResult.Error -> item {
                Text("خطا در دریافتِ کالاها", color = MaterialTheme.colorScheme.error, fontSize = FontSize.SMALL)
            }
            is AppResult.Success -> {
                if (products.data.isEmpty()) {
                    item { Text("کالایی ثبت نشده است", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = FontSize.SMALL) }
                } else {
                    items(products.data, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            buying = orderBusyProductId == product.id,
                            onBuy = { onBuy(product) },
                        )
                    }
                }
            }
        }

        // ---- نظرات ----
        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("نظرات", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                TextButton(onClick = onAddReview) { Text("افزودنِ نظر") }
            }
        }
        when (reviews) {
            is AppResult.Loading -> item {
                Box(Modifier.fillMaxWidth().height(60.dp), Alignment.Center) { CircularProgressIndicator() }
            }
            is AppResult.Error -> item {
                Text("خطا در دریافتِ نظرات", color = MaterialTheme.colorScheme.error, fontSize = FontSize.SMALL)
            }
            is AppResult.Success -> {
                if (reviews.data.isEmpty()) {
                    item { Text("هنوز نظری ثبت نشده است", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = FontSize.SMALL) }
                } else {
                    items(reviews.data, key = { "r-${it.id}" }) { review -> ReviewRow(review) }
                }
            }
        }
    }
}

@Composable
private fun ReviewRow(review: com.kazemieh.domain.marketplace.Review) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(review.authorName ?: "کاربر", fontSize = FontSize.SMALL, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
            Text("★ ${review.rating.toFaDigits()}", fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.secondary)
        }
        review.comment?.let {
            Spacer(Modifier.height(4.dp))
            Text(it, fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ShopHeader(shop: Shop, onMessage: () -> Unit, onOffer: () -> Unit) {
    var showPhone by remember { mutableStateOf(false) }

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
            ) { Text(shop.emoji ?: "🏬", fontSize = FontSize.EXTRA_MEDIUM) }
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
                if (loc.isNotBlank()) Text(loc, fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                Button(onClick = onMessage, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.AutoMirrored.Filled.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.size(8.dp))
                    Text("پیام")
                }
            }
            OutlinedButton(onClick = { showPhone = true }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.size(8.dp))
                Text("تماس")
            }
        }
        if (shop.acceptsOffers) {
            Spacer(Modifier.height(10.dp))
            OutlinedButton(onClick = onOffer, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.Sell, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.size(8.dp))
                Text("پیشنهادِ قیمت")
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OfferDialog(
    submitting: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (amount: Double, message: String?) -> Unit,
) {
    var amount by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val amountValue = amount.toDoubleOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("پیشنهادِ قیمت", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                TextField(
                    value = amount,
                    onValueChange = { amount = it.filter { c -> c.isDigit() } },
                    placeholder = { Text("مبلغِ پیشنهادی (تومان)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                Spacer(Modifier.height(10.dp))
                TextField(
                    value = message,
                    onValueChange = { message = it },
                    placeholder = { Text("توضیح (اختیاری)") },
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { amountValue?.let { onSubmit(it, message.ifBlank { null }) } },
                enabled = !submitting && amountValue != null && amountValue > 0,
            ) { Text("ثبتِ پیشنهاد") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReviewDialog(
    submitting: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, comment: String?) -> Unit,
) {
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ثبتِ نظر", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Row {
                    (1..5).forEach { star ->
                        IconButton(onClick = { rating = star }) {
                            Icon(
                                if (star <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = { Text("توضیح (اختیاری)") },
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSubmit(rating, comment.ifBlank { null }) }, enabled = !submitting) {
                Text("ثبت")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } },
    )
}

@Composable
private fun Center(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) { content() }
}
