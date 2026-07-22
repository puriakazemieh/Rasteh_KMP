package com.kazemieh.bazaar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Call
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.kazemieh.common.AppResult
import com.kazemieh.common.util.toFaDigits
import com.kazemieh.common.util.toFaPrice
import com.kazemieh.designsystem.FontSize
import com.kazemieh.designsystem.Resources
import org.jetbrains.compose.resources.painterResource
import com.kazemieh.domain.marketplace.Product
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

/**
 * صفحهٔ محصول (`listing` v3): گالری/عنوان/قیمت + کارتِ فروشنده + «دیدن در فروشگاه‌های دیگر»
 * + نوارِ اکشنِ چسبان (خرید/پیشنهاد/چت یا چت/تماس).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Long,
    navigateBack: () -> Unit,
    navigateToShop: (Long) -> Unit,
    navigateToCompare: (Long) -> Unit,
    navigateToChat: (conversationId: Long, title: String) -> Unit,
    viewModel: ProductDetailViewModel = koinViewModel(),
) {
    LaunchedEffect(productId) { viewModel.load(productId) }
    val state by viewModel.state.collectAsState()
    val dial = com.kazemieh.bazaar.util.rememberPhoneDialer()
    val snackbar = remember { SnackbarHostState() }
    var showOffer by remember { mutableStateOf(false) }
    var showReport by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { eff ->
            when (eff) { is ProductDetailEffect.OpenChat -> navigateToChat(eff.conversationId, eff.title) }
        }
    }
    LaunchedEffect(state.message) { state.message?.let { snackbar.showSnackbar(it); viewModel.consumeMessage() } }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("محصول", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = navigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت") } },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val p = state.product) {
                is AppResult.Loading -> Center { CircularProgressIndicator() }
                is AppResult.Error -> Center { Text("خطا در دریافتِ محصول", color = MaterialTheme.colorScheme.error, fontSize = FontSize.REGULAR) }
                is AppResult.Success -> {
                    val product = p.data
                    Column(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            item { Hero(product) }
                            item { PriceBlock(product) }
                            item {
                                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.surface).clickable { product.shopId?.let(navigateToShop) }.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                                            Icon(painterResource(Resources.Icon.StorePin), contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(22.dp))
                                        }
                                        Spacer(Modifier.size(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(product.shopName ?: "فروشگاه", fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                            Text("مشاهدهٔ فروشگاه ‹", fontSize = FontSize.EXTRA_SMALL, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                            product.description?.let { desc ->
                                item { Text(desc, fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            }
                            if (state.otherSellers.isNotEmpty()) {
                                item {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("دیدن در فروشگاه‌های دیگر", modifier = Modifier.weight(1f), fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        Text("مقایسه ‹", modifier = Modifier.clickable { navigateToCompare(productId) }, fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                itemsIndexed(state.otherSellers) { index, seller ->
                                    OtherSellerRow(seller = seller, cheapest = index == 0, onClick = { seller.shopId?.let(navigateToShop) })
                                }
                            }
                            item {
                                Spacer(Modifier.height(4.dp))
                                OutlinedButton(onClick = { showReport = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(11.dp)) {
                                    Text("گزارشِ تخلف", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                        StickyBar(
                            product = product,
                            busy = state.busy,
                            onBuy = { viewModel.buy(product) },
                            onOffer = { showOffer = true },
                            onChat = { viewModel.startChat(product.shopName ?: "فروشگاه") },
                            onCall = {
                                val phone = product.shopPhone
                                if (!phone.isNullOrBlank()) dial(phone) else product.shopId?.let(navigateToShop)
                            },
                        )
                    }
                }
            }
        }
    }

    if (showOffer) {
        OfferDialogPd(onDismiss = { showOffer = false }) { amount, msg -> viewModel.submitOffer(amount, msg, productId); showOffer = false }
    }
    if (showReport) {
        ReportDialogPd(onDismiss = { showReport = false }) { reason -> viewModel.report(reason, productId); showReport = false }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.itemsIndexed(
    list: List<Product>,
    content: @Composable (Int, Product) -> Unit,
) {
    items(list.size) { i -> content(i, list[i]) }
}

@Composable
private fun Hero(product: Product) {
    Box(
        modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Icon(painterResource(Resources.Icon.Orders), contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(56.dp))
    }
}

@Composable
private fun PriceBlock(product: Product) {
    Column {
        Text(product.name, fontSize = FontSize.MEDIUM, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(8.dp))
        if (product.price > 0) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("${product.price.toLong().toFaPrice()} تومان", fontSize = FontSize.MEDIUM, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                product.oldPrice?.let { Text(it.toLong().toFaPrice(), fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant, textDecoration = TextDecoration.LineThrough) }
            }
        } else {
            Text("نمایشی · برای قیمت تماس بگیرید", fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(4.dp))
        Text(if (product.condition == "USED") "کارکرده" else if (product.condition == "REFURBISHED") "بازسازی‌شده" else "نو", fontSize = FontSize.EXTRA_SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun OtherSellerRow(seller: Product, cheapest: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surface)
            .then(if (cheapest) Modifier.border(2.dp, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(12.dp)) else Modifier)
            .clickable(onClick = onClick).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
            Icon(painterResource(Resources.Icon.StorePin), contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.size(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(seller.shopName ?: "فروشگاه", fontSize = FontSize.SMALL, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            if (cheapest) Text("ارزان‌ترین", fontSize = FontSize.EXTRA_SMALL, color = MaterialTheme.colorScheme.tertiary)
        }
        Text("${seller.price.toLong().toFaPrice()}", fontSize = FontSize.SMALL, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun StickyBar(product: Product, busy: Boolean, onBuy: () -> Unit, onOffer: () -> Unit, onChat: () -> Unit, onCall: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (product.isBuyable) {
            Button(onClick = onBuy, enabled = !busy && product.purchasable, modifier = Modifier.weight(1f), shape = RoundedCornerShape(11.dp)) {
                if (busy) CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary) else Text("افزودن به سبد")
            }
            if (product.shopId != null) {
                OutlinedButton(onClick = onOffer, shape = RoundedCornerShape(11.dp)) { Text("پیشنهاد") }
            }
            IconButton(onClick = onChat) { Icon(Icons.AutoMirrored.Filled.Message, contentDescription = "چت", tint = MaterialTheme.colorScheme.primary) }
        } else {
            Button(onClick = onChat, modifier = Modifier.weight(1f), shape = RoundedCornerShape(11.dp)) {
                Icon(Icons.AutoMirrored.Filled.Message, contentDescription = null, modifier = Modifier.size(16.dp)); Spacer(Modifier.size(8.dp)); Text("چت با فروشگاه")
            }
            OutlinedButton(onClick = onCall, shape = RoundedCornerShape(11.dp)) {
                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp)); Spacer(Modifier.size(6.dp)); Text("تماس")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OfferDialogPd(onDismiss: () -> Unit, onSubmit: (Double, String?) -> Unit) {
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
        confirmButton = { TextButton(onClick = { amountValue?.let { onSubmit(it, message.ifBlank { null }) } }, enabled = amountValue != null && amountValue > 0) { Text("ثبت") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportDialogPd(onDismiss: () -> Unit, onSubmit: (String?) -> Unit) {
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
private fun Center(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) { content() }
}
