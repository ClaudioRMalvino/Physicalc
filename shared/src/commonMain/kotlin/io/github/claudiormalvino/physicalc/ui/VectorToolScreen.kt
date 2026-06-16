package io.github.claudiormalvino.physicalc.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import io.github.claudiormalvino.physicalc.physics.Vec2
import io.github.claudiormalvino.physicalc.physics.Vec3
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin

/*
 * Vector calculator. Two vectors in 2D or 3D, drawn tip-to-tail on a live
 * canvas with the resultant, plus dot product, cross product, and the angle
 * between them. The 3D view is an orthographic projection: drag to orbit,
 * pinch to zoom, double-tap to reset. Everything is derived state and updates
 * as you type, like the unit converter.
 */

/** How vector fields are interpreted (2D only; 3D is always components). */
enum class VectorInputMode(val label: String) {
    Components("Components (x, y)"),
    Polar("Magnitude & angle"),
}

/** Which resultant the canvas and result card show. */
enum class VectorOperation(val label: String) {
    Add("A + B"),
    Subtract("A − B"),
}

/** Whether the tool works in the plane or in space. */
enum class VectorSpace(val label: String) {
    TwoD("2D"),
    ThreeD("3D"),
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VectorToolScreen(onBack: () -> Unit, onSettingsClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val accents = vectorAccents()

    var space by remember { mutableStateOf(VectorSpace.TwoD) }
    var mode by remember { mutableStateOf(VectorInputMode.Components) }
    var operation by remember { mutableStateOf(VectorOperation.Add) }
    var aFirst by remember { mutableStateOf("") }
    var aSecond by remember { mutableStateOf("") }
    var aThird by remember { mutableStateOf("") }
    var bFirst by remember { mutableStateOf("") }
    var bSecond by remember { mutableStateOf("") }
    var bThird by remember { mutableStateOf("") }

    // 2D state
    val vectorA = parseVector(aFirst, aSecond, mode)
    val vectorB = parseVector(bFirst, bSecond, mode)
    val result = if (vectorA != null && vectorB != null) {
        when (operation) {
            VectorOperation.Add -> vectorA + vectorB
            VectorOperation.Subtract -> vectorA - vectorB
        }
    } else null

    // 3D state
    val vec3A = parseVec3(aFirst, aSecond, aThird)
    val vec3B = parseVec3(bFirst, bSecond, bThird)
    val result3 = if (vec3A != null && vec3B != null) {
        when (operation) {
            VectorOperation.Add -> vec3A + vec3B
            VectorOperation.Subtract -> vec3A - vec3B
        }
    } else null

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = { Text("Vectors", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            // Space selector. Going 3D converts polar input back to components
            // (there is no single-angle form in space) and zero-fills z.
            Column {
                Text(
                    text = "Space",
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VectorSpace.entries.forEach { s ->
                        FilterChip(
                            selected = s == space,
                            onClick = {
                                if (s == space) return@FilterChip
                                if (s == VectorSpace.ThreeD) {
                                    if (mode == VectorInputMode.Polar) {
                                        vectorA?.let {
                                            aFirst = it.field1(VectorInputMode.Components)
                                            aSecond = it.field2(VectorInputMode.Components)
                                        }
                                        vectorB?.let {
                                            bFirst = it.field1(VectorInputMode.Components)
                                            bSecond = it.field2(VectorInputMode.Components)
                                        }
                                        mode = VectorInputMode.Components
                                    }
                                    if (aThird.isBlank()) aThird = "0"
                                    if (bThird.isBlank()) bThird = "0"
                                }
                                space = s
                            },
                            label = { Text(s.label) },
                        )
                    }
                }
            }

            // Input mode selector (2D only); converts typed values to the new representation.
            if (space == VectorSpace.TwoD) {
                Column {
                    Text(
                        text = "Enter vectors as",
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(8.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        VectorInputMode.entries.forEach { m ->
                            FilterChip(
                                selected = m == mode,
                                onClick = {
                                    if (m == mode) return@FilterChip
                                    vectorA?.let { aFirst = it.field1(m); aSecond = it.field2(m) }
                                    vectorB?.let { bFirst = it.field1(m); bSecond = it.field2(m) }
                                    mode = m
                                },
                                label = { Text(m.label) },
                            )
                        }
                    }
                }
            }

            VectorInputCard(
                name = "A",
                color = accents.a,
                labels = fieldLabels("A", space, mode),
                values = if (space == VectorSpace.TwoD) listOf(aFirst, aSecond) else listOf(aFirst, aSecond, aThird),
                onValueChange = { index, text ->
                    when (index) {
                        0 -> aFirst = text
                        1 -> aSecond = text
                        else -> aThird = text
                    }
                },
                caption = inputCaption("A", space, mode, vectorA, vec3A),
            )

            VectorInputCard(
                name = "B",
                color = accents.b,
                labels = fieldLabels("B", space, mode),
                values = if (space == VectorSpace.TwoD) listOf(bFirst, bSecond) else listOf(bFirst, bSecond, bThird),
                onValueChange = { index, text ->
                    when (index) {
                        0 -> bFirst = text
                        1 -> bSecond = text
                        else -> bThird = text
                    }
                },
                caption = inputCaption("B", space, mode, vectorB, vec3B),
            )

            // Operation selector
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VectorOperation.entries.forEach { op ->
                    FilterChip(
                        selected = op == operation,
                        onClick = { operation = op },
                        label = { Text(op.label) },
                    )
                }
            }

            when (space) {
                VectorSpace.TwoD -> {
                    AnimatedVisibility(
                        visible = vectorA != null || vectorB != null,
                        enter = scaleIn(initialScale = 0.95f) + fadeIn(),
                        exit = fadeOut(),
                    ) {
                        VectorCanvas(
                            vectorA = vectorA,
                            vectorB = vectorB,
                            result = result,
                            operation = operation,
                        )
                    }

                    AnimatedVisibility(
                        visible = result != null,
                        enter = scaleIn(initialScale = 0.95f) + fadeIn(),
                        exit = fadeOut(),
                    ) {
                        ResultCard(
                            vectorA = vectorA ?: Vec2.ZERO,
                            vectorB = vectorB ?: Vec2.ZERO,
                            result = result ?: Vec2.ZERO,
                            operation = operation,
                        )
                    }
                }

                VectorSpace.ThreeD -> {
                    AnimatedVisibility(
                        visible = vec3A != null || vec3B != null,
                        enter = scaleIn(initialScale = 0.95f) + fadeIn(),
                        exit = fadeOut(),
                    ) {
                        Vector3Canvas(
                            vectorA = vec3A,
                            vectorB = vec3B,
                            result = result3,
                            operation = operation,
                        )
                    }

                    AnimatedVisibility(
                        visible = result3 != null,
                        enter = scaleIn(initialScale = 0.95f) + fadeIn(),
                        exit = fadeOut(),
                    ) {
                        ResultCard3(
                            vectorA = vec3A ?: Vec3.ZERO,
                            vectorB = vec3B ?: Vec3.ZERO,
                            result = result3 ?: Vec3.ZERO,
                            operation = operation,
                        )
                    }
                }
            }

            Spacer(Modifier.height(120.dp))
        }
    }
}

private fun fieldLabels(name: String, space: VectorSpace, mode: VectorInputMode): List<String> =
    when {
        // No subscript-z exists in Unicode, so 3D uses plain axis names.
        space == VectorSpace.ThreeD -> listOf("x", "y", "z")
        mode == VectorInputMode.Components -> listOf("${name}ₓ", "${name}ᵧ")
        else -> listOf("Magnitude", "Angle (°)")
    }

/** Live readout of the representation the user is not typing in. */
private fun inputCaption(
    name: String,
    space: VectorSpace,
    mode: VectorInputMode,
    vector2: Vec2?,
    vector3: Vec3?,
): String? = when {
    space == VectorSpace.ThreeD -> vector3?.let { "‖$name‖ = ${formatNumber(it.magnitude)}" }
    mode == VectorInputMode.Components ->
        vector2?.let { "‖$name‖ = ${formatNumber(it.magnitude)},  θ = ${formatNumber(it.angleDegrees)}°" }
    else ->
        vector2?.let { "${name}ₓ = ${formatNumber(it.x)},  ${name}ᵧ = ${formatNumber(it.y)}" }
}

/** Inputs for one vector with a live caption beneath. */
@Composable
private fun VectorInputCard(
    name: String,
    color: Color,
    labels: List<String>,
    values: List<String>,
    onValueChange: (Int, String) -> Unit,
    caption: String?,
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
                Spacer(Modifier.size(8.dp))
                Text(
                    text = "Vector $name",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface,
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                labels.forEachIndexed { index, label ->
                    VectorField(
                        label = label,
                        value = values[index],
                        onValueChange = { onValueChange(index, it) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            if (caption != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = caption,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = colors.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun VectorField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val parsed = value.trim().toDoubleOrNull()
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(label) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        isError = value.isNotBlank() && parsed == null,
    )
}

/** Tip-to-tail drawing: A and B from the origin, B ghosted onto A's tip, resultant highlighted. */
@Composable
private fun VectorCanvas(
    vectorA: Vec2?,
    vectorB: Vec2?,
    result: Vec2?,
    operation: VectorOperation,
) {
    val colors = MaterialTheme.colorScheme
    val accents = vectorAccents()
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.labelMedium

    // The second leg of the tip-to-tail chain: B for A+B, −B for A−B.
    val secondLeg = vectorB?.let { if (operation == VectorOperation.Add) it else it * -1.0 }
    val ghostTip = if (vectorA != null && secondLeg != null) vectorA + secondLeg else null

    // Fit every drawn tip inside the view, never zooming past a minimum extent.
    val extent = listOfNotNull(vectorA, vectorB, result, ghostTip)
        .flatMap { listOf(abs(it.x), abs(it.y)) }
        .maxOrNull()
        ?.takeIf { it > 1e-12 } ?: 1.0
    val gridStep = niceStep(extent)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
    ) {
        Column {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(8.dp),
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val scale = (size.minDimension / 2f * 0.85f) / extent.toFloat()
                fun toScreen(v: Vec2) = center + Offset((v.x * scale).toFloat(), (-v.y * scale).toFloat())

                // Grid and axes
                val gridPx = (gridStep * scale).toFloat()
                var offsetPx = gridPx
                while (offsetPx < size.minDimension / 2f) {
                    for (sign in listOf(1f, -1f)) {
                        drawLine(
                            colors.outlineVariant.copy(alpha = 0.4f),
                            Offset(center.x + sign * offsetPx, 0f),
                            Offset(center.x + sign * offsetPx, size.height),
                        )
                        drawLine(
                            colors.outlineVariant.copy(alpha = 0.4f),
                            Offset(0f, center.y + sign * offsetPx),
                            Offset(size.width, center.y + sign * offsetPx),
                        )
                    }
                    offsetPx += gridPx
                }
                drawLine(colors.outline, Offset(center.x, 0f), Offset(center.x, size.height))
                drawLine(colors.outline, Offset(0f, center.y), Offset(size.width, center.y))

                // Tip-to-tail ghost of the second leg from A's tip; for addition
                // also A from B's tip, completing the parallelogram around the
                // resultant diagonal. (Skipped for subtraction: −B has no solid
                // arrow at the origin to anchor the fourth side to.)
                if (vectorA != null && secondLeg != null && ghostTip != null) {
                    drawArrow(toScreen(vectorA), toScreen(ghostTip), accents.b.copy(alpha = 0.5f), dashed = true)
                    if (operation == VectorOperation.Add) {
                        drawArrow(toScreen(secondLeg), toScreen(ghostTip), accents.a.copy(alpha = 0.5f), dashed = true)
                    }
                }
                if (vectorA != null) drawArrow(center, toScreen(vectorA), accents.a)
                if (vectorB != null) drawArrow(center, toScreen(vectorB), accents.b)
                if (result != null) drawArrow(center, toScreen(result), accents.resultant)

                // Labels just past each tip
                fun label(text: String, v: Vec2, color: Color) {
                    drawClampedLabel(textMeasurer, text, labelStyle.copy(color = color), toScreen(v), center)
                }
                if (vectorA != null) label("A", vectorA, accents.a)
                if (vectorB != null) label("B", vectorB, accents.b)
                if (result != null) label(operation.label, result, accents.resultant)
            }

            Text(
                text = "Grid spacing: ${formatNumber(gridStep)}",
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
            )
        }
    }
}

// Default 3D view: slightly rotated and tilted so all three axes are visible.
private const val DEFAULT_YAW = -0.6f
private const val DEFAULT_PITCH = 0.45f

/**
 * Orthographic 3D view with the same tip-to-tail construction as the 2D canvas.
 * z is up. Drag orbits (yaw around z, pitch toward top-down), pinch zooms,
 * double-tap resets. Dashed drop lines tie each tip to its shadow on the
 * xy-plane — the standard textbook cue for depth in a flat projection.
 */
@Composable
private fun Vector3Canvas(
    vectorA: Vec3?,
    vectorB: Vec3?,
    result: Vec3?,
    operation: VectorOperation,
) {
    val colors = MaterialTheme.colorScheme
    val accents = vectorAccents()
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.labelMedium
    val axisStyle = MaterialTheme.typography.labelSmall

    var yaw by remember { mutableFloatStateOf(DEFAULT_YAW) }
    var pitch by remember { mutableFloatStateOf(DEFAULT_PITCH) }
    var zoom by remember { mutableFloatStateOf(1f) }

    val secondLeg = vectorB?.let { if (operation == VectorOperation.Add) it else it * -1.0 }
    val ghostTip = if (vectorA != null && secondLeg != null) vectorA + secondLeg else null

    val extent = listOfNotNull(vectorA, vectorB, result, ghostTip)
        .flatMap { listOf(abs(it.x), abs(it.y), abs(it.z)) }
        .maxOrNull()
        ?.takeIf { it > 1e-12 } ?: 1.0
    val gridStep = niceStep(extent)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
    ) {
        Column {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(8.dp)
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, gestureZoom, _ ->
                            yaw += pan.x * 0.008f
                            // Clamp short of ±90° so the view can't flip over the pole.
                            pitch = (pitch + pan.y * 0.008f).coerceIn(-1.5f, 1.5f)
                            zoom = (zoom * gestureZoom).coerceIn(0.3f, 5f)
                        }
                    }
                    // Mouse wheel / trackpad scroll zooms on desktop, where there's
                    // no pinch. Touch never emits scroll events, so this is inert there.
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                if (event.type == PointerEventType.Scroll) {
                                    val dy = event.changes.fold(0f) { acc, c -> acc + c.scrollDelta.y }
                                    if (dy != 0f) {
                                        zoom = (zoom * (1f - dy * 0.12f)).coerceIn(0.3f, 5f)
                                        event.changes.forEach { it.consume() }
                                    }
                                }
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(onDoubleTap = {
                            yaw = DEFAULT_YAW
                            pitch = DEFAULT_PITCH
                            zoom = 1f
                        })
                    },
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                // 0.55: a tip at (e, e, e) projects up to √3·e from the origin.
                val scale = (size.minDimension / 2f * 0.55f / extent.toFloat()) * zoom

                val cy = cos(yaw); val sy = sin(yaw)
                val cp = cos(pitch); val sp = sin(pitch)

                // Yaw about the world z axis, pitch about the screen x axis,
                // then drop the depth coordinate (orthographic).
                fun project(v: Vec3): Offset {
                    val x1 = v.x * cy - v.y * sy
                    val y1 = v.x * sy + v.y * cy
                    val vertical = y1 * sp + v.z * cp
                    return center + Offset((x1 * scale).toFloat(), (-vertical * scale).toFloat())
                }
                fun project(x: Double, y: Double, z: Double) = project(Vec3(x, y, z))

                // Floor grid on the xy-plane
                val gridColor = colors.outlineVariant.copy(alpha = 0.4f)
                var k = gridStep
                while (k <= extent + 1e-9) {
                    for (s in listOf(k, -k)) {
                        drawLine(gridColor, project(s, -extent, 0.0), project(s, extent, 0.0))
                        drawLine(gridColor, project(-extent, s, 0.0), project(extent, s, 0.0))
                    }
                    k += gridStep
                }

                // Axes with labels just past their positive ends
                val axisReach = extent * 1.1
                drawLine(colors.outline, project(-axisReach, 0.0, 0.0), project(axisReach, 0.0, 0.0))
                drawLine(colors.outline, project(0.0, -axisReach, 0.0), project(0.0, axisReach, 0.0))
                drawLine(colors.outline, project(0.0, 0.0, -axisReach), project(0.0, 0.0, axisReach))
                val axisLabelStyle = axisStyle.copy(color = colors.onSurfaceVariant)
                drawCenteredLabel(textMeasurer, "x", axisLabelStyle, project(axisReach * 1.12, 0.0, 0.0))
                drawCenteredLabel(textMeasurer, "y", axisLabelStyle, project(0.0, axisReach * 1.12, 0.0))
                drawCenteredLabel(textMeasurer, "z", axisLabelStyle, project(0.0, 0.0, axisReach * 1.12))

                // Drop lines: dashed from each tip to its shadow on the xy-plane.
                val dropColor = colors.onSurfaceVariant.copy(alpha = 0.45f)
                val dropDash = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                fun dropLine(v: Vec3) {
                    if (abs(v.z) < 1e-12) return
                    val shadow = project(v.x, v.y, 0.0)
                    drawLine(dropColor, project(v), shadow, pathEffect = dropDash)
                    drawCircle(dropColor, radius = 2.5f, center = shadow)
                }
                vectorA?.let(::dropLine)
                vectorB?.let(::dropLine)
                result?.let(::dropLine)

                // Tip-to-tail ghosts, then the vectors, resultant on top.
                if (vectorA != null && secondLeg != null && ghostTip != null) {
                    drawArrow(project(vectorA), project(ghostTip), accents.b.copy(alpha = 0.5f), dashed = true)
                    if (operation == VectorOperation.Add) {
                        drawArrow(project(secondLeg), project(ghostTip), accents.a.copy(alpha = 0.5f), dashed = true)
                    }
                }
                if (vectorA != null) drawArrow(center, project(vectorA), accents.a)
                if (vectorB != null) drawArrow(center, project(vectorB), accents.b)
                if (result != null) drawArrow(center, project(result), accents.resultant)

                fun label(text: String, v: Vec3, color: Color) {
                    drawClampedLabel(textMeasurer, text, labelStyle.copy(color = color), project(v), center)
                }
                if (vectorA != null) label("A", vectorA, accents.a)
                if (vectorB != null) label("B", vectorB, accents.b)
                if (result != null) label(operation.label, result, accents.resultant)
            }

