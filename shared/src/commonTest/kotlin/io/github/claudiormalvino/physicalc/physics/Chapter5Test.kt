package io.github.claudiormalvino.physicalc.physics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/*
 * These tests are a direct port of your Python `tests/chapter5_calc_test.py`.
 *
 * Tolerances match the Python `places`:
 *   places=1 -> 0.05, places=2 -> 0.005.
 */
class Chapter5Test {

    private val calc = Chapter5.Calculate

    // The Python ValueError message includes the indentation of its line
    // continuations, so the expected string is reproduced verbatim here.
    private val springConstNegativeMessage =
        "Spring constant cannot be negative. " +
            "                        Consider the relation between the direction " +
            "                        of displacment and the restorative force."

    // ---- normal_force ----

    @Test
    fun normalForceSolvingForNormalF() {
        val mass = listOf(-1.0, 10.0, 50.0, 50.0)
        val theta = listOf(0.0, 0.0, 90.0, 75.0)
        val expected = listOf(null, 98.2, 0.0, 127.08) // null => expect an exception

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.normalForce(mass = mass[i], theta = theta[i])
                }
                assertEquals("Mass cannot be negative.", ex.message)
            } else {
                val result = calc.normalForce(mass = mass[i], theta = theta[i])
                assertEquals(e, result, 0.005)
            }
        }
    }

    @Test
    fun normalForceSolvingForTheta() {
        val mass = listOf(-1.0, 10.0, 50.0, 50.0)
        val normalF = listOf(98.2, 98.2, 0.0, 127.08)
        val expected = listOf(null, 0.0, 90.0, 75.0)

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.normalForce(mass = mass[i], normalF = normalF[i])
                }
                assertEquals("Mass cannot be negative.", ex.message)
            } else {
                val result = calc.normalForce(mass = mass[i], normalF = normalF[i])
                assertEquals(e, result, 0.005)
            }
        }
    }

    @Test
    fun normalForceSolvingForMass() {
        val normalF = listOf(98.2, 98.2, 0.0, 127.0)
        val theta = listOf(180.0, 0.0, 90.0, 75.0)
        val expected = listOf("Mass cannot be negative.", 10.0, "Division by zero is undefined.", 50.0)

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.normalForce(theta = theta[i], normalF = normalF[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.normalForce(theta = theta[i], normalF = normalF[i])
                assertEquals(e as Double, result, 0.05)
            }
        }
    }

    // ---- hookes_law ----

    @Test
    fun hookesLawSolvingForForce() {
        val springConst = listOf(0.0, -2.0, 2.0, 10.0)
        val displacement = listOf(0.10, 1.0, -1.0, 2.0)
        val expected = listOf<Any?>(0.0, "Spring constant (k) cannot be a negative value.", 2.0, -20.0)

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.hookesLaw(springConst = springConst[i], displacement = displacement[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.hookesLaw(springConst = springConst[i], displacement = displacement[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    @Test
    fun hookesLawSolvingForSpringConst() {
        val force = listOf(0.0, 20.0, -10.0, 2.0)
        val displacement = listOf(10.0, 10.0, 0.0, -0.10)
        val expected = listOf<Any?>(
            0.0,
            springConstNegativeMessage,
            "Divison by zero is undefined.",
            20.0,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.hookesLaw(force = force[i], displacement = displacement[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.hookesLaw(force = force[i], displacement = displacement[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    @Test
    fun hookesLawSolvingForDisplacement() {
        val force = listOf(0.0, 20.0, -10.0, 2.0)
        val springConst = listOf(10.0, 0.0, -10.0, 2.0)
        val expected = listOf<Any?>(
            0.0,
            "Divison by zero is undefined.",
            "Spring constant (k) cannot be a negative value.",
            -1.0,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.hookesLaw(force = force[i], springConst = springConst[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.hookesLaw(force = force[i], springConst = springConst[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }
}
