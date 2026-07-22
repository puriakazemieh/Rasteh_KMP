package com.kazemieh.bazaar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kazemieh.designsystem.FontSize

/**
 * دستیارِ خرید (concierge) · MVP: پرسش‌های پرتکرار با پاسخِ آماده + میان‌بر به جست‌وجو.
 * فازِ بعدی: اتصال به LLM/جست‌وجوی هوشمند.
 */
private data class ConciergeQa(val q: String, val a: String)

private val CONCIERGE_QA = listOf(
    ConciergeQa("چطور یک فروشگاه پیدا کنم؟", "از خانه یک «راسته» (مثلِ موبایل یا طلا) را انتخاب کنید، سپس «محل» را برگزینید تا فروشگاه‌های آن را ببینید."),
    ConciergeQa("چطور با فروشنده تماس بگیرم؟", "در صفحهٔ هر فروشگاه دکمه‌های «پیام به فروشگاه» و «تماس» وجود دارد."),
    ConciergeQa("«خرید آنلاین» با «بازدید حضوری» چه فرقی دارد؟", "فروشگاه‌های «خرید آنلاین» امکانِ افزودن به سبد و سفارش دارند؛ «بازدید حضوری» فقط برای تماس/مراجعهٔ حضوری است."),
    ConciergeQa("چطور قیمتِ پیشنهادی بدهم؟", "اگر فروشگاه پذیرای پیشنهاد باشد، در صفحهٔ محصول دکمهٔ «پیشنهاد قیمت» را می‌بینید."),
    ConciergeQa("چطور کالایی را نشان کنم؟", "روی آیکونِ نشان (bookmark) در کارتِ فروشگاه/محصول بزنید؛ در «نشان‌شده‌ها» جمع می‌شوند."),
    ConciergeQa("مسیرِ فروشگاه در پاساژ را چطور پیدا کنم؟", "در صفحهٔ محل، آیکونِ مکان (بالا) «مسیریابِ محل» را باز می‌کند که فروشگاه‌ها را طبقه‌به‌طبقه نشان می‌دهد."),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConciergeScreen(
    navigateBack: () -> Unit,
    navigateToSearch: () -> Unit,
) {
    var openIndex by remember { mutableStateOf(-1) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("دستیارِ خرید", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold) },
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
            item {
                Text("سلام! چطور می‌توانم کمک کنم؟ یکی از پرسش‌ها را انتخاب کنید:", fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            items(CONCIERGE_QA.size) { i ->
                val qa = CONCIERGE_QA[i]
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { openIndex = if (openIndex == i) -1 else i }
                        .padding(14.dp),
                ) {
                    Text("💬 ${qa.q}", fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    if (openIndex == i) {
                        Spacer(Modifier.height(6.dp))
                        Text(qa.a, fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            item {
                Spacer(Modifier.height(8.dp))
                Button(onClick = navigateToSearch, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Text("جست‌وجوی محصول یا فروشگاه")
                }
            }
        }
    }
}
