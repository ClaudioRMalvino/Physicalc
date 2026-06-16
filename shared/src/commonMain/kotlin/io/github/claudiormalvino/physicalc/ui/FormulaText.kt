package io.github.claudiormalvino.physicalc.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import kotlin.math.max

/*
 * FormulaText — lightweight TeX-lite physics-formula renderer.
 *
 *   x_0          subscript                  v_{max}      multi-char subscript
 *   t^2          superscript                e^{-kt}      multi-char superscript
 *   \hat{i}      bold unit vector + hat     \vec{F}      bold vector
 *   \frac{a}{b}  inline fraction a/b        \Delta       named symbols (greek etc.)
 *
 * Plain text: short letter runs and differentials are italic (x, vt, dx);
 * function names and longer words are upright (sin, Total). Pure Compose
 * text — no images or WebView — identical on Android and desktop.
 */

@Composable
fun FormulaText(
    formula: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    textAlign: TextAlign? = null,
) {
    // Top-level split into whitespace-delimited tokens (wrap points); each token's
    // \frac groups become stacked 2D fractions, the rest stays inline styled text.
    val tokens = remember(formula) { tokenize(formula) }
    val barColor = color.takeOrElse { LocalContentColor.current }

    Layout(
        modifier = modifier,
        content = { tokens.forEach { SegRow(it.segments, color, style, barColor) } },
    ) { measurables, constraints ->
        val maxWidth = constraints.maxWidth
        val gapPx = run {
            val fontPx = if (style.fontSize.isSpecified) style.fontSize.toPx() else 16.sp.toPx()
            (fontPx * 0.25f).toInt()
        }

        val placeables = measurables.map { it.measure(Constraints(maxWidth = maxWidth)) }
        val lineOf = IntArray(placeables.size)
        val xOf = IntArray(placeables.size)
        val lineHeights = ArrayList<Int>()
        val lineWidths = ArrayList<Int>()

        var x = 0
        var line = 0
        var lineHeight = 0
        var lineHasContent = false

        placeables.forEachIndexed { idx, p ->
            val gap = if (lineHasContent && tokens[idx].spaceBefore) gapPx else 0
            if (lineHasContent && x + gap + p.width > maxWidth) {
                lineHeights.add(lineHeight); lineWidths.add(x)
                line++; x = 0; lineHeight = 0; lineHasContent = false
            }
            val g = if (lineHasContent && tokens[idx].spaceBefore) gapPx else 0
            xOf[idx] = x + g
            lineOf[idx] = line
            x += g + p.width
            lineHeight = max(lineHeight, p.height)
            lineHasContent = true
        }
        if (lineHasContent) { lineHeights.add(lineHeight); lineWidths.add(x) }

        val totalHeight = lineHeights.sum()
        val outWidth = if (constraints.hasBoundedWidth) maxWidth else (lineWidths.maxOrNull() ?: 0)
        val lineTop = IntArray(lineHeights.size)
        var acc = 0
        for (l in lineHeights.indices) { lineTop[l] = acc; acc += lineHeights[l] }

        layout(outWidth, totalHeight) {
            placeables.forEachIndexed { idx, p ->
                val l = lineOf[idx]
                val centerShift = if (textAlign == TextAlign.Center) (outWidth - lineWidths[l]) / 2 else 0
                // Center each piece on its line, so a fraction's bar lines up with adjacent text.
                val y = lineTop[l] + (lineHeights[l] - p.height) / 2
                p.place(xOf[idx] + centerShift, y)
            }
        }
    }
}

/** A horizontal run of segments (text + fractions), vertically centered on a shared axis. */
@Composable
private fun SegRow(segments: List<Seg>, color: Color, style: TextStyle, barColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        segments.forEach { seg ->
            when (seg) {
                is Seg.Txt -> {
                    val rendered = remember(seg.src) { FormulaParser.parse(seg.src) }
                    Text(text = rendered, color = color, style = style, softWrap = false, maxLines = 1)
                }
                is Seg.Frac -> Fraction(seg.num, seg.den, color, style, barColor)
                is Seg.Sqrt -> Radical(seg.radicand, color, style, barColor)
            }
        }
    }
}

