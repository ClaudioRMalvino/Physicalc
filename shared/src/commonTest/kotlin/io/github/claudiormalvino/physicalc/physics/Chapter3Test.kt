package io.github.claudiormalvino.physicalc.physics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/*
 * Tests for the Chapter3 solvers (1D kinematics and free fall).
 * Error messages are asserted byte-for-byte (typos included).
 */
class Chapter3Test {

    private val calc = Chapter3.Calculate

    // ---- position_from_vel_and_accel ----

    @Test
    fun positionSolvingForXf() {
        val x0 = listOf(0.0, 5.0, 10.0)
        val v0 = listOf(0.0, 25.0, 50.0)
        val t = listOf(0.0, 15.0, 20.0)
        val accel = listOf(0.0, 5.0, 10.0)
        val expected = listOf(0.0, 942.5, 3010.0)

        for (i in expected.indices) {
            val result = calc.positionFromVelAndAccel(x0 = x0[i], v0 = v0[i], t = t[i], accel = accel[i])
            assertEquals(expected[i], result, 0.00005)
        }
    }

    @Test
    fun positionSolvingForT() {
        val x0 = listOf(0.0, 5.0, 10.0)
        val v0 = listOf(0.0, 25.0, 50.0)
        val accel = listOf(0.0, 5.0, 10.0)
        val xf = listOf(0.0, 942.5, 3010.0)
        val expected = listOf(null, 15.0, 20.0) // null => expect an exception

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.positionFromVelAndAccel(x0 = x0[i], v0 = v0[i], accel = accel[i], xf = xf[i])
                }
                assertEquals("v₀ and a cannot both be equal to zero", ex.message)
            } else {
                val result = calc.positionFromVelAndAccel(x0 = x0[i], v0 = v0[i], accel = accel[i], xf = xf[i])
                assertEquals(e, result, 5e-8)
            }
        }
    }

    @Test
    fun positionSolvingForAccel() {
        val x0 = listOf(0.0, 5.0, 10.0)
        val v0 = listOf(0.0, 25.0, 50.0)
        val t = listOf(0.0, 15.0, 20.0)
        val xf = listOf(0.0, 942.5, 3010.0)
        val expected = listOf(null, 5.0, 10.0)

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.positionFromVelAndAccel(x0 = x0[i], v0 = v0[i], t = t[i], xf = xf[i])
                }
                assertEquals("Divison by zero is undefined.", ex.message)
            } else {
                val result = calc.positionFromVelAndAccel(x0 = x0[i], v0 = v0[i], t = t[i], xf = xf[i])
                assertEquals(e, result, 0.005)
            }
        }
    }

    @Test
    fun positionSolvingForX0() {
        val v0 = listOf(0.0, 25.0, 50.0)
        val t = listOf(0.0, 15.0, 20.0)
        val accel = listOf(0.0, 5.0, 10.0)
        val xf = listOf(0.0, 942.5, 3010.0)
        val expected = listOf(0.0, 5.0, 10.0)

        for (i in expected.indices) {
            val result = calc.positionFromVelAndAccel(accel = accel[i], v0 = v0[i], t = t[i], xf = xf[i])
            assertEquals(expected[i], result, 0.005)
        }
    }

    @Test
    fun positionSolvingForV0() {
        val x0 = listOf(0.0, 5.0, 10.0)
        val t = listOf(0.0, 15.0, 20.0)
        val accel = listOf(0.0, 5.0, 10.0)
        val xf = listOf(0.0, 942.5, 3010.0)
        val expected = listOf(null, 25.0, 50.0)

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.positionFromVelAndAccel(accel = accel[i], x0 = x0[i], t = t[i], xf = xf[i])
                }
                assertEquals("Division by zero is undefined", ex.message)
            } else {
                val result = calc.positionFromVelAndAccel(accel = accel[i], x0 = x0[i], t = t[i], xf = xf[i])
                assertEquals(e, result, 0.005)
            }
        }
    }

    // ---- velocity_from_distance ----

    @Test
    fun velocitySolvingForVf() {
        val x0 = listOf(0.0, 5.0, 10.0)
        val v0 = listOf(0.0, 25.0, 5.0)
        val xf = listOf(0.0, 15.0, 20.0)
        val accel = listOf(0.0, 5.0, -10.0)
        val expected = listOf(0.0, 26.93, null)

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.velocityFromDistance(x0 = x0[i], v0 = v0[i], xf = xf[i], accel = accel[i])
                }
                assertEquals("The discriminant cannot be negative", ex.message)
            } else {
                val result = calc.velocityFromDistance(x0 = x0[i], v0 = v0[i], xf = xf[i], accel = accel[i])
                assertEquals(e, result, 0.005)
            }
        }
    }

    @Test
    fun velocitySolvingForX0() {
        val xf = listOf(0.0, 5.0, 10.0)
        val v0 = listOf(0.0, 25.0, 50.0)
        val accel = listOf(0.0, 5.0, 10.0)
        val vf = listOf(0.0, 60.0, 120.0)
        val expected = listOf(null, -292.5, -585.0)

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.velocityFromDistance(xf = xf[i], v0 = v0[i], accel = accel[i], vf = vf[i])
                }
                assertEquals("acceleration cannot be equal to zero", ex.message)
            } else {
                val result = calc.velocityFromDistance(xf = xf[i], v0 = v0[i], accel = accel[i], vf = vf[i])
                assertEquals(e, result, 5e-8)
            }
        }
    }

    @Test
    fun velocitySolvingForV0() {
        val x0 = listOf(0.0, 5.0, 10.0)
        val xf = listOf(0.0, 25.0, 50.0)
        val accel = listOf(0.0, 5.0, 20.0)
        val vf = listOf(0.0, 60.0, 10.0)
        val expected = listOf(0.0, 58.31, null)

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.velocityFromDistance(x0 = x0[i], xf = xf[i], accel = accel[i], vf = vf[i])
                }
                assertEquals("The discriminant cannot be negative", ex.message)
            } else {
                val result = calc.velocityFromDistance(x0 = x0[i], xf = xf[i], accel = accel[i], vf = vf[i])
                assertEquals(e, result, 0.005)
            }
        }
    }

    // ---- height_of_free_fall ----

    @Test
    fun heightSolvingForYf() {
        val y0 = listOf(0.0, 5.0, 10.0)
        val v0 = listOf(0.0, 25.0, 50.0)
        val t = listOf(0.0, 15.0, 20.0)
        val expected = listOf(0.0, -724.75, -954.0)

        for (i in expected.indices) {
            val result = calc.heightOfFreeFall(y0 = y0[i], v0 = v0[i], t = t[i])
            assertEquals(expected[i], result, 0.00005)
        }
    }

    @Test
    fun heightSolvingForT() {
        val y0 = listOf(0.0, 5.0, 10.0)
        val v0 = listOf(0.0, 25.0, 50.0)
        val yf = listOf(0.0, 30.0, 0.0)
        val expected = listOf(0.0, 1.367, 10.3795)

        for (i in expected.indices) {
            val result = calc.heightOfFreeFall(y0 = y0[i], v0 = v0[i], yf = yf[i])
            assertEquals(expected[i], result, 5e-8)
        }
    }

    // ---- vel_free_fall_from_height ----

    @Test
    fun velFreeFallSolvingForVf() {
        val y0 = listOf(0.0, 5.0, 10.0)
        val v0 = listOf(0.0, 25.0, 5.0)
        val yf = listOf(0.0, 15.0, 20.0)
        val expected = listOf(0.0, 20.70266, null)

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.velFreeFallFromHeight(y0 = y0[i], v0 = v0[i], yf = yf[i])
                }
                assertEquals("The discriminant cannot be negative", ex.message)
            } else {
                val result = calc.velFreeFallFromHeight(y0 = y0[i], v0 = v0[i], yf = yf[i])
                assertEquals(e, result, 0.005)
            }
        }
    }

    @Test
    fun velFreeFallSolvingForY0() {
        val yf = listOf(0.0, 5.0, 10.0)
        val v0 = listOf(0.0, 25.0, 50.0)
        val vf = listOf(0.0, 60.0, 120.0)
        val expected = listOf(0.0, 156.476, 615.906)

        for (i in expected.indices) {
            val result = calc.velFreeFallFromHeight(yf = yf[i], v0 = v0[i], vf = vf[i])
            assertEquals(expected[i], result, 0.005)
        }
    }

    @Test
    fun velFreeFallSolvingForV0() {
        val y0 = listOf(0.0, 5.0, 60.0)
        val yf = listOf(0.0, 25.0, 50.0)
        val vf = listOf(0.0, 60.0, 5.0)
        val expected = listOf(0.0, 63.188, null)

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.velFreeFallFromHeight(y0 = y0[i], yf = yf[i], vf = vf[i])
                }
                assertEquals("The discriminant cannot be negative", ex.message)
            } else {
                val result = calc.velFreeFallFromHeight(y0 = y0[i], yf = yf[i], vf = vf[i])
                assertEquals(e, result, 0.005)
            }
        }
    }
}