            Column(modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)) {
                Text(
                    text = "Grid spacing: ${formatNumber(gridStep)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                )
                Text(
                    text = "Drag to orbit · pinch to zoom · double-tap to reset",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant.copy(alpha = 0.7f),
                )
            }
        }
    }
}

@Composable
private fun ResultCard(
    vectorA: Vec2,
    vectorB: Vec2,
    result: Vec2,
    operation: VectorOperation,
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.primaryContainer),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = operation.label,
                style = MaterialTheme.typography.labelLarge,
                color = colors.onPrimaryContainer.copy(alpha = 0.7f),
            )
            ResultRow("Components", "(${formatNumber(result.x)}, ${formatNumber(result.y)})")
            ResultRow("Magnitude", formatNumber(result.magnitude))
            ResultRow("Direction", "${formatNumber(result.angleDegrees)}°")

            HorizontalDivider(color = colors.onPrimaryContainer.copy(alpha = 0.15f))

            ResultRow("A · B", formatNumber(vectorA dot vectorB))

            // 2D vectors live in the xy-plane, so their cross product is purely ±z.
            val crossZ = vectorA cross vectorB
            ResultRow("A × B", "(0, 0, ${formatNumber(crossZ)})")
            if (crossZ != 0.0) {
                Text(
                    text = if (crossZ > 0) "⊙  out of the page (+z)" else "⊗  into the page (−z)",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onPrimaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.End),
                )
            }

            ResultRow("Angle between", "${formatNumber(vectorA.angleBetweenDegrees(vectorB))}°")
        }
    }
}

