package io.github.claudiormalvino.physicalc.physics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/*
 * These tests are a direct port of your Python `tests/chapter4_calc_test.py`.
 *
 * Tolerances follow the Python `places` convention:
 * places=1 -> 0.05, places=2 -> 0.005.
 */
class Chapter4Test {

    private val calc = Chapter4.Calculate

    // ---- time_of_flight ----

    @Test
    fun timeOfFlightSolvingForT() {
        val v0 = listOf(0.0, 25.0, 100.0)
        val theta = listOf(10.0, 25.7, 75.8)
        val expected = listOf(0.0, 2.208, 19.744)

        for (i in expected.indices) {
            val result = calc.timeOfFlight(v0 = v0[i], theta = theta[i])
            assertEquals(expected[i], result, 0.005)
        }
    }

    @Test
    fun timeOfFlightSolvingForV0() {
        val t = listOf(0.0, 5.0, 20.0)
        val theta = listOf(10.0, 25.8, 45.0)
        val expected = listOf(0.0, 56.41, 138.88)

        for (i in expected.indices) {
            val result = calc.timeOfFlight(t = t[i], theta = theta[i])
            assertEquals(expected[i], result, 0.005)
        }
    }

    @Test
    fun timeOfFlightSolvingForTheta() {
        val v0 = listOf(0.0, 10.0, 56.41)
        val t = listOf(0.0, -10.0, 20.0)

        for (i in v0.indices) {
            val ex = assertFailsWith<IllegalArgumentException> {
                calc.timeOfFlight(v0 = v0[i], t = t[i])
            }
            assertEquals(
                "Cannot solve for theta with this equation. Yields complex numbers. \n Please input a value for theta.",
                ex.message,
            )
        }
    }

    // ---- trajectory ----

    @Test
    fun trajectorySolvingForTheta() {
        val v0 = listOf(0.0, 50.0, -10.0)
        val x = listOf(0.0, 10.0, 50.0)
        val y = listOf(0.0, 10.0, 25.0)

        for (i in v0.indices) {
            val ex = assertFailsWith<IllegalArgumentException> {
                calc.trajectory(v0 = v0[i], x = x[i], y = y[i])
            }
            assertEquals(
                "Cannot solve for theta with this equation. Please input a value for theta.",
                ex.message,
            )
        }
    }

    @Test
    fun trajectorySolvingForV0() {
        val theta = listOf(0.0, 45.0, 30.0)
        val x = listOf(0.0, -10.0, 50.0)
        val y = listOf(0.0, 20.0, 30.0)
        val expected = listOf(null, null, 4.626) // null => expect an exception
        val expectedMessages = listOf(
            "Division by zero is undefined",
            "Radicand cannot be negative. Outputs imaginary number.",
            null,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.trajectory(theta = theta[i], x = x[i], y = y[i])
                }
                assertEquals(expectedMessages[i], ex.message)
            } else {
                val result = calc.trajectory(theta = theta[i], x = x[i], y = y[i])
                assertEquals(e, result, 0.005)
            }
        }
    }

    @Test
    fun trajectorySolvingForX() {
        val theta = listOf(0.0, 45.0, 67.0)
        val v0 = listOf(0.0, 25.0, 80.0)
        val y = listOf(0.0, 0.0, 10.0)

        for (i in theta.indices) {
            val ex = assertFailsWith<IllegalArgumentException> {
                calc.trajectory(theta = theta[i], v0 = v0[i], y = y[i])
            }
            assertEquals(
                "Cannot solve for x with this equation. Consider calculating the range.",
                ex.message,
            )
        }
    }

    @Test
    fun trajectorySolvingForY() {
        val theta = listOf(0.0, 45.0, 67.0)
        val v0 = listOf(0.0, 25.0, 80.0)
        val x = listOf(0.0, 15.0, 30.0)
        val expected = listOf(null, 11.46, 66.15)

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.trajectory(theta = theta[i], v0 = v0[i], x = x[i])
                }
                assertEquals("Division by zero is undefined", ex.message)
            } else {
                val result = calc.trajectory(theta = theta[i], v0 = v0[i], x = x[i])
                assertEquals(e, result, 0.05)
            }
        }
    }

    // ---- projectile_range ----

    @Test
    fun rangeSolvingForR() {
        val v0 = listOf(0.0, 10.0, 40.0)
        val theta = listOf(10.0, 25.0, 45.0)
        val expected = listOf(0.0, 7.80, 162.93)

        for (i in expected.indices) {
            val result = calc.projectileRange(v0 = v0[i], theta = theta[i])
            assertEquals(expected[i], result, 0.005)
        }
    }

    @Test
    fun rangeSolvingForV0() {
        val r = listOf(0.0, 40.0, 80.0)
        val theta = listOf(0.0, 45.0, 135.0)
        val expected = listOf(null, 19.81, null)
        val expectedMessages = listOf(
            "Division by zero is undefined.",
            null,
            "Radicand cannot be negative. Outputs imaginary number.",
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.projectileRange(theta = theta[i], rTotal = r[i])
                }
                assertEquals(expectedMessages[i], ex.message)
            } else {
                val result = calc.projectileRange(theta = theta[i], rTotal = r[i])
                assertEquals(e, result, 0.05)
            }
        }
    }

    @Test
    fun rangeSolvingForTheta() {
        val r = listOf(0.0, 120.0)
        val v0 = listOf(0.0, 20.0)
        val expectedMessages = listOf(
            "Division by zero is undefined.",
            "No real solution exists. Range too large for given velocity.",
        )

        for (i in expectedMessages.indices) {
            val ex = assertFailsWith<IllegalArgumentException> {
                calc.projectileRange(v0 = v0[i], rTotal = r[i])
            }
            assertEquals(expectedMessages[i], ex.message)
        }
    }

    // ---- centripetal_accel ----

    @Test
    fun centripetalAccelSolvingForAccel() {
        val velocity = listOf(10.0, 0.0, 50.0, 10.0)
        val radius = listOf(0.0, 10.0, 2.0, -1.0)
        val expected = listOf(null, 0.0, 1250.0, null)

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.centripetalAccel(velocity = velocity[i], radius = radius[i])
                }
                assertEquals("Radius cannot be less than or equal to zero.", ex.message)
            } else {
                val result = calc.centripetalAccel(velocity = velocity[i], radius = radius[i])
                assertEquals(e, result, 0.05)
            }
        }
    }

    @Test
    fun centripetalAccelSolvingForVelocity() {
        val accel = listOf(0.0, -10.0, 10.0, 10.0)
        val radius = listOf(1.0, 1.0, -1.0, 20.0)
        val expected = listOf(0.0, null, null, 14.142)
        val expectedMessages = listOf(
            null,
            "Radicand cannot be negative. Yields an imaginary number.",
            "Radius cannot be less than or equal to zero.",
            null,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.centripetalAccel(accel = accel[i], radius = radius[i])
                }
                assertEquals(expectedMessages[i], ex.message)
            } else {
                val result = calc.centripetalAccel(accel = accel[i], radius = radius[i])
                assertEquals(e, result, 0.005)
            }
        }
    }
}
