package com.kazemieh.bazaar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kazemieh.designsystem.FontSize

/**
 * فهرستِ فروشگاه‌های یک راسته در یک محل. سرور هنوز endpointِ «فهرستِ عمومیِ فروشگاه‌ها» را
 * ندارد (فازِ ۲ کاتالوگِ بازارچه)؛ فعلاً سربرگِ انتخاب + وضعیتِ «به‌زودی» نمایش داده می‌شود.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RastehSearchScreen(
    rastehLabel: String,
    locationName: String,
    navigateBack: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = rastehLabel,
                            fontSize = FontSize.EXTRA_REGULAR,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = locationName,
                            fontSize = FontSize.SMALL,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت",
                        )
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(text = "🏪", fontSize = FontSize.EXTRA_LARGE)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "فهرستِ فروشگاه‌ها به‌زودی",
                    fontSize = FontSize.MEDIUM,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "کاتالوگِ بازارچه (فهرست/جستجوی فروشگاه‌ها) در فازِ بعدیِ سرور فعال می‌شود.",
                    fontSize = FontSize.REGULAR,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
