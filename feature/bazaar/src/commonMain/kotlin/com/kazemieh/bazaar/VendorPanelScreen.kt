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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import com.kazemieh.common.AppResult
import com.kazemieh.common.util.toFaDigits
import com.kazemieh.common.util.toFaPrice
import com.kazemieh.designsystem.FontSize
import org.koin.compose.viewmodel.koinViewModel

private val VENDOR_TABS = listOf("داشبورد", "محصولات", "سفارش‌ها", "پیشنهادها")

/** پنلِ فروشنده (Vendor app v3): داشبورد/محصولات/سفارش‌ها/پیشنهادها. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorPanelScreen(
    navigateBack: () -> Unit,
    navigateToBecomeVendor: () -> Unit,
    viewModel: VendorPanelViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var showAdd by remember { mutableStateOf(false) }
    LaunchedEffect(state.message) { state.message?.let { snackbar.showSnackbar(it); viewModel.consumeMessage() } }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(state.shopName.ifBlank { "پنلِ فروشنده" }, fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = navigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت") } },
            )
        },
        floatingActionButton = {
            if (state.shopId != null && state.tab == 1) {
                FloatingActionButton(onClick = { showAdd = true }) { Icon(Icons.Default.Add, contentDescription = "افزودنِ محصول") }
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val shops = state.shops) {
                is AppResult.Loading -> Center { CircularProgressIndicator() }
                is AppResult.Error -> Center { Text("خطا در دریافتِ فروشگاه‌ها", color = MaterialTheme.colorScheme.error, fontSize = FontSize.REGULAR) }
                is AppResult.Success -> {
                    if (shops.data.isEmpty() || state.shopId == null) {
                        Center {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("هنوز فروشگاهی ندارید", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = FontSize.REGULAR)
                                Spacer(Modifier.height(12.dp))
                                OutlinedButton(onClick = navigateToBecomeVendor, shape = RoundedCornerShape(11.dp)) { Text("ثبتِ فروشگاه") }
                            }
                        }
                    } else {
                        Column(modifier = Modifier.fillMaxSize()) {
                            TabBar(tab = state.tab, onTab = viewModel::onTab)
                            when (state.tab) {
                                0 -> DashboardTab(state)
                                1 -> ProductsTab(state, onDelete = viewModel::deleteProduct)
                                2 -> OrdersTab(state, onStatus = viewModel::updateOrderStatus)
                                else -> OffersTab(state, onAccept = viewModel::acceptOffer, onReject = viewModel::rejectOffer)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAdd) {
        AddProductDialog(busy = state.busy, onDismiss = { showAdd = false }) { name, price, stock, condition ->
            viewModel.createProduct(name, price, stock, condition); showAdd = false
        }
    }
}

@Composable
private fun TabBar(tab: Int, onTab: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        VENDOR_TABS.forEachIndexed { i, title ->
            val sel = i == tab
            Box(
                modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant).clickable { onTab(i) }.padding(horizontal = 16.dp, vertical = 8.dp),
            ) { Text(title, fontSize = FontSize.SMALL, fontWeight = FontWeight.Bold, color = if (sel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }
}

@Composable
private fun DashboardTab(state: VendorPanelState) {
    val a = state.analytics
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (a == null) { Box(Modifier.fillMaxWidth().height(80.dp), Alignment.Center) { CircularProgressIndicator() }; return@Column }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard("محصولات", a.productCount.toFaDigits(), Modifier.weight(1f))
            StatCard("سفارش‌ها", a.orderCount.toFaDigits(), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard("پیشنهادِ معلق", a.pendingOffers.toFaDigits(), Modifier.weight(1f))
            StatCard("امتیاز", a.rating.toString().toFaDigits(), Modifier.weight(1f))
        }
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.primaryContainer).padding(18.dp)) {
            Column {
                Text("درآمدِ کل", fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(Modifier.height(6.dp))
                Text("${a.revenue.toLong().toFaPrice()} تومان", fontSize = FontSize.MEDIUM, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.surface).padding(16.dp)) {
        Text(value, fontSize = FontSize.MEDIUM, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = FontSize.EXTRA_SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ProductsTab(state: VendorPanelState, onDelete: (Long) -> Unit) {
    when (val p = state.products) {
        is AppResult.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        is AppResult.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) { Text("خطا", color = MaterialTheme.colorScheme.error) }
        is AppResult.Success -> {
            if (p.data.isEmpty()) Box(Modifier.fillMaxSize(), Alignment.Center) { Text("محصولی ندارید · با دکمهٔ + اضافه کنید", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = FontSize.SMALL) }
            else LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(p.data, key = { it.id }) { product ->
                    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.surface).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.height(2.dp))
                            Text("${product.price.toLong().toFaPrice()} تومان · موجودی ${product.stock.toFaDigits()}", fontSize = FontSize.EXTRA_SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { onDelete(product.id) }) { Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error) }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrdersTab(state: VendorPanelState, onStatus: (Long, String) -> Unit) {
    when (val o = state.orders) {
        is AppResult.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        is AppResult.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) { Text("خطا", color = MaterialTheme.colorScheme.error) }
        is AppResult.Success -> {
            if (o.data.isEmpty()) Box(Modifier.fillMaxSize(), Alignment.Center) { Text("سفارشی نیست", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = FontSize.SMALL) }
            else LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(o.data, key = { it.id }) { order ->
                    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.surface).padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("سفارشِ #${order.id.toFaDigits()}", modifier = Modifier.weight(1f), fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("${order.totalAmount.toLong().toFaPrice()} تومان", fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { onStatus(order.id, "PREPARING") }, shape = RoundedCornerShape(10.dp)) { Text("آماده‌سازی", fontSize = FontSize.EXTRA_SMALL) }
                            OutlinedButton(onClick = { onStatus(order.id, "COMPLETED") }, shape = RoundedCornerShape(10.dp)) { Text("تکمیل", fontSize = FontSize.EXTRA_SMALL) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OffersTab(state: VendorPanelState, onAccept: (Long) -> Unit, onReject: (Long) -> Unit) {
    when (val o = state.offers) {
        is AppResult.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        is AppResult.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) { Text("خطا", color = MaterialTheme.colorScheme.error) }
        is AppResult.Success -> {
            if (o.data.isEmpty()) Box(Modifier.fillMaxSize(), Alignment.Center) { Text("پیشنهادی نیست", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = FontSize.SMALL) }
            else LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(o.data, key = { it.id }) { offer ->
                    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.surface).padding(14.dp)) {
                        Text("${offer.customerName ?: "کاربر"} · ${offer.amount.toLong().toFaPrice()} تومان", fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        offer.message?.let { Spacer(Modifier.height(4.dp)); Text(it, fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        Spacer(Modifier.height(8.dp))
                        if (offer.status == "PENDING") {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(onClick = { onAccept(offer.id) }) { Text("پذیرش") }
                                OutlinedButton(onClick = { onReject(offer.id) }, shape = RoundedCornerShape(10.dp)) { Text("رد") }
                            }
                        } else {
                            Text(if (offer.status == "ACCEPTED") "پذیرفته‌شده" else "ردشده", fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddProductDialog(busy: Boolean, onDismiss: () -> Unit, onSubmit: (String, Double, Int, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("NEW") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("افزودنِ محصول", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                TextField(value = name, onValueChange = { name = it }, placeholder = { Text("نامِ محصول") }, singleLine = true)
                Spacer(Modifier.height(8.dp))
                TextField(value = price, onValueChange = { price = it.filter { c -> c.isDigit() } }, placeholder = { Text("قیمت (تومان)") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                Spacer(Modifier.height(8.dp))
                TextField(value = stock, onValueChange = { stock = it.filter { c -> c.isDigit() } }, placeholder = { Text("موجودی") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("NEW" to "نو", "USED" to "کارکرده").forEach { (v, lbl) ->
                        OutlinedButton(onClick = { condition = v }, shape = RoundedCornerShape(10.dp)) { Text(if (condition == v) "● $lbl" else lbl) }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { onSubmit(name, price.toDoubleOrNull() ?: 0.0, stock.toIntOrNull() ?: 0, condition) }, enabled = !busy && name.isNotBlank()) { Text("افزودن") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } },
    )
}

@Composable
private fun Center(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) { content() }
}
