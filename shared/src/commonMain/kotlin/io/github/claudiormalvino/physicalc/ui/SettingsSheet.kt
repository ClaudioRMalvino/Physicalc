package io.github.claudiormalvino.physicalc.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

/**
 * Settings + Help + About as a frosted-glass bottom panel, matching the bar
 * and help cards. Drawn in the app window (not a platform sheet) because Haze
 * can only blur content in the same window; the caller provides the shared
 * [HazeState] and hosts this inside an AnimatedVisibility, whose scope drives
 * the slide-up enter/exit. [onHelp] replays the guide for the current screen.
 */
@OptIn(ExperimentalHazeMaterialsApi::class, ExperimentalComposeUiApi::class)
@Composable
fun AnimatedVisibilityScope.SettingsSheet(
    hazeState: HazeState,
    onDismiss: () -> Unit,
    onHelp: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val uriHandler = LocalUriHandler.current
    val shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

    BackHandler(enabled = true, onBack = onDismiss)

    // Swipe-down-to-dismiss: the panel follows the finger (downward only) and
    // either springs back or dismisses once dragged past a quarter of its height.
    val dragOffset = remember { Animatable(0f) }
    val dragScope = rememberCoroutineScope()

    // Scrim: tap anywhere above the panel to dismiss.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.scrim.copy(alpha = 0.45f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss,
            ),
        contentAlignment = Alignment.BottomCenter,
    ) {
        // The old ModalBottomSheet's Surface used to provide the content color;
        // the raw glass panel must supply it itself or text falls back to black.
        CompositionLocalProvider(LocalContentColor provides colors.onSurface) {
        Column(
            modifier = Modifier
                .animateEnterExit(
                    enter = slideInVertically { it },
                    exit = slideOutVertically { it },
                )
                .offset { IntOffset(0, dragOffset.value.roundToInt()) }
                .widthIn(max = 640.dp)
                .fillMaxWidth()
                .clip(shape)
                .hazeEffect(state = hazeState, style = HazeMaterials.thin(colors.surface))
                .border(width = 1.dp, color = colors.onSurface.copy(alpha = 0.10f), shape = shape)
                // Swallow taps on the panel so they don't reach the scrim.
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                )
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { change, amount ->
                            change.consume()
                            dragScope.launch {
                                dragOffset.snapTo((dragOffset.value + amount).coerceAtLeast(0f))
                            }
                        },
                        onDragEnd = {
                            if (dragOffset.value > size.height / 4f) {
                                onDismiss()
                            } else {
                                dragScope.launch { dragOffset.animateTo(0f) }
                            }
                        },
                        onDragCancel = {
                            dragScope.launch { dragOffset.animateTo(0f) }
                        },
                    )
                }
                .padding(horizontal = 24.dp)
                .navigationBarsPadding(),
        ) {
            // Drag-handle affordance, matching the Material sheet this replaces.
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 12.dp, bottom = 16.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .background(colors.onSurfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(2.dp)),
            )

            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(Modifier.height(8.dp))

            // --- Theme toggle -------------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Dark theme", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = "Use the dark color palette",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = AppSettings.darkTheme,
                    onCheckedChange = { AppSettings.updateDarkTheme(it) },
                )
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = colors.outlineVariant)
            Spacer(Modifier.height(8.dp))

            // --- Help -----------------------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onHelp)
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Help", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = "Show the guide for this screen again",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant,
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = colors.outlineVariant)
            Spacer(Modifier.height(16.dp))

            // --- About --------------------------------------------------------
            Text(
                text = "ABOUT",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurfaceVariant,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = AppInfo.NAME,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Version ${AppInfo.VERSION}  •  ${AppInfo.LICENSE}",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
            )

            Spacer(Modifier.height(12.dp))

            // GitHub link
            Surface(
                onClick = { uriHandler.openUri(AppInfo.REPO_URL) },
                shape = RoundedCornerShape(12.dp),
                color = colors.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "View source on GitHub",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.primary,
                    )
                    Text(
                        text = AppInfo.REPO_URL.removePrefix("https://"),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
        }
    }
}
