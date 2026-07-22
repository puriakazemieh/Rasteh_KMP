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

/** پارکینگِ من (parking): ثبتِ ورود به جای پارک + پرداختِ هزینه. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingScreen(
    navigateBack: () -> Unit,
    viewModel: ParkingViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var spot by remember { mutableStateOf("") }
    LaunchedEffect(state.message) { state.message?.let { snackbar.showSnackbar(it); viewModel.consumeMessage() } }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("پارکینگِ من", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold) },
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
            item { Text("ثبتِ ورود به پارکینگ", fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) }
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(value = spot, onValueChange = { spot = it }, modifier = Modifier.weight(1f), placeholder = { Text("جای پارک (مثلِ B-12)") }, singleLine = true)
                    Button(onClick = { viewModel.checkin(spot); spot = "" }, enabled = !state.busy && spot.isNotBlank(), shape = RoundedCornerShape(10.dp)) { Text("ورود") }
                }
            }
            item {
                Spacer(Modifier.height(4.dp))
                Text("نشست‌های من", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
            when (val p = state.items) {
                is AppResult.Loading -> item { Box(Modifier.fillMaxWidth().height(60.dp), Alignment.Center) { CircularProgressIndicator() } }
                is AppResult.Error -> item { Text("خطا در دریافت", color = MaterialTheme.colorScheme.error, fontSize = FontSize.SMALL) }
                is AppResult.Success -> {
                    if (p.data.isEmpty()) item { Text("نشستِ پارکینگی ندارید", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = FontSize.SMALL) }
                    else items(p.data, key = { it.id }) { ps ->
                        Column(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant).padding(16.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🅿️ جای ${ps.spot.toFaDigits()}", fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                                Text(if (ps.paid) "پرداخت‌شده" else "باز", fontSize = FontSize.SMALL, fontWeight = FontWeight.Bold, color = if (ps.paid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text("ورود: ${ps.enteredAt.take(16).toFaDigits()}", fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (ps.paid) {
                                Text("هزینه: ${ps.fee.toLong().toFaPrice()} تومان", fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            } else {
                                Spacer(Modifier.height(8.dp))
                                OutlinedButton(onClick = { viewModel.pay(ps.id) }, enabled = !state.busy, shape = RoundedCornerShape(10.dp)) { Text("پرداخت و خروج") }
                            }
                        }
                    }
                }
            }
        }
    }
}
