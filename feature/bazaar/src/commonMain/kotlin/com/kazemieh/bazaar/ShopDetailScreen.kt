package com.kazemieh.bazaar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.HorizontalDivider
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
import com.kazemieh.designsystem.AppTheme
import com.kazemieh.designsystem.FontSize
import com.kazemieh.designsystem.Resources
import com.kazemieh.designsystem.responsiveMaxWidth
import org.jetbrains.compose.resources.painterResource
import com.kazemieh.domain.marketplace.Product
import com.kazemieh.domain.marketplace.Review
import com.kazemieh.domain.marketplace.Shop
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

private val SHOP_TABS = listOf("محصولات", "حراجی", "ویترینو", "نظرات", "اطلاعات")

/**
 * صفحهٔ فروشگاه (shopDetail v3): کاور + بلوکِ هویت + اکشن‌ها + ۵ تب
 * (محصولات/حراجی/ویترینو/نظرات/اطلاعات).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopDetailScreen(
    shopId: Long,
    navigateBack: () -> Unit,
    navigateToProduct: (Long) -> Unit,
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
        state.message?.let { snackbar.showSnackbar(it); viewModel.consumeMessage() }
    }

    var showOffer by remember { mutableStateOf(false) }
    var showReview by remember { mutableStateOf(false) }
    var showReport by remember { mutableStateOf(false) }
    var tab by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("فروشگاه", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت") }
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
                is AppResult.Error -> Center { Text("خطا در دریافتِ فروشگاه", color = MaterialTheme.colorScheme.error, fontSize = FontSize.REGULAR) }
                is AppResult.Success -> Content(
                    shop = shop.data,
                    products = state.products,
                    reviews = state.reviews,
                    tab = tab,
                    onTab = { tab = it },
                    orderBusyProductId = state.orderBusyProductId,
                    onMessage = { viewModel.startChat(shop.data.name) },
                    onOffer = { showOffer = true },
                    onBuy = { viewModel.quickOrder(it) },
                    onProduct = navigateToProduct,
                    onAddReview = { showReview = true },
                    onReport = { showReport = true },
                )
            }
        }
    }

    if (showOffer) {
        OfferDialog(state.offerSubmitting, onDismiss = { showOffer = false }) { amount, msg ->
            viewModel.submitOffer(amount, msg); showOffer = false
        }
    }
    if (showReview) {
        ReviewDialog(state.reviewSubmitting, onDismiss = { showReview = false }) { rating, comment ->
            viewModel.submitReview(rating, comment); showReview = false
        }
    }
    if (showReport) {
        ReportDialog(onDismiss = { showReport = false }) { reason ->
            viewModel.reportShop(reason); showReport = false
        }
    }
}

@Composable
private fun Content(
    shop: Shop,
    products: AppResult<List<Product>>,
    reviews: AppResult<List<Review>>,
    tab: Int,
    onTab: (Int) -> Unit,
    orderBusyProductId: Long?,
    onMessage: () -> Unit,
    onOffer: () -> Unit,
    onBuy: (Product) -> Unit,
    onProduct: (Long) -> Unit,
    onAddReview: () -> Unit,
    onReport: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().responsiveMaxWidth(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item { ShopHeader(shop = shop, onMessage = onMessage, onOffer = onOffer) }
        item { TabRow(tab = tab, onTab = onTab) }

        when (tab) {
            0 -> productItems(products, orderBusyProductId, onBuy, onProduct) { true }
            1 -> productItems(products, orderBusyProductId, onBuy, onProduct) { p -> p.oldPrice?.let { it > p.price } ?: false }
            2 -> productItems(products, orderBusyProductId, onBuy, onProduct) { !it.purchasable }
            3 -> {
                item {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("نظرات", modifier = Modifier.weight(1f), fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        TextButton(onClick = onAddReview) { Text("افزودنِ نظر") }
                    }
                }
                reviewItems(reviews)
            }
            else -> item { InfoTab(shop = shop, onReport = onReport) }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.productItems(
    products: AppResult<List<Product>>,
    orderBusyProductId: Long?,
    onBuy: (Product) -> Unit,
    onProduct: (Long) -> Unit,
    filter: (Product) -> Boolean,
) {
    when (products) {
        is AppResult.Loading -> item { Box(Modifier.fillMaxWidth().height(80.dp), Alignment.Center) { CircularProgressIndicator() } }
        is AppResult.Error -> item { PadText("خطا در دریافتِ کالاها") }
        is AppResult.Success -> {
            val list = products.data.filter(filter)
            if (list.isEmpty()) item { PadText("موردی نیست") }
            else items(list, key = { it.id }) { product ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    ProductCard(product = product, buying = orderBusyProductId == product.id, onClick = { onProduct(product.id) }, onBuy = { onBuy(product) })
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.reviewItems(reviews: AppResult<List<Review>>) {
    when (reviews) {
        is AppResult.Loading -> item { Box(Modifier.fillMaxWidth().height(60.dp), Alignment.Center) { CircularProgressIndicator() } }
        is AppResult.Error -> item { PadText("خطا در دریافتِ نظرات") }
        is AppResult.Success -> {
            if (reviews.data.isEmpty()) item { PadText("هنوز نظری ثبت نشده است") }
            else items(reviews.data, key = { "r-${it.id}" }) { review ->
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.surface).padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(review.authorName ?: "کاربر", modifier = Modifier.weight(1f), fontSize = FontSize.SMALL, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("★ ${review.rating.toFaDigits()}", fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.secondary)
                    }
                    review.comment?.let {
                        Spacer(Modifier.height(4.dp))
                        Text(it, fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun ShopHeader(shop: Shop, onMessage: () -> Unit, onOffer: () -> Unit) {
    var showPhone by remember { mutableStateOf(false) }
    val dial = com.kazemieh.bazaar.util.rememberPhoneDialer()
    Column {
        // کاور رنگی
        Box(modifier = Modifier.fillMaxWidth().height(110.dp).background(MaterialTheme.colorScheme.primaryContainer))
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) { Icon(painterResource(Resources.Icon.StorePin), contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(30.dp)) }
                Spacer(Modifier.size(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(shop.name, fontSize = FontSize.MEDIUM, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        if (shop.verified) {
                            Spacer(Modifier.size(6.dp))
                            Icon(painterResource(Resources.Icon.Checkmark), contentDescription = "تأییدشده", tint = AppTheme.colors.ok, modifier = Modifier.size(15.dp))
                        }
                    }
                    val loc = listOfNotNull(shop.rastehLabel, shop.locationName, shop.floor).joinToString(" · ")
                    if (loc.isNotBlank()) { Spacer(Modifier.height(4.dp)); Text(loc, fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = AppTheme.colors.star, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.size(3.dp))
                        Text("${shop.rating.toString().toFaDigits()} · ${shop.reviewsCount.toFaDigits()} نظر", fontSize = FontSize.EXTRA_SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (shop.hasChat) {
                    Button(onClick = onMessage, modifier = Modifier.weight(1f), shape = RoundedCornerShape(11.dp)) {
                        Icon(Icons.AutoMirrored.Filled.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.size(8.dp)); Text("پیام به فروشگاه")
                    }
                }
                OutlinedButton(
                    onClick = {
                        showPhone = true
                        shop.phone?.let { dial(it) }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(11.dp),
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp)); Spacer(Modifier.size(8.dp)); Text("تماس")
                }
            }
            if (shop.acceptsOffers) {
                Spacer(Modifier.height(10.dp))
                OutlinedButton(onClick = onOffer, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(11.dp)) {
                    Icon(Icons.Default.Sell, contentDescription = null, modifier = Modifier.size(16.dp)); Spacer(Modifier.size(8.dp)); Text("پیشنهادِ قیمت")
                }
            }
            if (showPhone) {
                Spacer(Modifier.height(10.dp))
                Text(shop.phone?.let { "شمارهٔ تماس: ${it.toFaDigits()}" } ?: "شماره‌ای ثبت نشده است", fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun TabRow(tab: Int, onTab: (Int) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            SHOP_TABS.forEachIndexed { i, title ->
                val sel = i == tab
                Column(
                    modifier = Modifier.clickable { onTab(i) }.padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        title,
                        fontSize = FontSize.SMALL,
                        fontWeight = if (sel) FontWeight.Bold else FontWeight.Medium,
                        color = if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .width(if (sel) 20.dp else 0.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (sel) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent),
                    )
                }
            }
        }
        HorizontalDivider(color = AppTheme.colors.line)
    }
}

@Composable
private fun InfoTab(shop: Shop, onReport: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        shop.about?.let { InfoRow("درباره", it) }
        shop.workingHoursJson?.let { InfoRow("ساعاتِ کاری", it) }
        shop.address?.let { InfoRow("آدرس", it) }
        shop.phone?.let { InfoRow("تلفن", it.toFaDigits()) }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onReport, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(11.dp)) {
            Text("گزارشِ تخلف", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(label, fontSize = FontSize.EXTRA_SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(2.dp))
        Text(value, fontSize = FontSize.REGULAR, color = MaterialTheme.colorScheme.onSurface)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OfferDialog(submitting: Boolean, onDismiss: () -> Unit, onSubmit: (Double, String?) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val amountValue = amount.toDoubleOrNull()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("پیشنهادِ قیمت", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                TextField(value = amount, onValueChange = { amount = it.filter { c -> c.isDigit() } }, placeholder = { Text("مبلغِ پیشنهادی (تومان)") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                Spacer(Modifier.height(10.dp))
                TextField(value = message, onValueChange = { message = it }, placeholder = { Text("توضیح (اختیاری)") })
            }
        },
        confirmButton = { TextButton(onClick = { amountValue?.let { onSubmit(it, message.ifBlank { null }) } }, enabled = !submitting && amountValue != null && amountValue > 0) { Text("ثبتِ پیشنهاد") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReviewDialog(submitting: Boolean, onDismiss: () -> Unit, onSubmit: (Int, String?) -> Unit) {
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
                            Icon(if (star <= rating) Icons.Default.Star else Icons.Default.StarBorder, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                TextField(value = comment, onValueChange = { comment = it }, placeholder = { Text("توضیح (اختیاری)") })
            }
        },
        confirmButton = { TextButton(onClick = { onSubmit(rating, comment.ifBlank { null }) }, enabled = !submitting) { Text("ثبت") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportDialog(onDismiss: () -> Unit, onSubmit: (String?) -> Unit) {
    var reason by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("گزارشِ تخلف", fontWeight = FontWeight.Bold) },
        text = { TextField(value = reason, onValueChange = { reason = it }, placeholder = { Text("علتِ گزارش") }) },
        confirmButton = { TextButton(onClick = { onSubmit(reason.ifBlank { null }) }) { Text("ارسال") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } },
    )
}

@Composable
private fun PadText(text: String) {
    Text(text, modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = FontSize.SMALL)
}

@Composable
private fun Center(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) { content() }
}
