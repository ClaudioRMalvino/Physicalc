package io.github.claudiormalvino.physicalc.physics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/*
 * Smoke tests for Chapter 8 (Potential Energy & Conservation of Energy).
 *
 * The Python source has NO tests and NO calculation functions for this chapter —
 * every equation is reference-only. So instead of solver tests (there is nothing to
 * solve), this suite pins down the chapter's structure: titles, counts, variable
 * keys, and the guarantee that no equation claims to be calculable.
 */
class Chapter8Test {

    private val chapter = Chapter8()

    @Test
    fun titleMatchesPythonSource() {
        assertEquals("Ch.8 - Potential Energy & Conservation of Energy", chapter.title)
    }

    @Test
    fun hasFiveEquations() {
        assertEquals(5, chapter.equations.size)
    }

    @Test
    fun hasThirteenDefinitions() {
        assertEquals(13, chapter.definitions.size)
    }

    @Test
    fun noEquationIsCalculable() {
        // Mirrors the Python chapter, where no Equation has a `calculation`.
        for (eq in chapter.equations) {
            assertNull(eq.calculation, "Equation '${eq.name}' should be reference-only")
        }
        assertTrue(chapter.calculableEquations().isEmpty())
    }

    @Test
    fun equationNamesMatchPythonSource() {
        val expected = listOf(
            "Difference in gravitational potential energy",
            "Conservation of Energy",
            "Work done by a conservative force along a closed path",
            "Condition for conservative forces in two dimensions",
            "Conservative force is the negative derivative of the potential energy",
        )
        assertEquals(expected, chapter.equations.map { it.name })
    }

    @Test
    fun gravitationalPotentialEnergyVariables() {
        val eq = chapter.equations[0]
        assertEquals(setOf("ΔUαβ", "Uβ", "Uα", "Wαβ"), eq.variables.keys)
        assertEquals("Work done from point α to point β (J)", eq.variables["Wαβ"])
    }

    @Test
    fun conservationOfEnergyVariables() {
        val eq = chapter.equations[1]
        assertEquals(setOf("Wₙc,αβ", "Δ(K+U)_{αβ}", "ΔEαβ"), eq.variables.keys)
        assertEquals("Non-conservative work (J)", eq.variables["Wₙc,αβ"])
    }

    @Test
    fun everyEquationHasVariablesWithDescriptions() {
        for (eq in chapter.equations) {
            assertTrue(eq.variables.isNotEmpty(), "Equation '${eq.name}' should list its variables")
            for ((symbol, description) in eq.variables) {
                assertTrue(symbol.isNotBlank(), "Blank variable key in '${eq.name}'")
                assertTrue(description.isNotBlank(), "Blank description for '$symbol' in '${eq.name}'")
            }
        }
    }

    @Test
    fun variableKeysAndFormulasAreBmpOnly() {
        // Non-BMP characters (e.g. mathematical-alphanumeric letters like 𝐅) render as
        // tofu on Android, so neither variable keys nor formulas may contain them.
        for (eq in chapter.equations) {
            assertTrue(eq.formula.none { it.isSurrogate() }, "Non-BMP char in formula of '${eq.name}'")
            for (symbol in eq.variables.keys) {
                assertTrue(symbol.none { it.isSurrogate() }, "Non-BMP char in key '$symbol' of '${eq.name}'")
            }
        }
    }

    @Test
    fun definitionsMatchPythonTerms() {
        val expectedTerms = listOf(
            "conservative force",
            "conserved quantity",
            "energy conservation",
            "equilibrium point",
            "exact differential",
            "mechanical energy",
            "non-conservative force",
            "non-renewable",
            "potential energy",
            "potential energy diagram",
            "potential energy difference",
            "renewable",
            "turning point",
        )
        assertEquals(expectedTerms, chapter.definitions.map { it.term })
        for (def in chapter.definitions) {
            assertTrue(def.meaning.isNotBlank(), "Blank meaning for term '${def.term}'")
        }
    }
}
