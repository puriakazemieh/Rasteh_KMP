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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kazemieh.bazaar.component.LocationPickerSheet
import com.kazemieh.bazaar.component.RastehCard
import com.kazemieh.common.AppResult
import com.kazemieh.designsystem.FontSize
import com.kazemieh.designsystem.adaptiveGridColumns
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

/**
 * خانهٔ v2 (search-first): بدونِ هدرِ سنگین؛ نوارِ جستجو در بالا و گریدِ راسته‌ها.
 * انتخابِ راسته → باتم‌شیتِ محل؛ انتخابِ محل → فهرستِ فروشگاه‌ها.
 */
@Composable
fun RastehHomeScreen(
    navigateToRastehSearch: (rastehId: Long, rastehLabel: String, locationId: Long, locationName: String) -> Unit,
    viewModel: RastehHomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is RastehHomeEffect.NavigateToRastehSearch ->
                    navigateToRastehSearch(
                        effect.rastehId,
                        effect.rastehLabel,
                        effect.locationId,
                        effect.locationName,
                    )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        SearchField(
            query = state.query,
            onQueryChange = { viewModel.handleIntent(RastehHomeIntent.OnQueryChange(it)) },
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "راسته‌ها",
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            fontSize = FontSize.EXTRA_REGULAR,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(8.dp))

        when (val rastehs = state.rastehs) {
            is AppResult.Loading -> CenterBox { CircularProgressIndicator() }

            is AppResult.Error -> CenterBox {
                Text(
                    text = "خطا در دریافتِ راسته‌ها",
                    fontSize = FontSize.REGULAR,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            is AppResult.Success -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(adaptiveGridColumns(compact = 3, medium = 4, expanded = 6)),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(rastehs.data, key = { it.id }) { rasteh ->
                        RastehCard(
                            rasteh = rasteh,
                            onClick = { viewModel.handleIntent(RastehHomeIntent.OnRastehClick(rasteh)) },
                        )
                    }
                }
            }
        }
    }

    val sheetRasteh = state.sheetRasteh
    if (sheetRasteh != null) {
        LocationPickerSheet(
            rastehLabel = sheetRasteh.label,
            locations = state.locations,
            onDismiss = { viewModel.handleIntent(RastehHomeIntent.DismissSheet) },
            onLocationClick = { viewModel.handleIntent(RastehHomeIntent.OnLocationClick(it)) },
        )
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp)),
        placeholder = { Text(text = "جستجوی راسته یا فروشگاه…", fontSize = FontSize.REGULAR) },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
        ),
    )
}

@Composable
private fun CenterBox(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center,
        content = { content() },
    )
}
