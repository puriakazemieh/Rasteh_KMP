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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kazemieh.common.AppResult
import com.kazemieh.common.util.toFaDigits
import com.kazemieh.common.util.toFaPrice
import com.kazemieh.designsystem.FontSize
import com.kazemieh.domain.features.FlashSale
import com.kazemieh.domain.features.GroupBuy
import org.koin.compose.viewmodel.koinViewModel

/** پیشنهادها و جوایز: امتیازِ وفاداری + فلش + خریدِ گروهی. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealsScreen(
    navigateBack: () -> Unit,
    viewModel: DealsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(state.message) {
        state.message?.let { snackbar.showSnackbar(it); viewModel.consumeMessage() }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("پیشنهادها و جوایز", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            state.loyalty?.let { loyalty ->
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(MaterialTheme.colorScheme.primaryContainer).padding(18.dp),
                    ) {
                        Text("باشگاهِ وفاداری", fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(Modifier.height(6.dp))
                        Text("${loyalty.points.toFaDigits()} امتیاز · سطحِ ${tierLabel(loyalty.tier)}", fontSize = FontSize.MEDIUM, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }

            item { SectionTitle("تخفیف‌های فلش ⚡") }
            when (val f = state.flash) {
                is AppResult.Loading -> item { Loading() }
                is AppResult.Error -> item { ErrorText("خطا در دریافتِ فلش") }
                is AppResult.Success -> {
                    if (f.data.isEmpty()) item { EmptyText("فلشِ فعالی نیست") }
                    else items(f.data, key = { "f-${it.id}" }) { FlashCard(it) }
                }
            }

            item { SectionTitle("خریدِ گروهی 👥") }
            when (val g = state.groupBuys) {
                is AppResult.Loading -> item { Loading() }
                is AppResult.Error -> item { ErrorText("خطا در دریافتِ خریدِ گروهی") }
                is AppResult.Success -> {
                    if (g.data.isEmpty()) item { EmptyText("خریدِ گروهیِ فعالی نیست") }
                    else items(g.data, key = { "g-${it.id}" }) { gb ->
                        GroupBuyCard(gb, joining = state.joiningId == gb.id, onJoin = { viewModel.join(gb.id) })
                    }
                }
            }
        }
    }
}

private fun tierLabel(tier: String) = when (tier) {
    "GOLD" -> "طلایی"; "SILVER" -> "نقره‌ای"; else -> "برنزی"
}

@Composable
private fun SectionTitle(text: String) =
    Text(text, fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

@Composable
private fun FlashCard(flash: FlashSale) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface).padding(14.dp),
    ) {
        Text(flash.productName ?: "کالا", fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            flash.salePrice?.let {
                Text("${it.toLong().toFaPrice()} تومان", fontSize = FontSize.SMALL, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Text("٪${flash.discountPercent.toFaDigits()} تخفیف", fontSize = FontSize.EXTRA_SMALL, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun GroupBuyCard(gb: GroupBuy, joining: Boolean, onJoin: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(gb.productName ?: "کالا", fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(4.dp))
            Text("${gb.unitPrice.toLong().toFaPrice()} تومان · ${gb.currentCount.toFaDigits()}/${gb.targetCount.toFaDigits()} نفر", fontSize = FontSize.EXTRA_SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (gb.joined) {
            Text("عضو شدید", fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.primary)
        } else {
            Button(onClick = onJoin, enabled = !joining, shape = RoundedCornerShape(10.dp)) {
                if (joining) CircularProgressIndicator(modifier = Modifier.height(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                else Text("پیوستن")
            }
        }
    }
}

@Composable
private fun Loading() = Box(Modifier.fillMaxWidth().height(60.dp), Alignment.Center) { CircularProgressIndicator() }
@Composable
private fun ErrorText(t: String) = Text(t, color = MaterialTheme.colorScheme.error, fontSize = FontSize.SMALL)
@Composable
private fun EmptyText(t: String) = Text(t, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = FontSize.SMALL)