/** A radical sign with a vinculum drawn over the radicand. */
@Composable
private fun Radical(radicand: String, color: Color, style: TextStyle, barColor: Color) {
    val segs = remember(radicand) { splitSegments(radicand) }
    val hook = 9.dp
    val topGap = 4.dp
    Box(
        modifier = Modifier
            .drawBehind {
                val sw = 1.4.dp.toPx()
                val hookPx = hook.toPx()
                val topY = sw
                // Vinculum over the radicand, from the top of the radical tick rightward.
                drawLine(barColor, Offset(hookPx, topY), Offset(size.width, topY), sw)
                // The radical check mark, rising to meet the vinculum.
                drawPath(
                    Path().apply {
                        moveTo(hookPx * 0.18f, size.height * 0.6f)
                        lineTo(hookPx * 0.5f, size.height - sw)
                        lineTo(hookPx, topY)
                    },
                    barColor,
                    style = Stroke(width = sw),
                )
            }
            .padding(start = hook + 2.dp, top = topGap),
    ) {
        SegRow(segs, color, style, barColor)
    }
}

/** Numerator stacked over denominator with a rule as wide as the wider half. */
@Composable
private fun Fraction(num: String, den: String, color: Color, style: TextStyle, barColor: Color) {
    // Fraction halves typeset a touch smaller, as in printed math; nesting compounds.
    val half = if (style.fontSize.isSpecified) style.copy(fontSize = style.fontSize * 0.94f) else style
    Layout(
        content = {
            SegRow(remember(num) { splitSegments(num) }, color, half, barColor)
            SegRow(remember(den) { splitSegments(den) }, color, half, barColor)
            Box(Modifier.background(barColor))
        },
    ) { measurables, _ ->
        val numP = measurables[0].measure(Constraints())
        val denP = measurables[1].measure(Constraints())
        val sidePad = 2.dp.roundToPx()
        val barWidth = max(numP.width, denP.width) + 2 * sidePad
        val barThick = max(1, (1.5.dp).roundToPx())
        val barP = measurables[2].measure(Constraints.fixed(barWidth, barThick))
        val gap = 3.dp.roundToPx()

        val width = barWidth
        val height = numP.height + gap + barThick + gap + denP.height
        layout(width, height) {
            numP.place((width - numP.width) / 2, 0)
            barP.place(0, numP.height + gap)
            denP.place((width - denP.width) / 2, numP.height + gap + barThick + gap)
        }
    }
}

// ---- Top-level structure: tokens and segments -------------------------------

/** A piece of a token: plain styled text, a stacked fraction, or a radical. */
private sealed interface Seg {
    data class Txt(val src: String) : Seg
    data class Frac(val num: String, val den: String) : Seg
    data class Sqrt(val radicand: String) : Seg
}

/** A whitespace-delimited token; [spaceBefore] marks where a gap (and wrap) may go. */
private data class Token(val segments: List<Seg>, val spaceBefore: Boolean)

/** Splits a formula into tokens at top-level (brace-depth-0) whitespace. */
private fun tokenize(formula: String): List<Token> {
    val tokens = ArrayList<Token>()
    var i = 0
    var spaceBefore = false
    while (i < formula.length) {
        if (formula[i].isWhitespace()) { spaceBefore = true; i++; continue }
        val start = i
        var depth = 0
        while (i < formula.length) {
            when (formula[i]) {
                '{' -> depth++
                '}' -> depth--
                else -> if (formula[i].isWhitespace() && depth == 0) break
            }
            i++
        }
        tokens.add(Token(splitSegments(formula.substring(start, i)), spaceBefore))
        spaceBefore = false
    }
    return tokens
}

/** Splits a token (or a fraction half) into text and \frac segments, in order. */
private fun splitSegments(src: String): List<Seg> {
    val segs = ArrayList<Seg>()
    val text = StringBuilder()
    fun flush() { if (text.isNotEmpty()) { segs.add(Seg.Txt(text.toString())); text.clear() } }

    var i = 0
    while (i < src.length) {
        if (src.startsWith("\\frac", i)) {
            val (num, afterNum) = readBraceGroup(src, i + 5)
            val (den, afterDen) = readBraceGroup(src, afterNum)
            if (num != null && den != null) {
                flush()
                segs.add(Seg.Frac(num, den))
                i = afterDen
                continue
            }
        } else if (src.startsWith("\\sqrt", i)) {
            val (radicand, after) = readBraceGroup(src, i + 5)
            if (radicand != null) {
                flush()
                segs.add(Seg.Sqrt(radicand))
                i = after
                continue
            }
        }
        text.append(src[i]); i++
    }
    flush()
    return segs
}

/** Reads a balanced {group} starting at the first '{' at or after [from]. */
private fun readBraceGroup(src: String, from: Int): Pair<String?, Int> {
    var i = from
    while (i < src.length && src[i] != '{') {
        if (!src[i].isWhitespace()) return null to from // not a brace group
        i++
    }
    if (i >= src.length) return null to from
    var depth = 0
    val open = i
    while (i < src.length) {
        when (src[i]) {
            '{' -> depth++
            '}' -> { depth--; if (depth == 0) return src.substring(open + 1, i) to (i + 1) }
        }
        i++
    }
    return null to from // unbalanced
}

