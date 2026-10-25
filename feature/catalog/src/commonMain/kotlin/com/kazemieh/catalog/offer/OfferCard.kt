package com.kazemieh.catalog.offer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kazemieh.domain.canonical.SaleMode
import com.kazemieh.domain.canonical.ShopOffer

/** A shopper-facing summary that never presents stale or unavailable stock as purchasable. */
@Composable
fun OfferCard(offer: ShopOffer, onAction: () -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Text(offer.title, style = MaterialTheme.typography.titleMedium)
        offer.price?.let { Text("${it.minorUnits} ${it.currency}") }
        if (offer.stale) {
            Text("اطلاعات این عرضه نیازمند تأیید فروشگاه است", color = MaterialTheme.colorScheme.error)
            return
        }
        when (offer.saleMode) {
            SaleMode.SHOWCASE -> AssistChip(onClick = onAction, label = { Text("مشاهده") })
            SaleMode.RESERVABLE -> AssistChip(onClick = onAction, label = { Text("درخواست رزرو") })
            SaleMode.BUYABLE -> if (offer.availableQuantity > 0) {
                AssistChip(onClick = onAction, label = { Text("افزودن به سبد") })
            } else Text("ناموجود", color = MaterialTheme.colorScheme.error)
        }
    }
}
