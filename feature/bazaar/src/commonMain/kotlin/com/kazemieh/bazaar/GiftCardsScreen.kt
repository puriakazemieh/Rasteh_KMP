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

/** کارت‌های هدیه (giftcard): ساخت، دریافت با کد، فهرست. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiftCardsScreen(
    navigateBack: () -> Unit,
    viewModel: GiftCardsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var code by remember { mutableStateOf("") }
    LaunchedEffect(state.message) { state.message?.let { snackbar.showSnackbar(it); viewModel.consumeMessage() } }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("کارت‌های هدیه", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold) },
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
                Text("ساختِ کارتِ هدیه", fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(200_000L, 500_000L, 1_000_000L).forEach { amount ->
                        OutlinedButton(onClick = { viewModel.issue(amount.toDouble()) }, enabled = !state.busy, shape = RoundedCornerShape(10.dp)) {
                            Text("${amount.toFaPrice()}", fontSize = FontSize.SMALL)
                        }
                    }
                }
            }
            item {
                Text("دریافتِ کارت با کد", fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(value = code, onValueChange = { code = it }, modifier = Modifier.weight(1f), placeholder = { Text("کدِ کارت") }, singleLine = true)
                    Button(onClick = { viewModel.redeem(code); code = "" }, enabled = !state.busy, shape = RoundedCornerShape(10.dp)) { Text("دریافت") }
                }
            }
            item {
                Spacer(Modifier.height(4.dp))
                Text("کارت‌های من", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
            when (val c = state.cards) {
                is AppResult.Loading -> item { Box(Modifier.fillMaxWidth().height(60.dp), Alignment.Center) { CircularProgressIndicator() } }
                is AppResult.Error -> item { Text("خطا در دریافت", color = MaterialTheme.colorScheme.error, fontSize = FontSize.SMALL) }
                is AppResult.Success -> {
                    if (c.data.isEmpty()) item { Text("کارتی ندارید", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = FontSize.SMALL) }
                    else items(c.data, key = { it.id }) { card ->
                        Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.secondaryContainer).padding(16.dp)) {
                            Text("کد: ${card.code.toFaDigits()}", fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            Spacer(Modifier.height(6.dp))
                            Text("موجودی: ${card.balance.toLong().toFaPrice()} تومان", fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                }
            }
        }
    }
}
