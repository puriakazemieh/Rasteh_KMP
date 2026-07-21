package com.kazemieh.bazaar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import com.kazemieh.designsystem.FontSize
import org.koin.compose.viewmodel.koinViewModel

/** دفترچهٔ ضمانتِ دیجیتال (warranty): ثبت + فهرست. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarrantyScreen(
    navigateBack: () -> Unit,
    viewModel: WarrantyViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var title by remember { mutableStateOf("") }
    var serial by remember { mutableStateOf("") }
    LaunchedEffect(state.message) { state.message?.let { snackbar.showSnackbar(it); viewModel.consumeMessage() } }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("ضمانت‌نامه‌ها", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold) },
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
            item { Text("ثبتِ ضمانت‌نامه", fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) }
            item {
                TextField(value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("نامِ کالا") }, singleLine = true)
            }
            item {
                TextField(value = serial, onValueChange = { serial = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("شمارهٔ سریال (اختیاری)") }, singleLine = true)
            }
            item {
                Button(
                    onClick = { viewModel.add(title, serial); title = ""; serial = "" },
                    enabled = !state.busy && title.isNotBlank(),
                    shape = RoundedCornerShape(10.dp),
                ) { Text("ثبت") }
            }
            item {
                Spacer(Modifier.height(4.dp))
                Text("ضمانت‌نامه‌های من", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
            when (val w = state.items) {
                is AppResult.Loading -> item { Box(Modifier.fillMaxWidth().height(60.dp), Alignment.Center) { CircularProgressIndicator() } }
                is AppResult.Error -> item { Text("خطا در دریافت", color = MaterialTheme.colorScheme.error, fontSize = FontSize.SMALL) }
                is AppResult.Success -> {
                    if (w.data.isEmpty()) item { Text("ضمانت‌نامه‌ای ثبت نشده است", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = FontSize.SMALL) }
                    else items(w.data, key = { it.id }) { wr ->
                        Column(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant).padding(16.dp),
                        ) {
                            Text("🛡️ ${wr.title}", fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            wr.serial?.takeIf { it.isNotBlank() }?.let {
                                Spacer(Modifier.height(4.dp))
                                Text("سریال: ${it.toFaDigits()}", fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            wr.validUntil?.takeIf { it.isNotBlank() }?.let {
                                Spacer(Modifier.height(2.dp))
                                Text("اعتبار تا: ${it.take(10).toFaDigits()}", fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
