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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kazemieh.common.AppResult
import com.kazemieh.common.util.toFaDigits
import com.kazemieh.common.util.toFaPrice
import com.kazemieh.designsystem.FontSize
import com.kazemieh.domain.marketplace.Order
import org.koin.compose.viewmodel.koinViewModel

private fun statusLabel(status: String): String = when (status) {
    "PENDING" -> "در انتظار"
    "CONFIRMED" -> "تأییدشده"
    "PREPARING" -> "در حالِ آماده‌سازی"
    "READY" -> "آمادهٔ تحویل"
    "COMPLETED" -> "تکمیل‌شده"
    "CANCELLED" -> "لغوشده"
    else -> status
}

/** سفارش‌های من (خریدار). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    navigateBack: () -> Unit,
    navigateToShop: (Long) -> Unit,
    viewModel: MyOrdersViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("سفارش‌های من", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is AppResult.Loading -> Center { CircularProgressIndicator() }
                is AppResult.Error -> Center { Text("خطا در دریافتِ سفارش‌ها", color = MaterialTheme.colorScheme.error, fontSize = FontSize.REGULAR) }
                is AppResult.Success -> {
                    if (s.data.isEmpty()) {
                        Center { Text("هنوز سفارشی ندارید", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = FontSize.REGULAR) }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(s.data, key = { it.id }) { order -> OrderCard(order = order, onShopClick = { order.shopId?.let(navigateToShop) }) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: Order, onShopClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("سفارشِ #${order.id.toFaDigits()}", fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
            Text(statusLabel(order.status), fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(4.dp))
        order.shopName?.let {
            Text(it, fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(10.dp))
        OrderStatusTimeline(order.status)
        Spacer(Modifier.height(10.dp))
        order.items.forEach { item ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                Text("${item.productName} ×${item.quantity.toFaDigits()}", fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                Text("${item.lineTotal.toLong().toFaPrice()}", fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "مجموع: ${order.totalAmount.toLong().toFaPrice()} تومان",
            fontSize = FontSize.REGULAR,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun Center(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) { content() }
}

/** تایم‌لاینِ رهگیریِ سفارش (tracking) — استنتاجِ کلاینت‌ساید از وضعیتِ فعلی. */
@Composable
private fun OrderStatusTimeline(status: String) {
    if (status == "CANCELLED") {
        Text("این سفارش لغو شده است.", fontSize = FontSize.SMALL, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
        return
    }
    val stages = listOf("PENDING" to "ثبت", "CONFIRMED" to "تأیید", "PREPARING" to "آماده‌سازی", "READY" to "آمادهٔ تحویل", "COMPLETED" to "تحویل")
    val current = stages.indexOfFirst { it.first == status }.let { if (it < 0) 0 else it }
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        stages.forEachIndexed { i, stage ->
            val reached = i <= current
            val color = if (reached) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(50)).background(color))
                Spacer(Modifier.height(4.dp))
                Text(
                    stage.second,
                    fontSize = FontSize.EXTRA_SMALL,
                    fontWeight = if (i == current) FontWeight.Bold else FontWeight.Normal,
                    color = if (reached) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
