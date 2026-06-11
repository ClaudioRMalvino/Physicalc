package io.github.claudiormalvino.physicalc.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FormulaParserTest {

    private fun styleAt(parsed: AnnotatedString, index: Int) =
        parsed.spanStyles.filter { index >= it.start && index < it.end }.map { it.item }

    @Test
    fun subscriptRendersSmallerAndLower() {
        val parsed = FormulaParser.parse("x_0")
        assertEquals("x0", parsed.text)
        val zeroStyles = styleAt(parsed, parsed.text.indexOf('0'))
        assertTrue(zeroStyles.any { (it.baselineShift?.multiplier ?: 0f) < 0f }, "expected lowered baseline")
    }

    @Test
    fun superscriptRendersRaised() {
        val parsed = FormulaParser.parse("t^2")
        assertEquals("t2", parsed.text)
        val twoStyles = styleAt(parsed, parsed.text.indexOf('2'))
        assertTrue(twoStyles.any { (it.baselineShift?.multiplier ?: 0f) > 0f }, "expected raised baseline")
    }

    @Test
    fun bracedScriptsTakeWholeGroup() {
        val parsed = FormulaParser.parse("v_{max}")
        assertEquals("vmax", parsed.text)
        val mStyles = styleAt(parsed, parsed.text.indexOf('m'))
        assertTrue(mStyles.any { (it.baselineShift?.multiplier ?: 0f) < 0f })
    }

    @Test
    fun hatProducesBoldWithCombiningCircumflex() {
        val parsed = FormulaParser.parse("\\hat{i}")
        assertEquals("î", parsed.text)
        val iStyles = styleAt(parsed, 0)
        assertTrue(iStyles.any { it.fontWeight == FontWeight.Bold }, "unit vectors should be bold")
    }

    @Test
    fun namedSymbolsResolve() {
        assertEquals("Δx", FormulaParser.parse("\\Delta x").text.replace(" ", ""))
        assertEquals("∫a(t)dt", FormulaParser.parse("\\int a(t)dt").text.replace(" ", ""))
        assertEquals("ω", FormulaParser.parse("\\omega").text)
    }

    @Test
    fun fracRendersInlineWithParensOnlyWhenNeeded() {
        assertEquals("1/2", FormulaParser.parse("\\frac{1}{2}").text)
        assertEquals("(x - x0)/t", FormulaParser.parse("\\frac{x - x_0}{t}").text)
    }

    @Test
    fun singleLettersItalicWordsUpright() {
        val parsed = FormulaParser.parse("s = (Total distance)/(Elapsed time)")
        val sStyles = styleAt(parsed, 0)
        assertTrue(sStyles.any { it.fontStyle == FontStyle.Italic }, "variable s should be italic")
        val tIndex = parsed.text.indexOf("Total")
        val tStyles = styleAt(parsed, tIndex)
        assertTrue(tStyles.none { it.fontStyle == FontStyle.Italic }, "prose should stay upright")
    }

    @Test
    fun differentialsStayItalic() {
        val parsed = FormulaParser.parse("v(t) = dx/dt")
        val dIndex = parsed.text.indexOf("dx")
        val dStyles = styleAt(parsed, dIndex)
        assertTrue(dStyles.any { it.fontStyle == FontStyle.Italic })
    }

    @Test
    fun functionsStayUpright() {
        val parsed = FormulaParser.parse("y = sin x")
        val sinIndex = parsed.text.indexOf("sin")
        val sinStyles = styleAt(parsed, sinIndex)
        assertTrue(sinStyles.none { it.fontStyle == FontStyle.Italic })
    }

    @Test
    fun unknownCommandShownVerbatimNotSwallowed() {
        assertEquals("\\nope x", FormulaParser.parse("\\nope x").text)
    }

    @Test
    fun juxtaposedFunctionSplits() {
        // "mgsinθ" must typeset as italic mg, upright sin, italic θ
        val parsed = FormulaParser.parse("a = mgsinθ")
        val mIndex = parsed.text.indexOf("mg")
        assertTrue(styleAt(parsed, mIndex).any { it.fontStyle == FontStyle.Italic }, "mg should be italic")
        val sinIndex = parsed.text.indexOf("sin")
        assertTrue(styleAt(parsed, sinIndex).none { it.fontStyle == FontStyle.Italic }, "sin should be upright")
        val thetaIndex = parsed.text.indexOf("θ")
        assertTrue(styleAt(parsed, thetaIndex).any { it.fontStyle == FontStyle.Italic }, "θ should be italic")
    }

    @Test
    fun ordinaryWordsDoNotSplit() {
        // "constant" contains "tan" but must stay one upright word
        val parsed = FormulaParser.parse("p = mv = constant")
        val cIndex = parsed.text.indexOf("constant")
        for (k in cIndex until cIndex + "constant".length) {
            assertTrue(styleAt(parsed, k).none { it.fontStyle == FontStyle.Italic }, "constant must stay upright at $k")
        }
    }

    @Test
    fun limIsUpright() {
        val parsed = FormulaParser.parse("ω = lim(Δt→0) Δθ/Δt")
        val limIndex = parsed.text.indexOf("lim")
        assertTrue(styleAt(parsed, limIndex).none { it.fontStyle == FontStyle.Italic })
    }

    @Test
    fun unicodePassthroughStillWorks() {
        // Existing chapter strings (plain Unicode) must keep rendering unchanged.
        assertEquals("Δx = x - x₀", FormulaParser.parse("Δx = x - x₀").text)
    }
}
