package com.kazemieh.bazaar.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.kazemieh.common.util.toFaDigits
import com.kazemieh.common.util.toFaPrice
import com.kazemieh.designsystem.FontSize
import com.kazemieh.domain.marketplace.Product

/** کارتِ کالا در صفحهٔ فروشگاه. */
@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    buying: Boolean = false,
    onBuy: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = product.emoji ?: "📦", fontSize = FontSize.MEDIUM)
        }
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.name,
                fontSize = FontSize.REGULAR,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(6.dp))
            if (product.price > 0) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "${product.price.toLong().toFaPrice()} تومان",
                        fontSize = FontSize.SMALL,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    product.oldPrice?.let {
                        Text(
                            text = it.toLong().toFaPrice(),
                            fontSize = FontSize.EXTRA_SMALL,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough,
                        )
                    }
                }
            } else {
                Text(
                    text = "نمایشی — تماس بگیرید",
                    fontSize = FontSize.SMALL,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (product.purchasable && onBuy != null) {
            Spacer(Modifier.size(8.dp))
            Button(
                onClick = onBuy,
                enabled = !buying,
                shape = RoundedCornerShape(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            ) {
                if (buying) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("خرید", fontSize = FontSize.SMALL)
                }
            }
        } else if (product.purchasable && product.stock in 1..3) {
            Text(
                text = "${product.stock.toFaDigits()} عدد",
                fontSize = FontSize.EXTRA_SMALL,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}
