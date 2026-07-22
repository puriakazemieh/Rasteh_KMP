package com.kazemieh.bazaar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kazemieh.bazaar.component.SelectField
import com.kazemieh.designsystem.FontSize
import com.kazemieh.designsystem.component.CustomTextField
import com.kazemieh.designsystem.component.PrimaryButton
import org.koin.compose.viewmodel.koinViewModel

/**
 * فرمِ «درخواستِ فروشندگی» · نام، راسته، محل، دسته، طبقه، نوع (خرید آنلاین/فقط حضوری)،
 * تلفن، دربارهٔ فروشگاه، چت و پیشنهادِ قیمت. ثبت → PENDING تا تأییدِ ادمین.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BecomeVendorScreen(
    navigateBack: () -> Unit,
    viewModel: BecomeVendorViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "درخواستِ فروشندگی", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
            )
        },
    ) { padding ->
        if (state.submitted) {
            SubmittedContent(modifier = Modifier.fillMaxSize().padding(padding), onDone = navigateBack)
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Text(
                text = "اطلاعاتِ فروشگاهِ خود را وارد کنید. پس از تأییدِ ادمین، حسابِ شما به فروشنده ارتقا می‌یابد.",
                fontSize = FontSize.SMALL,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))

            CustomTextField(
                value = state.name,
                onValueChange = { viewModel.handleIntent(BecomeVendorIntent.OnName(it)) },
                placeholder = "نامِ فروشگاه *",
            )
            Spacer(Modifier.height(12.dp))

            SelectField(
                label = "راسته (صنف) *",
                selected = state.selectedRasteh,
                options = state.rastehs,
                optionLabel = { it.label },
                onSelect = { viewModel.handleIntent(BecomeVendorIntent.OnRasteh(it)) },
            )
            Spacer(Modifier.height(12.dp))

            SelectField(
                label = if (state.isLocationsLoading) "در حالِ دریافتِ محل‌ها..." else "محل (پاساژ/بازار) *",
                selected = state.selectedLocation,
                options = state.locations,
                optionLabel = { it.name },
                enabled = state.selectedRasteh != null && !state.isLocationsLoading,
                onSelect = { viewModel.handleIntent(BecomeVendorIntent.OnLocation(it)) },
            )
            Spacer(Modifier.height(12.dp))

            CustomTextField(
                value = state.category,
                onValueChange = { viewModel.handleIntent(BecomeVendorIntent.OnCategory(it)) },
                placeholder = "دسته‌بندی (اختیاری)",
            )
            Spacer(Modifier.height(12.dp))

            CustomTextField(
                value = state.floor,
                onValueChange = { viewModel.handleIntent(BecomeVendorIntent.OnFloor(it)) },
                placeholder = "طبقه/پلاک (اختیاری)",
            )
            Spacer(Modifier.height(16.dp))

            Text(text = "نوعِ فروشگاه", fontSize = FontSize.REGULAR, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    selected = state.type == "BUYABLE",
                    onClick = { viewModel.handleIntent(BecomeVendorIntent.OnType("BUYABLE")) },
                    label = { Text("خرید آنلاین") },
                )
                FilterChip(
                    selected = state.type == "VISIT_ONLY",
                    onClick = { viewModel.handleIntent(BecomeVendorIntent.OnType("VISIT_ONLY")) },
                    label = { Text("فقط حضوری") },
                )
            }
            Spacer(Modifier.height(16.dp))

            CustomTextField(
                value = state.phone,
                onValueChange = { viewModel.handleIntent(BecomeVendorIntent.OnPhone(it)) },
                placeholder = "تلفنِ فروشگاه (اختیاری)",
            )
            Spacer(Modifier.height(12.dp))

            CustomTextField(
                value = state.about,
                onValueChange = { viewModel.handleIntent(BecomeVendorIntent.OnAbout(it)) },
                placeholder = "دربارهٔ فروشگاه (اختیاری)",
                expanded = true,
            )
            Spacer(Modifier.height(16.dp))

            ToggleRow(
                title = "چتِ درون‌برنامه",
                checked = state.hasChat,
                onCheckedChange = { viewModel.handleIntent(BecomeVendorIntent.OnHasChat(it)) },
            )
            Spacer(Modifier.height(8.dp))
            ToggleRow(
                title = "پذیرشِ پیشنهادِ قیمت",
                checked = state.acceptsOffers,
                onCheckedChange = { viewModel.handleIntent(BecomeVendorIntent.OnAcceptsOffers(it)) },
            )

            if (state.error != null) {
                Spacer(Modifier.height(12.dp))
                Text(text = state.error!!, fontSize = FontSize.SMALL, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(20.dp))
            PrimaryButton(
                text = if (state.isSubmitting) "در حالِ ثبت..." else "ثبتِ درخواست",
                enabled = state.canSubmit,
                onClick = { viewModel.handleIntent(BecomeVendorIntent.Submit) },
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ToggleRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, modifier = Modifier.weight(1f), fontSize = FontSize.REGULAR, color = MaterialTheme.colorScheme.onSurface)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SubmittedContent(modifier: Modifier, onDone: () -> Unit) {
    Box(modifier = modifier.padding(24.dp), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = "✅", fontSize = FontSize.EXTRA_LARGE)
            Spacer(Modifier.height(16.dp))
            Text(
                text = "درخواستِ شما ثبت شد",
                fontSize = FontSize.MEDIUM,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "پس از تأییدِ ادمین، فروشگاهِ شما فعال می‌شود و به شما اطلاع داده خواهد شد.",
                fontSize = FontSize.REGULAR,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            PrimaryButton(text = "بازگشت", onClick = onDone)
        }
    }
}