@Composable
private fun ResultCard3(
    vectorA: Vec3,
    vectorB: Vec3,
    result: Vec3,
    operation: VectorOperation,
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.primaryContainer),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = operation.label,
                style = MaterialTheme.typography.labelLarge,
                color = colors.onPrimaryContainer.copy(alpha = 0.7f),
            )
            ResultRow("Components", formatTriple(result))
            ResultRow("Magnitude", formatNumber(result.magnitude))
            if (result.magnitude > 0.0) {
                ResultRow("Unit vector", formatTriple(result * (1.0 / result.magnitude)))
            }

            HorizontalDivider(color = colors.onPrimaryContainer.copy(alpha = 0.15f))

            ResultRow("A · B", formatNumber(vectorA dot vectorB))
            val cross = vectorA cross vectorB
            ResultRow("A × B", formatTriple(cross))
            ResultRow("‖A × B‖", formatNumber(cross.magnitude))
            ResultRow("Angle between", "${formatNumber(vectorA.angleBetweenDegrees(vectorB))}°")
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    val colors = MaterialTheme.colorScheme
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onPrimaryContainer.copy(alpha = 0.7f),
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = colors.onPrimaryContainer,
        )
    }
}

/** A line with a filled triangular head; too-short vectors are skipped. */
private fun DrawScope.drawArrow(start: Offset, end: Offset, color: Color, dashed: Boolean = false) {
    val delta = end - start
    val length = delta.getDistance()
    if (length < 2f) return

    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = 3.dp.toPx(),
        cap = StrokeCap.Round,
        pathEffect = if (dashed) PathEffect.dashPathEffect(floatArrayOf(12f, 10f)) else null,
    )

    val direction = delta / length
    val perpendicular = Offset(-direction.y, direction.x)
    val headLength = 12.dp.toPx().coerceAtMost(length / 2f)
    val base = end - direction * headLength
    val halfWidth = headLength * 0.45f
    val head = Path().apply {
        moveTo(end.x, end.y)
        lineTo(base.x + perpendicular.x * halfWidth, base.y + perpendicular.y * halfWidth)
        lineTo(base.x - perpendicular.x * halfWidth, base.y - perpendicular.y * halfWidth)
        close()
    }
    drawPath(head, color)
}

