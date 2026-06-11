package io.github.claudiormalvino.physicalc.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import io.github.claudiormalvino.physicalc.RootTab

/*
 * Floating frosted-glass navigation dock. Haze renders a real backdrop blur
 * of the content scrolling beneath it; a hairline border supplies the glass rim.
 */

private fun iconFor(tab: RootTab): ImageVector = when (tab) {
    RootTab.Tools -> Icons.Default.Build
    RootTab.Home -> Icons.Default.Home
    RootTab.Chapters -> Icons.AutoMirrored.Filled.List
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun GlassBottomBar(
    selected: RootTab,
    onSelect: (RootTab) -> Unit,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(31.dp)

    Row(
        modifier = modifier
            .navigationBarsPadding()
            .padding(start = 24.dp, end = 24.dp, bottom = 10.dp)
            // Cap the dock width so it stays a compact pill on desktop/tablets.
            .widthIn(max = 440.dp)
            .fillMaxWidth()
            .height(62.dp)
            .clip(shape)
            .hazeEffect(state = hazeState, style = HazeMaterials.thin(colors.surface))
            .border(width = 1.dp, color = colors.onSurface.copy(alpha = 0.10f), shape = shape),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RootTab.entries.forEach { tab ->
            val isSelected = tab == selected
            val tint by animateColorAsState(
                targetValue = if (isSelected) colors.primary else colors.onSurfaceVariant,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    // No ripple: a clean tint change reads better on glass.
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onSelect(tab) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = iconFor(tab),
                    contentDescription = tab.label,
                    tint = tint,
                    modifier = Modifier.size(22.dp),
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = tab.label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = tint,
                )
            }
        }
    }
}
