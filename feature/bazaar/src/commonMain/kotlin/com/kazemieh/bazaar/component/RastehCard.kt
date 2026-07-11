package com.kazemieh.bazaar.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kazemieh.bazaar.util.rastehVisual
import com.kazemieh.designsystem.FontSize
import com.kazemieh.domain.marketplace.Rasteh

/**
 * کارتِ یک راسته در گریدِ خانه — مربعِ رنگی با اموجی + برچسبِ فارسی.
 */
@Composable
fun RastehCard(
    rasteh: Rasteh,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val visual = rastehVisual(rasteh.iconKey)
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(18.dp))
                .background(visual.color.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(visual.color.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = visual.emoji, fontSize = FontSize.EXTRA_MEDIUM)
            }
        }
        Text(
            text = rasteh.label,
            modifier = Modifier.fillMaxWidth(),
            fontSize = FontSize.SMALL,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
