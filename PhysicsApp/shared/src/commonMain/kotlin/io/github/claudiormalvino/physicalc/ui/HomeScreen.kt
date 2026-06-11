package io.github.claudiormalvino.physicalc.ui

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.claudiormalvino.physicalc.ui.solar.SolarSystemModel
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/*
 * Landing page: a live model of the solar system.
 *
 * Radial distances are compressed (Neptune is 30 AU out; nobody has a phone
 * that tall), but the ANGLES are real — each planet sits at its true
 * heliocentric ecliptic longitude for the current date, via SolarSystemModel.
 * Ambient animation: twinkling starfield + a gently pulsing sun.
 */

@OptIn(ExperimentalTime::class)
private fun nowMillis(): Long = Clock.System.now().toEpochMilliseconds()

/** One background star: position, size, and twinkle phase/speed. */
private data class Star(val x: Float, val y: Float, val size: Float, val phase: Float, val speed: Float)

/** Deterministic starfield (seeded LCG) — stable across recompositions and platforms. */
private fun generateStars(count: Int): List<Star> {
    var seed = 0x5DEECE66DL
    fun next(): Float {
        seed = seed * 6364136223846793005L + 1442695040888963407L
        return ((seed ushr 33) and 0x7FFFFFFF).toFloat() / 0x7FFFFFFF
    }
    return List(count) {
        Star(
            x = next(),
            y = next(),
            size = 0.6f + next() * 1.6f,
            phase = next(),
            // WHOLE cycles per animation loop (1..3). Integer speeds make the
            // loop seam invisible: sin(2π(k + φ)) == sin(2πφ) exactly, so a
            // star's brightness is identical on both sides of the restart.
            speed = (1 + (next() * 3f).toInt().coerceAtMost(2)).toFloat(),
        )
    }
}

@Composable
fun HomeScreen(onSettingsClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme

    // Planet positions are computed when the screen opens and then refreshed
    // every 20 minutes — even Mercury only moves ~0.06° in that window, far
    // below one pixel at this scale, so more frequent updates are pure waste.
    var epochMillis by remember { mutableLongStateOf(nowMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(20 * 60_000L)
            epochMillis = nowMillis()
        }
    }
    val planetStates = remember(epochMillis) {
        SolarSystemModel.positions(SolarSystemModel.julianDay(epochMillis))
    }

    // Ambient animation clocks.
    val transition = rememberInfiniteTransition()
    val twinkle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 10_000, easing = LinearEasing)),
    )
    val pulse by transition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        // Sine easing has zero slope at both endpoints, so the Reverse turn-around
        // is velocity-continuous — the sun "breathes" without a kink.
        animationSpec = infiniteRepeatable(tween(durationMillis = 2600, easing = EaseInOutSine), repeatMode = RepeatMode.Reverse),
    )

    val stars = remember { generateStars(80) }
    val isDark = AppSettings.darkTheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.55f)
            val maxOrbit = minOf(size.width, size.height * 0.78f) / 2f - 16.dp.toPx()

            // --- Starfield ------------------------------------------------------
            val starColor = if (isDark) Color.White else colors.onSurfaceVariant
            for (s in stars) {
                val a = 0.18f + 0.55f * (0.5f + 0.5f * sin(2f * PI.toFloat() * (twinkle * s.speed + s.phase)))
                drawCircle(
                    color = starColor.copy(alpha = a * if (isDark) 1f else 0.55f),
                    radius = s.size.dp.toPx() * 0.6f,
                    center = Offset(s.x * size.width, s.y * size.height),
                )
            }

            // --- Orbit rings ----------------------------------------------------
            val ringColor = colors.onSurfaceVariant.copy(alpha = if (isDark) 0.20f else 0.30f)
            val orbitRadii = planetStates.indices.map { i ->
                maxOrbit * (0.20f + 0.80f * i / (planetStates.size - 1f))
            }
            for (r in orbitRadii) {
                drawCircle(color = ringColor, radius = r, center = center, style = Stroke(width = 1.dp.toPx()))
            }

            // --- Sun (pulsing glow + core) --------------------------------------
            val sunCore = Color(0xFFFFC94D)
            val glowRadius = maxOrbit * 0.17f * pulse
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(sunCore.copy(alpha = 0.55f), sunCore.copy(alpha = 0f)),
                    center = center,
                    radius = glowRadius,
                ),
                radius = glowRadius,
                center = center,
            )
            drawCircle(color = sunCore, radius = maxOrbit * 0.055f, center = center)

            // --- Planets at their true longitudes -------------------------------
            planetStates.forEachIndexed { i, state ->
                val angle = (state.longitudeDeg * PI / 180.0).toFloat()
                // Ecliptic longitude is counterclockwise from the vernal equinox;
                // Canvas y points down, hence the minus.
                val pos = Offset(
                    center.x + orbitRadii[i] * cos(angle),
                    center.y - orbitRadii[i] * sin(angle),
                )
                val dotR = maxOrbit * state.spec.relativeSize * 0.5f
                drawCircle(color = Color(state.spec.color), radius = dotR, center = pos)
                if (state.spec.name == "Saturn") {
                    drawOval(
                        color = Color(state.spec.color).copy(alpha = 0.7f),
                        topLeft = Offset(pos.x - dotR * 2.0f, pos.y - dotR * 0.75f),
                        size = Size(dotR * 4.0f, dotR * 1.5f),
                        style = Stroke(width = 1.2.dp.toPx()),
                    )
                }
            }
        }

        // --- Overlays ---------------------------------------------------------
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 24.dp, top = 16.dp, end = 64.dp),
        ) {
            Text(
                text = "Physicalc",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground,
            )
            Text(
                text = "Interactive reference & equation solvers",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onSurfaceVariant,
            )
        }

        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(end = 8.dp, top = 4.dp),
        ) {
            Icon(Icons.Default.MoreVert, contentDescription = "Settings", tint = colors.onSurfaceVariant)
        }

        Text(
            text = "Planet positions for ${SolarSystemModel.formatDate(epochMillis)}",
            style = MaterialTheme.typography.labelMedium,
            color = colors.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 86.dp), // sits just above the floating glass bar
        )
    }
}