/** Text nudged just past a tip, kept fully inside the canvas. */
private fun DrawScope.drawClampedLabel(
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    text: String,
    style: TextStyle,
    tip: Offset,
    origin: Offset,
) {
    val nudge = (tip - origin).let { d ->
        val len = d.getDistance()
        if (len < 1f) Offset(8f, -8f) else d / len * 14f
    }
    val layout = textMeasurer.measure(text, style)
    val raw = tip + nudge - Offset(layout.size.width / 2f, layout.size.height / 2f)
    drawText(
        layout,
        topLeft = Offset(
            raw.x.coerceIn(0f, size.width - layout.size.width),
            raw.y.coerceIn(0f, size.height - layout.size.height),
        ),
    )
}

/** Text centered on a point, kept fully inside the canvas. */
private fun DrawScope.drawCenteredLabel(
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    text: String,
    style: TextStyle,
    position: Offset,
) {
    val layout = textMeasurer.measure(text, style)
    val raw = position - Offset(layout.size.width / 2f, layout.size.height / 2f)
    drawText(
        layout,
        topLeft = Offset(
            raw.x.coerceIn(0f, size.width - layout.size.width),
            raw.y.coerceIn(0f, size.height - layout.size.height),
        ),
    )
}

private fun parseVector(first: String, second: String, mode: VectorInputMode): Vec2? {
    val f = first.trim().toDoubleOrNull() ?: return null
    val s = second.trim().toDoubleOrNull() ?: return null
    return when (mode) {
        VectorInputMode.Components -> Vec2(f, s)
        VectorInputMode.Polar -> Vec2.fromPolar(f, s)
    }
}

