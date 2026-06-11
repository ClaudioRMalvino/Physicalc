package io.github.claudiormalvino.physicalc.physics

/*
 * This file is the Kotlin equivalent of your Python `base_chapter.py`.
 *
 * Kotlin learning notes:
 *  - `data class` is like Python's @dataclass: it auto-generates the constructor,
 *    equals(), hashCode(), toString(), and copy() for you.
 *  - `val` means read-only (like a Python attribute you never reassign). `var` is mutable.
 *  - `String?` (with the `?`) means "nullable" — it may hold null. A plain `String`
 *    can NEVER be null. This is Kotlin's headline feature; the compiler forces you
 *    to handle the null case, which kills a whole category of runtime crashes.
 */

/**
 * A calculation takes the variables the user has filled in and returns the value of
 * the single unknown.
 *
 * The map is keyed by the variable's display symbol (e.g. "x₀", "v₀", "t").
 * The ONE variable the user wants solved for is passed as `null` — this mirrors your
 * Python design where you leave one argument as `None` and the function solves for it.
 *
 * `typealias` just gives a long type a short, readable name. Here,
 * `Calculation` == "a function taking Map<String, Double?> and returning Double".
 */
typealias Calculation = (variables: Map<String, Double?>) -> Double

/**
 * A physics equation — the Kotlin twin of your Python `Equation` dataclass.
 *
 * @param name        Human-readable name, e.g. "Displacement".
 * @param formula     The formula as text, e.g. "Δx = x - x₀".
 * @param variables   Map of symbol -> description, e.g. "x" -> "Final position (m)".
 * @param calculation Optional solver. `null` means this equation is reference-only
 *                    (no calculator), exactly like `calculation=None` in Python.
 */
data class Equation(
    val name: String,
    val formula: String,
    val variables: Map<String, String> = emptyMap(),
    val calculation: Calculation? = null,
)

/**
 * A physics term and its plain-language meaning — the twin of your `Definition` dataclass.
 */
data class Definition(
    val term: String,
    val meaning: String,
)

/**
 * Base class for every chapter — the twin of your Python `PhysicsChapter`.
 *
 * `abstract` means you cannot create a `PhysicsChapter` directly; you can only create a
 * concrete subclass (like `Chapter3`) that fills in the `equations` and `definitions`.
 *
 * `abstract val equations` is a property that every subclass MUST provide
 * (the compiler enforces it), just like the empty lists you populate in each Python chapter.
 */
abstract class PhysicsChapter(
    val title: String,
    val description: String = "",
) {
    abstract val equations: List<Equation>
    abstract val definitions: List<Definition>

    /**
     * Returns only the equations that can actually be solved (those with a calculation).
     *
     * `filter { ... }` is Kotlin's equivalent of your Python list comprehension
     * `[eq for eq in self.equations if eq.calculation is not None]`.
     * `it` is the implicit name for the current item inside the lambda.
     */
    fun calculableEquations(): List<Equation> =
        equations.filter { it.calculation != null }
}
