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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kazemieh.common.util.toFaDigits
import com.kazemieh.designsystem.FontSize
import org.koin.compose.viewmodel.koinViewModel

/** «بازارچه پلاس» (vipsub): وضعیتِ اشتراکِ ویژه + فعال‌سازی. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VipSubScreen(
    navigateBack: () -> Unit,
    viewModel: VipSubViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(state.message) { state.message?.let { snackbar.showSnackbar(it); viewModel.consumeMessage() } }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("بازارچه پلاس", fontSize = FontSize.EXTRA_REGULAR, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت") }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.loading) {
                Box(Modifier.fillMaxWidth().height(120.dp), Alignment.Center) { CircularProgressIndicator() }
                return@Column
            }

            val sub = state.subscription
            val active = sub?.active == true

            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer).padding(20.dp),
            ) {
                Text(if (active) "اشتراکِ شما فعال است " else "بازارچه پلاس", fontSize = FontSize.LARGE, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(Modifier.height(8.dp))
                Text(
                    if (active) "به همهٔ مزایای ویژه دسترسی دارید." else "با فعال‌سازیِ بازارچه پلاس از ارسالِ رایگان، تخفیف‌های ویژه و پشتیبانیِ سریع بهره‌مند شوید.",
                    fontSize = FontSize.SMALL,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                sub?.expiresAt?.takeIf { active }?.let {
                    Spacer(Modifier.height(8.dp))
                    Text("اعتبار تا: ${it.toFaDigits()}", fontSize = FontSize.SMALL, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }

            Button(
                onClick = { viewModel.subscribe() },
                enabled = !state.busy,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) { Text(if (active) "تمدیدِ اشتراک" else "فعال‌سازیِ بازارچه پلاس") }
        }
    }
}
