package com.kazemieh.main.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kazemieh.designsystem.AppTheme
import org.jetbrains.compose.resources.painterResource

/**
 * نوار پایینِ تمام‌عرضِ مسطح — مطابقِ دیزاینِ بازارچه:
 * چهار آیتمِ هم‌عرض، هر آیتم آیکن روی برچسبِ فارسی (همیشه نمایان)،
 * آیتمِ فعال با رنگِ primary. برچسب‌ها هاردکدِ فارسی‌اند تا روی وب مستقل از locale درست باشند.
 */
@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    cartItemCount: Int,
    selected: BottomBarDestination,
    onSelect: (BottomBarDestination) -> Unit,
) {
    val colors = AppTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(top = 8.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        BottomBarDestination.entries.forEach { destination ->
            val isSelected = selected == destination
            val animatedTint by animateColorAsState(
                targetValue = if (isSelected) colors.primary else colors.onSurfaceVariant
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelect(destination) }
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (destination == BottomBarDestination.Cart && cartItemCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = colors.sale, contentColor = Color.White) {
                                    Text(text = cartItemCount.toString(), fontSize = 9.sp)
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(destination.icon),
                                contentDescription = destination.faLabel,
                                tint = animatedTint,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else {
                        Icon(
                            painter = painterResource(destination.icon),
                            contentDescription = destination.faLabel,
                            tint = animatedTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = destination.faLabel,
                    color = animatedTint,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