/** Pure parser: TeX-lite source -> styled AnnotatedString. Kept UI-free so it's unit-testable. */
object FormulaParser {

    /** \name -> symbol. Extend freely as chapters need more. */
    private val SYMBOLS: Map<String, String> = mapOf(
        // Greek (lowercase)
        "alpha" to "α", "beta" to "β", "gamma" to "γ", "delta" to "δ",
        "epsilon" to "ε", "theta" to "θ", "lambda" to "λ", "mu" to "μ",
        "pi" to "π", "rho" to "ρ", "sigma" to "σ", "tau" to "τ",
        "phi" to "φ", "omega" to "ω",
        // Greek (uppercase)
        "Delta" to "Δ", "Theta" to "Θ", "Sigma" to "Σ", "Omega" to "Ω", "Phi" to "Φ",
        // Operators & misc
        "int" to "∫", "sum" to "∑", "sqrt" to "√", "infty" to "∞",
        "cdot" to "·", "times" to "×", "pm" to "±",
        "approx" to "≈", "propto" to "∝", "le" to "≤", "ge" to "≥", "neq" to "≠",
        "rightarrow" to "→",
    )

    /** Multi-letter names that must stay upright even though they're "words". */
    private val FUNCTIONS = setOf("sin", "cos", "tan", "ln", "log", "exp", "min", "max", "lim", "constant")

    /** Longest-first, so decomposition prefers e.g. "tan" over shorter overlaps. */
    private val FUNCTIONS_BY_LENGTH = FUNCTIONS.sortedByDescending { it.length }

    /** Max letters allowed OUTSIDE function names when splitting a run like "mgsinθ". */
    private const val MAX_LOOSE_LETTERS = 4

    private const val COMBINING_HAT = '̂'

    /** Script position for sub/superscripts. */
    private enum class Script { NONE, SUB, SUP }

    private data class Ctx(
        val bold: Boolean = false,
        val script: Script = Script.NONE,
    )

    fun parse(source: String): AnnotatedString = buildAnnotatedString {
        emit(source, Ctx())
    }

    // ---- Recursive emitter ------------------------------------------------------

    private fun androidx.compose.ui.text.AnnotatedString.Builder.emit(src: String, ctx: Ctx) {
        var i = 0
        val plain = StringBuilder()

        fun flushPlain() {
            if (plain.isNotEmpty()) {
                emitPlain(plain.toString(), ctx)
                plain.clear()
            }
        }

        while (i < src.length) {
            when (val c = src[i]) {
                '\\' -> {
                    flushPlain()
                    i = emitCommand(src, i + 1, ctx)
                }
                '_' -> {
                    flushPlain()
                    val (arg, next) = readArg(src, i + 1)
                    emit(arg, ctx.copy(script = Script.SUB))
                    i = next
                }
                '^' -> {
                    flushPlain()
                    val (arg, next) = readArg(src, i + 1)
                    emit(arg, ctx.copy(script = Script.SUP))
                    i = next
                }
                '{' -> {
                    flushPlain()
                    val (group, next) = readGroup(src, i)
                    emit(group, ctx)
                    i = next
                }
                else -> {
                    plain.append(c)
                    i++
                }
            }
        }
        flushPlain()
    }

    /** Handles a \command starting at [start] (just past the backslash). Returns next index. */
    private fun androidx.compose.ui.text.AnnotatedString.Builder.emitCommand(
        src: String,
        start: Int,
        ctx: Ctx,
    ): Int {
        var i = start
        while (i < src.length && src[i].isLetter()) i++
        val name = src.substring(start, i)

        when (name) {
            "hat", "vec" -> {
                val (arg, next) = readArg(src, i)
                // Bold the vector, then attach a combining circumflex to its last char.
                emit(arg, ctx.copy(bold = true))
                withStyle(spanStyle(ctx.copy(bold = true))) { append(COMBINING_HAT) }
                return next
            }
            "frac" -> {
                val (top, afterTop) = readArg(src, i)
                val (bottom, afterBottom) = readArg(src, afterTop)
                // Inline fraction: parenthesize multi-character halves for clarity.
                val needTopParens = top.count { !it.isWhitespace() } > 1
                val needBotParens = bottom.count { !it.isWhitespace() } > 1
                if (needTopParens) emitPlain("(", ctx)
                emit(top, ctx)
                if (needTopParens) emitPlain(")", ctx)
                emitPlain("/", ctx)
                if (needBotParens) emitPlain("(", ctx)
                emit(bottom, ctx)
                if (needBotParens) emitPlain(")", ctx)
                return afterBottom
            }
            "text" -> {
                val (arg, next) = readArg(src, i)
                withStyle(spanStyle(ctx)) { append(arg) } // verbatim, upright
                return next
            }
            else -> {
                val symbol = SYMBOLS[name]
                if (symbol != null) {
                    withStyle(spanStyle(ctx)) { append(symbol) }
                } else {
                    // Unknown command: show it verbatim so mistakes are visible, not silent.
                    withStyle(spanStyle(ctx)) { append("\\$name") }
                }
                return i
            }
        }
    }

