package io.github.claudiormalvino.physicalc.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/*
 * Hand-drawn vector glyphs for the chapter cards — one tiny "essence of the
 * chapter" pictogram each, drawn with Canvas so they are crisp at any size,
 * tint with the theme, and work identically on Android / desktop / iOS.
 *
 * All coordinates are fractions of the canvas size (0..1), so the glyphs scale
 * with whatever size the badge gives them.
 */

@Composable
fun ChapterIcon(
    chapterNumber: Int,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val sw = size.width * 0.085f // stroke width
        when (chapterNumber) {
            3 -> motion1D(tint, sw)
            4 -> axes3D(tint, sw)
            5 -> forceOnBlock(tint, sw)
            6 -> inclinedPlane(tint, sw)
            7 -> movingBall(tint, sw)
            8 -> potentialWell(tint, sw)
            9 -> collision(tint, sw)
            10 -> rotation(tint, sw)
            11 -> spinningDisc(tint, sw)
            12 -> balanceScale(tint, sw)
            13 -> orbit(tint, sw)
            14 -> waves(tint, sw)
            else -> drawCircle(tint, radius = size.width * 0.12f, center = p(0.5f, 0.5f))
        }
    }
}

// ---- shared helpers --------------------------------------------------------

private fun DrawScope.p(x: Float, y: Float): Offset = Offset(size.width * x, size.height * y)

/** Line with an arrowhead at `to`. */
private fun DrawScope.arrow(from: Offset, to: Offset, color: Color, sw: Float) {
    drawLine(color, from, to, strokeWidth = sw, cap = StrokeCap.Round)
    val angle = atan2(to.y - from.y, to.x - from.x)
    val head = sw * 2.6f
    for (spread in floatArrayOf(2.65f, -2.65f)) {
        val a = angle + spread
        drawLine(color, to, Offset(to.x + head * cos(a), to.y + head * sin(a)), strokeWidth = sw, cap = StrokeCap.Round)
    }
}

// ---- chapter glyphs ---------------------------------------------------------

/** Ch.3 — motion along a straight line. */
private fun DrawScope.motion1D(c: Color, sw: Float) {
    arrow(p(0.12f, 0.5f), p(0.88f, 0.5f), c, sw)
}

/** Ch.4 — x/y/z coordinate axes. */
private fun DrawScope.axes3D(c: Color, sw: Float) {
    val origin = p(0.34f, 0.66f)
    arrow(origin, p(0.34f, 0.10f), c, sw)  // y up
    arrow(origin, p(0.92f, 0.66f), c, sw)  // x right
    arrow(origin, p(0.08f, 0.92f), c, sw)  // z toward viewer
}

/** Ch.5 — force arrow acting on a block. */
private fun DrawScope.forceOnBlock(c: Color, sw: Float) {
    drawRect(
        color = c,
        topLeft = p(0.14f, 0.36f),
        size = Size(size.width * 0.32f, size.height * 0.30f),
        style = Stroke(width = sw),
    )
    arrow(p(0.54f, 0.51f), p(0.90f, 0.51f), c, sw)
}

/** Ch.6 — block on an inclined plane. */
private fun DrawScope.inclinedPlane(c: Color, sw: Float) {
    val tri = Path().apply {
        moveTo(size.width * 0.12f, size.height * 0.80f)
        lineTo(size.width * 0.88f, size.height * 0.80f)
        lineTo(size.width * 0.88f, size.height * 0.28f)
        close()
    }
    drawPath(tri, c, style = Stroke(width = sw, cap = StrokeCap.Round))
    drawCircle(c, radius = size.width * 0.09f, center = p(0.45f, 0.47f))
}

/** Ch.7 — ball moving fast (motion streaks). */
private fun DrawScope.movingBall(c: Color, sw: Float) {
    drawCircle(c, radius = size.width * 0.16f, center = p(0.64f, 0.5f))
    drawLine(c, p(0.10f, 0.34f), p(0.34f, 0.34f), sw, StrokeCap.Round)
    drawLine(c, p(0.06f, 0.50f), p(0.38f, 0.50f), sw, StrokeCap.Round)
    drawLine(c, p(0.10f, 0.66f), p(0.34f, 0.66f), sw, StrokeCap.Round)
}

/** Ch.8 — potential well with a ball settled at the bottom. */
private fun DrawScope.potentialWell(c: Color, sw: Float) {
    val well = Path().apply {
        moveTo(size.width * 0.12f, size.height * 0.22f)
        quadraticTo(size.width * 0.5f, size.height * 1.05f, size.width * 0.88f, size.height * 0.22f)
    }
    drawPath(well, c, style = Stroke(width = sw, cap = StrokeCap.Round))
    drawCircle(c, radius = size.width * 0.09f, center = p(0.5f, 0.52f))
}

