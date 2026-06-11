package io.github.claudiormalvino.physicalc.ui

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.em

/*
 * FormulaText — a lightweight physics-formula renderer.
 *
 * Renders a TeX-lite markup into properly typeset Compose text:
 *
 *   x_0          subscript                  v_{max}      multi-char subscript
 *   t^2          superscript                e^{-kt}      multi-char superscript
 *   \hat{i}      bold unit vector + hat     \vec{F}      bold vector
 *   \frac{a}{b}  inline fraction a/b        \Delta       named symbols (greek etc.)
 *
 * Typesetting conventions applied automatically to plain text:
 *   - single letters are italic (physics variables: x, v, t, ω ...)
 *   - differentials stay italic (dx, dt, dθ)
 *   - multi-letter words are upright (sin, cos, Total, distance ...)
 *
 * Everything is plain Compose text — no images, no WebView, no dependencies —
 * so it works identically on Android and desktop, scrolls like ordinary
 * text, and obeys the Material theme color/typography.
 */

@Composable
fun FormulaText(
    formula: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    textAlign: TextAlign? = null,
) {
    // Parsing is pure; remember() caches it per formula string.
    val rendered = remember(formula) { FormulaParser.parse(formula) }
    Text(
        text = rendered,
        modifier = modifier,
        color = color,
        style = style,
        textAlign = textAlign,
    )
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
