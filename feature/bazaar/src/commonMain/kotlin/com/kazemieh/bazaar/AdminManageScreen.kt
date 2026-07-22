package com.kazemieh.bazaar

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import com.kazemieh.common.AppResult
import com.kazemieh.designsystem.FontSize
import org.koin.compose.viewmodel.koinViewModel

/** پنلِ مدیریتِ ادمین: مدیریتِ راسته/محل + رسیدگی به گزارش‌ها. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManageScreen(
    navigateBack: () -> Unit,
    viewModel: AdminManageViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(state.message) { state.message?.let { snackbar.showSnackbar(it); viewModel.consumeMessage() } }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("مدیریتِ بازارچه", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = navigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت") } },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("راسته و محل", "گزارش‌ها").forEachIndexed { i, title ->
                    val sel = i == state.tab
                    Box(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant).clickable { viewModel.onTab(i) }.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) { Text(title, fontSize = FontSize.SMALL, fontWeight = FontWeight.Bold, color = if (sel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            }
            if (state.tab == 0) ManageTab(state, viewModel) else ReportsTab(state, viewModel)
        }
    }
}

@Composable
private fun ManageTab(state: AdminManageState, viewModel: AdminManageViewModel) {
    var rastehLabel by remember { mutableStateOf("") }
    var locName by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("افزودنِ راسته", fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextField(value = rastehLabel, onValueChange = { rastehLabel = it }, modifier = Modifier.weight(1f), placeholder = { Text("نامِ راسته") }, singleLine = true)
            Button(onClick = { viewModel.createRasteh(rastehLabel); rastehLabel = "" }, enabled = !state.busy) { Text("افزودن") }
        }

        Text("افزودنِ محل", fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        val cityId = state.cities.firstOrNull()?.id
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextField(value = locName, onValueChange = { locName = it }, modifier = Modifier.weight(1f), placeholder = { Text("نامِ محل" + (state.cities.firstOrNull()?.let { " (${it.name})" } ?: "")) }, singleLine = true)
            Button(onClick = { cityId?.let { viewModel.createLocation(it, locName, "PASSAGE"); locName = "" } }, enabled = !state.busy && cityId != null) { Text("افزودن") }
        }

        Spacer(Modifier.height(4.dp))
        Text("راسته‌های موجود", fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        when (val r = state.rastehs) {
            is AppResult.Loading -> Box(Modifier.fillMaxWidth().height(60.dp), Alignment.Center) { CircularProgressIndicator() }
            is AppResult.Error -> Text("خطا", color = MaterialTheme.colorScheme.error, fontSize = FontSize.SMALL)
            is AppResult.Success -> r.data.forEach { rasteh ->
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surface).padding(12.dp)) {
                    Text(rasteh.label, fontSize = FontSize.REGULAR, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
private fun ReportsTab(state: AdminManageState, viewModel: AdminManageViewModel) {
    when (val r = state.reports) {
        is AppResult.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        is AppResult.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) { Text("خطا", color = MaterialTheme.colorScheme.error) }
        is AppResult.Success -> {
            if (r.data.isEmpty()) Box(Modifier.fillMaxSize(), Alignment.Center) { Text("گزارشی نیست", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = FontSize.SMALL) }
            else LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(r.data, key = { it.id }) { report ->
                    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.surface).padding(14.dp)) {
                        Text("${if (report.targetType == "SHOP") "فروشگاه" else "محصول"} #${report.targetId} · وضعیت: ${report.status}", fontSize = FontSize.SMALL, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        report.reason?.let { Spacer(Modifier.height(4.dp)); Text(it, fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        if (report.status == "OPEN") {
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(onClick = { viewModel.resolveReport(report.id, "RESOLVED") }) { Text("رسیدگی‌شد") }
                                OutlinedButton(onClick = { viewModel.resolveReport(report.id, "DISMISSED") }, shape = RoundedCornerShape(10.dp)) { Text("رد") }
                            }
                        }
                    }
                }
            }
        }
    }
}