    /**
     * Plain text run: apply typesetting conventions word-by-word.
     * Letters are split into tokens; everything else passes through unchanged.
     */
    private fun androidx.compose.ui.text.AnnotatedString.Builder.emitPlain(text: String, ctx: Ctx) {
        var i = 0
        while (i < text.length) {
            val c = text[i]
            if (c.isLetter()) {
                var j = i
                while (j < text.length && text[j].isLetter()) j++
                val word = text.substring(i, j)
                // Short letter runs are (products of) variables -> italic: x, vt, mgh, dx.
                // Function names and longer words are prose/operators -> upright: sin, Total.
                // Runs like "mgsinθ" split into italic variables around the upright function.
                for ((segment, isFunction) in splitWord(word)) {
                    val italic = !isFunction && segment.length <= 3
                    withStyle(spanStyle(ctx, italic)) { append(segment) }
                }
                i = j
            } else {
                var j = i
                while (j < text.length && !text[j].isLetter()) j++
                withStyle(spanStyle(ctx)) { append(text.substring(i, j)) }
                i = j
            }
        }
    }

    /**
     * Splits a letter run into (segment, isFunction) pieces so that physics
     * juxtapositions typeset correctly: "mgsinθ" -> italic "mg", upright "sin",
     * italic "θ". Splitting only triggers when the run decomposes into function
     * names plus a few short variable chunks — ordinary words ("constant",
     * "distance") fail that test and stay whole.
     */
    private fun splitWord(word: String): List<Pair<String, Boolean>> {
        if (word in FUNCTIONS) return listOf(word to true)
        val parts = decomposeFrom(word, 0, MAX_LOOSE_LETTERS)
        // Only use the split when it actually found an embedded function.
        return if (parts != null && parts.any { it.second }) parts else listOf(word to false)
    }

    /** Recursive decomposition: function names or loose chunks of <=2 letters. */
    private fun decomposeFrom(run: String, start: Int, looseBudget: Int): List<Pair<String, Boolean>>? {
        if (start == run.length) return emptyList()
        for (fn in FUNCTIONS_BY_LENGTH) {
            if (run.startsWith(fn, start)) {
                val rest = decomposeFrom(run, start + fn.length, looseBudget)
                if (rest != null) return listOf(fn to true) + rest
            }
        }
        for (len in 2 downTo 1) {
            if (start + len <= run.length && looseBudget >= len) {
                val rest = decomposeFrom(run, start + len, looseBudget - len)
                if (rest != null) return listOf(run.substring(start, start + len) to false) + rest
            }
        }
        return null
    }

    private fun spanStyle(ctx: Ctx, italic: Boolean = false): SpanStyle {
        var style = SpanStyle(
            fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
            fontWeight = if (ctx.bold) FontWeight.Bold else null,
        )
        when (ctx.script) {
            Script.SUB -> style = style.copy(fontSize = 0.72.em, baselineShift = BaselineShift(-0.18f))
            Script.SUP -> style = style.copy(fontSize = 0.72.em, baselineShift = BaselineShift(0.40f))
            Script.NONE -> Unit
        }
        return style
    }

    // ---- Tokenizer helpers ------------------------------------------------------

    /** Reads a command/script argument at [i]: either a {braced group} or a single char. */
    private fun readArg(src: String, i: Int): Pair<String, Int> {
        if (i >= src.length) return "" to i
        return if (src[i] == '{') readGroup(src, i) else src[i].toString() to i + 1
    }

    /** Reads a brace-balanced {group} starting at [open]; returns content and index past '}'. */
    private fun readGroup(src: String, open: Int): Pair<String, Int> {
        var depth = 0
        var i = open
        while (i < src.length) {
            when (src[i]) {
                '{' -> depth++
                '}' -> {
                    depth--
                    if (depth == 0) return src.substring(open + 1, i) to i + 1
                }
            }
            i++
        }
        // Unbalanced braces: return the rest verbatim rather than crashing.
        return src.substring(open + 1) to src.length
    }
}
