package com.kazemieh.bazaar.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kazemieh.bazaar.util.rastehVisual
import com.kazemieh.designsystem.AppTheme
import com.kazemieh.designsystem.FontSize
import com.kazemieh.domain.marketplace.Rasteh

/**
 * کارتِ یک راسته در گریدِ خانه · دقیقاً مطابقِ بستهٔ طراحی:
 * مربعِ ۵۲×۵۲ با پس‌زمینهٔ خنثیِ روشن + بوردرِ ۱px، آیکونِ خطیِ (stroke) ۲۳px
 * با رنگِ اختصاصیِ راسته، و برچسبِ فارسیِ زیرِ آن.
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
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(BorderStroke(1.dp, AppTheme.colors.line), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(visual.icon),
                contentDescription = rasteh.label,
                tint = visual.color,
                modifier = Modifier.size(23.dp),
            )
        }
        Text(
            text = rasteh.label,
            modifier = Modifier.fillMaxWidth(),
            fontSize = FontSize.EXTRA_SMALL,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