private fun parseVec3(first: String, second: String, third: String): Vec3? {
    val x = first.trim().toDoubleOrNull() ?: return null
    val y = second.trim().toDoubleOrNull() ?: return null
    val z = third.trim().toDoubleOrNull() ?: return null
    return Vec3(x, y, z)
}

/** Render this vector's first/second field text under the given input mode. */
private fun Vec2.field1(mode: VectorInputMode): String = when (mode) {
    VectorInputMode.Components -> formatNumber(x)
    VectorInputMode.Polar -> formatNumber(magnitude)
}

private fun Vec2.field2(mode: VectorInputMode): String = when (mode) {
    VectorInputMode.Components -> formatNumber(y)
    VectorInputMode.Polar -> formatNumber(angleDegrees)
}

/** Grid step of 1, 2, or 5 × 10ⁿ giving a handful of lines across the view. */
private fun niceStep(extent: Double): Double {
    val raw = extent / 3.0
    val magnitude = 10.0.pow(floor(log10(raw)))
    val residual = raw / magnitude
    return magnitude * when {
        residual <= 1.0 -> 1.0
        residual <= 2.0 -> 2.0
        residual <= 5.0 -> 5.0
        else -> 10.0
    }
}

private fun formatTriple(v: Vec3): String =
    "(${formatNumber(v.x)}, ${formatNumber(v.y)}, ${formatNumber(v.z)})"

/** Compact display: round to 4 decimals, trim float noise, scientific at the extremes. */
private fun formatNumber(value: Double): String {
    if (value == 0.0) return "0"
    val magnitude = abs(value)
    if (magnitude >= 1e9 || magnitude < 1e-4) return value.toString()
    val rounded = (round(value * 1e4) / 1e4).toString()
    return if ('E' in rounded || 'e' in rounded) rounded else rounded.trimEnd('0').trimEnd('.')
}
