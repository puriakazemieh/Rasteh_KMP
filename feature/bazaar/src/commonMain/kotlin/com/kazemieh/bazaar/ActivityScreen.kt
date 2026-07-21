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
import com.kazemieh.designsystem.FontSize
import org.koin.compose.viewmodel.koinViewModel

/** فیدِ فعالیت (activity) — نشان‌کردن‌ها و سفارش‌های اخیرِ کاربر. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    navigateBack: () -> Unit,
    viewModel: ActivityViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("فعالیت‌های من", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت") }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            when (val a = state.items) {
                is AppResult.Loading -> item { Box(Modifier.fillMaxWidth().height(120.dp), Alignment.Center) { CircularProgressIndicator() } }
                is AppResult.Error -> item { Text("خطا در دریافتِ فعالیت‌ها", color = MaterialTheme.colorScheme.error, fontSize = FontSize.SMALL) }
                is AppResult.Success -> {
                    if (a.data.isEmpty()) {
                        item { Text("هنوز فعالیتی ثبت نشده است", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = FontSize.SMALL) }
                    } else items(a.data.size) { i ->
                        val act = a.data[i]
                        Row(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant).padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Box(
                                modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.surface),
                                contentAlignment = Alignment.Center,
                            ) { Text(if (act.type == "ORDER") "🛍️" else "🔖", fontSize = FontSize.REGULAR) }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(act.title, fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                act.subtitle?.takeIf { it.isNotBlank() }?.let {
                                    Text("${it.toFaDigits()} تومان", fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                act.createdAt?.takeIf { it.isNotBlank() }?.let {
                                    Text(it.take(10).toFaDigits(), fontSize = FontSize.EXTRA_SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
