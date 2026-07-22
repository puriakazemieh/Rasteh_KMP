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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.unit.dp
import com.kazemieh.common.AppResult
import com.kazemieh.common.util.toFaDigits
import com.kazemieh.common.util.toFaPrice
import com.kazemieh.designsystem.FontSize
import org.koin.compose.viewmodel.koinViewModel

/** پرداختِ امانی (escrow): بازکردنِ امانت + آزادسازی پس از دریافت. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EscrowScreen(
    navigateBack: () -> Unit,
    viewModel: EscrowViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var amount by remember { mutableStateOf("") }
    LaunchedEffect(state.message) { state.message?.let { snackbar.showSnackbar(it); viewModel.consumeMessage() } }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("پرداختِ امانی", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold) },
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
            item {
                Text("مبلغ را نزدِ بازارچه امانت بگذارید؛ پس از دریافتِ کالا آزادش کنید.", fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(
                        value = amount,
                        onValueChange = { v -> amount = v.filter { it.isDigit() } },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("مبلغ (تومان)") },
                        singleLine = true,
                    )
                    Button(
                        onClick = { amount.toDoubleOrNull()?.let { viewModel.open(it, null); amount = "" } },
                        enabled = !state.busy && amount.toDoubleOrNull() != null,
                        shape = RoundedCornerShape(10.dp),
                    ) { Text("امانت") }
                }
            }
            item {
                Spacer(Modifier.height(4.dp))
                Text("امانت‌های من", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
            when (val e = state.items) {
                is AppResult.Loading -> item { Box(Modifier.fillMaxWidth().height(60.dp), Alignment.Center) { CircularProgressIndicator() } }
                is AppResult.Error -> item { Text("خطا در دریافت", color = MaterialTheme.colorScheme.error, fontSize = FontSize.SMALL) }
                is AppResult.Success -> {
                    if (e.data.isEmpty()) item { Text("امانتی ندارید", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = FontSize.SMALL) }
                    else items(e.data, key = { it.id }) { es ->
                        Column(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant).padding(16.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${es.amount.toLong().toFaPrice()} تومان", fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                                Text(statusLabelEscrow(es.status), fontSize = FontSize.SMALL, fontWeight = FontWeight.Bold, color = if (es.status == "RELEASED") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                            }
                            es.orderId?.let {
                                Spacer(Modifier.height(2.dp))
                                Text("سفارش #${it.toFaDigits()}", fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (es.status == "HELD") {
                                Spacer(Modifier.height(8.dp))
                                OutlinedButton(onClick = { viewModel.release(es.id) }, enabled = !state.busy, shape = RoundedCornerShape(10.dp)) { Text("تأییدِ دریافت و آزادسازی") }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun statusLabelEscrow(status: String): String = when (status.uppercase()) {
    "HELD" -> "در امانت"
    "RELEASED" -> "آزادشده"
    "REFUNDED" -> "بازپرداخت‌شده"
    else -> status
}
