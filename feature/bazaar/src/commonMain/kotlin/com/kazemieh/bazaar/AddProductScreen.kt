package com.kazemieh.bazaar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kazemieh.designsystem.AppTheme
import com.kazemieh.designsystem.FontSize
import org.koin.compose.viewmodel.koinViewModel

/**
 * صفحهٔ «افزودن محصول جدید» (addProduct) — مطابقِ بستهٔ طراحیِ Unified App:
 * ردیفِ عکس‌ها، نام، قیمت/دسته، وضعیت (نو/کارکرده)، موجودی/تخفیف، توضیحات، ثبت.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    navigateBack: () -> Unit,
    viewModel: AddProductViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val colors = AppTheme.colors

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("افزودنِ محصولِ جدید", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = navigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت") } },
            )
        },
        bottomBar = {
            if (!state.added) {
                Box(Modifier.fillMaxWidth().background(colors.surface).padding(horizontal = 18.dp, vertical = 12.dp)) {
                    val enabled = viewModel.canSubmit
                    Box(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                            .background(if (enabled) colors.primary else colors.onSurfaceVariant.copy(alpha = 0.4f))
                            .clickable(enabled = enabled) { viewModel.submit() }.padding(vertical = 13.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (state.submitting) CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                        else Text("ثبتِ محصول", fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        },
    ) { padding ->
        if (state.added) {
            SuccessView(modifier = Modifier.fillMaxSize().padding(padding), onList = { viewModel.reset(); navigateBack() })
            return@Scaffold
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // ردیفِ عکس‌ها (نمایشی — آپلود در فازِ بعد)
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Column(
                    modifier = Modifier.weight(1f).height(100.dp).clip(RoundedCornerShape(12.dp))
                        .background(colors.accentSoft)
                        .border(BorderStroke(1.5.dp, colors.primary.copy(alpha = 0.5f)), RoundedCornerShape(12.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = colors.primary, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.height(6.dp))
                    Text("عکسِ اصلی", fontSize = FontSize.EXTRA_SMALL, fontWeight = FontWeight.Bold, color = colors.primary)
                }
                repeat(2) {
                    Box(
                        modifier = Modifier.width(70.dp).height(100.dp).clip(RoundedCornerShape(12.dp))
                            .border(BorderStroke(1.5.dp, colors.line), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center,
                    ) { Icon(Icons.Default.Add, contentDescription = null, tint = colors.onSurfaceVariant, modifier = Modifier.size(20.dp)) }
                }
            }

            FieldLabel("نامِ محصول")
            FormField(state.title, viewModel::onTitle, "مثلاً قابِ محافظِ گوشی")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FieldLabel("قیمت (تومان)")
                    FormField(state.price, viewModel::onPrice, "۴۵۰٬۰۰۰", KeyboardType.Number)
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FieldLabel("دسته‌بندی")
                    FormField(state.category, viewModel::onCategory, "موبایل")
                }
            }

            FieldLabel("وضعیتِ کالا")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ConditionChip("نو", state.condition == "NEW") { viewModel.onCondition("NEW") }
                ConditionChip("کارکرده", state.condition == "USED") { viewModel.onCondition("USED") }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FieldLabel("موجودی (تعداد)")
                    FormField(state.stock, viewModel::onStock, "۱۰", KeyboardType.Number)
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FieldLabel("تخفیف (٪)")
                    FormField(state.discount, viewModel::onDiscount, "۰", KeyboardType.Number)
                }
            }

            FieldLabel("توضیحات")
            FormField(state.description, viewModel::onDescription, "ویژگی‌ها، گارانتی، نحوهٔ تحویل...", singleLine = false, minHeight = 74.dp)

            if (state.shopBuyable) {
                Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(colors.accentSoft).padding(9.dp)) {
                    Text("فروشگاهِ شما «خرید آنلاین» است؛ این محصول قابلِ افزودن به سبد خواهد بود.", fontSize = FontSize.EXTRA_SMALL, color = colors.primary)
                }
            }
            state.error?.let { Text(it, fontSize = FontSize.SMALL, color = colors.sale) }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text, fontSize = FontSize.SMALL, fontWeight = FontWeight.SemiBold, color = AppTheme.colors.onSurface)
}

@Composable
private fun FormField(
    value: String,
    onChange: (String) -> Unit,
    placeholder: String,
    keyboard: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minHeight: androidx.compose.ui.unit.Dp = 0.dp,
) {
    val colors = AppTheme.colors
    TextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth().then(if (minHeight > 0.dp) Modifier.height(minHeight) else Modifier)
            .clip(RoundedCornerShape(10.dp))
            .border(BorderStroke(1.5.dp, colors.line), RoundedCornerShape(10.dp)),
        placeholder = { Text(placeholder, fontSize = FontSize.SMALL, color = colors.onSurfaceVariant) },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = FontSize.REGULAR),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colors.surface,
            unfocusedContainerColor = colors.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
    )
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.ConditionChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val colors = AppTheme.colors
    Box(
        modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
            .background(if (selected) colors.accentSoft else colors.surface)
            .border(BorderStroke(1.5.dp, if (selected) colors.primary else colors.line), RoundedCornerShape(10.dp))
            .clickable(onClick = onClick).padding(vertical = 11.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, fontSize = FontSize.SMALL, fontWeight = FontWeight.Bold, color = if (selected) colors.primary else colors.onSurfaceVariant)
    }
}

@Composable
private fun SuccessView(modifier: Modifier, onList: () -> Unit) {
    val colors = AppTheme.colors
    Column(modifier = modifier.padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Box(Modifier.size(56.dp).clip(RoundedCornerShape(28.dp)).background(colors.ok.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Check, contentDescription = null, tint = colors.ok, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.height(14.dp))
        Text("محصول ثبت شد", fontSize = FontSize.MEDIUM, fontWeight = FontWeight.ExtraBold, color = colors.onSurface)
        Spacer(Modifier.height(8.dp))
        Text("محصولِ شما در وضعیتِ «در انتظارِ تأییدِ ادمین» قرار گرفت.", fontSize = FontSize.SMALL, color = colors.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Spacer(Modifier.height(18.dp))
        Box(
            modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(colors.primary).clickable(onClick = onList).padding(horizontal = 22.dp, vertical = 11.dp),
        ) { Text("مشاهدهٔ لیستِ محصولات", fontSize = FontSize.SMALL, fontWeight = FontWeight.Bold, color = Color.White) }
    }
}