/** Ch.9 — two bodies colliding head-on (arrows above, bodies below). */
private fun DrawScope.collision(c: Color, sw: Float) {
    drawCircle(c, radius = size.width * 0.13f, center = p(0.24f, 0.62f))
    drawCircle(c, radius = size.width * 0.13f, center = p(0.76f, 0.62f))
    arrow(p(0.08f, 0.26f), p(0.40f, 0.26f), c, sw * 0.9f)
    arrow(p(0.92f, 0.26f), p(0.60f, 0.26f), c, sw * 0.9f)
}

/** Ch.10 — rotation arc with arrowhead. */
private fun DrawScope.rotation(c: Color, sw: Float) {
    val r = size.width * 0.28f
    val center = p(0.5f, 0.5f)
    drawArc(
        color = c,
        startAngle = 45f,
        sweepAngle = 260f,
        useCenter = false,
        topLeft = Offset(center.x - r, center.y - r),
        size = Size(r * 2, r * 2),
        style = Stroke(width = sw, cap = StrokeCap.Round),
    )
    // Arrowhead at the arc's end (305°), pointing along the direction of travel.
    val endAngle = (45f + 260f) * (kotlin.math.PI.toFloat() / 180f)
    val end = Offset(center.x + r * cos(endAngle), center.y + r * sin(endAngle))
    val tangent = endAngle + (kotlin.math.PI.toFloat() / 2f)
    val head = sw * 2.6f
    for (spread in floatArrayOf(2.65f, -2.65f)) {
        val a = tangent + spread
        drawLine(c, end, Offset(end.x + head * cos(a), end.y + head * sin(a)), sw, StrokeCap.Round)
    }
}

/** Ch.11 — spinning disc with its angular-momentum axis. */
private fun DrawScope.spinningDisc(c: Color, sw: Float) {
    drawOval(
        color = c,
        topLeft = p(0.16f, 0.52f),
        size = Size(size.width * 0.68f, size.height * 0.26f),
        style = Stroke(width = sw),
    )
    arrow(p(0.5f, 0.65f), p(0.5f, 0.10f), c, sw)
}

/** Ch.12 — balance scale. */
private fun DrawScope.balanceScale(c: Color, sw: Float) {
    drawLine(c, p(0.16f, 0.32f), p(0.84f, 0.32f), sw, StrokeCap.Round)   // beam
    drawLine(c, p(0.5f, 0.32f), p(0.5f, 0.80f), sw, StrokeCap.Round)     // post
    drawLine(c, p(0.34f, 0.80f), p(0.66f, 0.80f), sw, StrokeCap.Round)   // base
    drawLine(c, p(0.16f, 0.32f), p(0.16f, 0.46f), sw, StrokeCap.Round)   // left drop
    drawLine(c, p(0.84f, 0.32f), p(0.84f, 0.46f), sw, StrokeCap.Round)   // right drop
    drawLine(c, p(0.06f, 0.50f), p(0.26f, 0.50f), sw, StrokeCap.Round)   // left pan
    drawLine(c, p(0.74f, 0.50f), p(0.94f, 0.50f), sw, StrokeCap.Round)   // right pan
}

/** Ch.13 — top-down solar system: rayed sun in the center, planet on its orbit.
 *  The rays are what keep this from reading as an atom — nuclei don't shine. */
private fun DrawScope.orbit(c: Color, sw: Float) {
    val center = p(0.5f, 0.5f)
    // Sun
    drawCircle(c, radius = size.width * 0.10f, center = center)
    // Rays (8, at 45° steps, kept tight around the sun so the ring stands apart)
    for (k in 0 until 8) {
        val a = k * (kotlin.math.PI.toFloat() / 4f)
        val inner = size.width * 0.14f
        val outer = size.width * 0.20f
        drawLine(
            color = c,
            start = Offset(center.x + inner * cos(a), center.y + inner * sin(a)),
            end = Offset(center.x + outer * cos(a), center.y + outer * sin(a)),
            strokeWidth = sw * 0.8f,
            cap = StrokeCap.Round,
        )
    }
    // Orbit ring — pushed out for clear separation from the rays
    val orbitR = size.width * 0.46f
    drawCircle(c, radius = orbitR, center = center, style = Stroke(width = sw * 0.9f))
    // Planet on the ring (upper right)
    val pa = -kotlin.math.PI.toFloat() / 4f
    drawCircle(c, radius = size.width * 0.07f, center = Offset(center.x + orbitR * cos(pa), center.y + orbitR * sin(pa)))
}

/** Ch.14 — flowing waves. */
private fun DrawScope.waves(c: Color, sw: Float) {
    for (y in floatArrayOf(0.38f, 0.62f)) {
        val wave = Path().apply {
            moveTo(size.width * 0.10f, size.height * y)
            quadraticTo(size.width * 0.275f, size.height * (y - 0.16f), size.width * 0.45f, size.height * y)
            quadraticTo(size.width * 0.625f, size.height * (y + 0.16f), size.width * 0.80f, size.height * y)
            quadraticTo(size.width * 0.87f, size.height * (y - 0.07f), size.width * 0.92f, size.height * (y - 0.04f))
        }
        drawPath(wave, c, style = Stroke(width = sw, cap = StrokeCap.Round))
    }
}
