package com.kazemieh.main.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kazemieh.designsystem.AppTheme
import com.kazemieh.designsystem.Radius
import org.jetbrains.compose.resources.painterResource

/**
 * نوارِ کناریِ ناوبری برای نمایشگرهای بزرگ (تبلت/دسکتاپ/وب) · جایگزینِ نوارِ پایینِ موبایل.
 * روی `expanded` برچسب کنارِ آیکن دیده می‌شود؛ روی `medium` فقط آیکن (rail باریک).
 */
@Composable
fun SideNavRail(
    cartItemCount: Int,
    selected: BottomBarDestination,
    onSelect: (BottomBarDestination) -> Unit,
    expandedLabels: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(if (expandedLabels) 200.dp else 76.dp)
            .background(colors.surface)
            .border(BorderStroke(1.dp, colors.line), RoundedCornerShape(0.dp))
            .padding(vertical = 16.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BottomBarDestination.entries.forEach { destination ->
            val isSelected = selected == destination
            val animatedTint by animateColorAsState(
                targetValue = if (isSelected) colors.primary else colors.onSurfaceVariant
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(Radius.button))
                    .background(if (isSelected) colors.accentSoft else Color.Transparent)
                    .clickable { onSelect(destination) }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(painterResource(destination.icon), destination.faLabel, tint = animatedTint, modifier = Modifier.size(24.dp))
                }
                if (expandedLabels) {
                    Text(
                        text = destination.faLabel,
                        color = if (isSelected) colors.primary else colors.onSurface,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    )
                    Spacer(Modifier.width(4.dp))
                }
            }
        }
    }
}
