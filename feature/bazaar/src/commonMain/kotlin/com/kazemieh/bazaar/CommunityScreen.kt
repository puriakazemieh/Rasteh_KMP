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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kazemieh.common.AppResult
import com.kazemieh.common.util.toFaDigits
import com.kazemieh.designsystem.FontSize
import org.koin.compose.viewmodel.koinViewModel

/** انجمنِ بازارچه (community). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    navigateBack: () -> Unit,
    viewModel: CommunityViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("انجمنِ بازارچه", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت") }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(modifier = Modifier.weight(1f)) {
                when (val p = state.posts) {
                    is AppResult.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
                    is AppResult.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) { Text("خطا در دریافتِ پست‌ها", color = MaterialTheme.colorScheme.error, fontSize = FontSize.REGULAR) }
                    is AppResult.Success -> {
                        if (p.data.isEmpty()) Box(Modifier.fillMaxSize(), Alignment.Center) { Text("اولین پست را بنویسید", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = FontSize.REGULAR) }
                        else LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            items(p.data, key = { it.id }) { post ->
                                Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.surface).padding(14.dp)) {
                                    Text(post.authorName ?: "کاربر", fontSize = FontSize.SMALL, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    Spacer(Modifier.size(6.dp))
                                    Text(post.body, fontSize = FontSize.REGULAR, color = MaterialTheme.colorScheme.onSurface)
                                    Spacer(Modifier.size(6.dp))
                                    Text("${post.commentCount.toFaDigits()} نظر", fontSize = FontSize.EXTRA_SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = state.input,
                    onValueChange = viewModel::onInput,
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(24.dp)),
                    placeholder = { Text("پستِ جدید…", fontSize = FontSize.REGULAR) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                )
                Spacer(Modifier.size(8.dp))
                IconButton(onClick = viewModel::post, enabled = !state.posting && state.input.isNotBlank()) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "ارسال", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
