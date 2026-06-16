package io.github.claudiormalvino.physicalc.physics

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.round

/*
 * Core model types shared by every physics chapter.
 *
 * Conventions used by all chapter solvers (canonical reference):
 *  - Solvers take nullable parameters; pass null for the single unknown to solve for.
 *  - Error-message strings intentionally match the original Python source verbatim
 *    (typos and odd spacing included) because tests assert them exactly.
 *  - Results are rounded via roundResult before being returned.
 */

/**
 * Solves an equation from the user-supplied variables, keyed by display symbol
 * (e.g. "x₀", "v₀", "t"). The single unknown is passed as null.
 */
typealias Calculation = (variables: Map<String, Double?>) -> Double

/** One solver result, with an optional label when an answer has several roots. */
data class Root(val value: Double, val label: String? = null)

/**
 * A solver that may yield more than one valid answer (e.g. a quadratic). Returns
 * the physical roots; throw IllegalArgumentException when none exist.
 */
typealias MultiCalculation = (variables: Map<String, Double?>) -> List<Root>

/**
 * A physics equation. Reference-only when both `calculation` and
 * `multiCalculation` are null.
 *
 * Most solvers return a single value via `calculation`. An equation whose
 * inverse can have several roots supplies `multiCalculation` instead, which the
 * UI uses in preference and renders each root.
 *
 * `solvableFor` lists the symbols the solver can actually invert for; null means
 * every variable is solvable (the common case). Use it when a variable can only
 * be supplied as input — e.g. an angle the closed form can't be inverted for —
 * so the UI doesn't offer a "Solve for" option that only throws.
 */
data class Equation(
    val name: String,
    val formula: String,
    val variables: Map<String, String> = emptyMap(),
    val calculation: Calculation? = null,
    val solvableFor: Set<String>? = null,
    val multiCalculation: MultiCalculation? = null,
) {
    val isCalculable: Boolean get() = calculation != null || multiCalculation != null
}

/**
 * A physics term and its plain-language meaning.
 */
data class Definition(
    val term: String,
    val meaning: String,
)

/**
 * Rounds a solver result for stable display: 4 decimal places at ordinary magnitudes,
 * 6 significant figures below 1e-3 so tiny results (e.g. ~1e-9 N) don't collapse to 0.0.
 */
internal fun roundResult(value: Double): Double {
    if (value == 0.0 || !value.isFinite()) return value
    if (abs(value) >= 1e-3) return round(value * 10_000.0) / 10_000.0
    val magnitude = floor(log10(abs(value)))
    val factor = 10.0.pow(5.0 - magnitude)
    return round(value * factor) / factor
}

/**
 * Base class for every chapter; subclasses provide the equations and definitions.
 */
abstract class PhysicsChapter(
    val title: String,
    val description: String = "",
) {
    abstract val equations: List<Equation>
    abstract val definitions: List<Definition>

    /** Equations that have a solver attached. */
    fun calculableEquations(): List<Equation> =
        equations.filter { it.isCalculable }
}
