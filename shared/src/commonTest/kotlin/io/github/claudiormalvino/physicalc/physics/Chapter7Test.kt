package io.github.claudiormalvino.physicalc.physics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/*
 * These tests are a direct port of your Python `tests/chapter7_cal_test.py`.
 *
 * Tolerances follow the Python `places`:
 * places=1 -> 0.05, places=2 -> 0.005.
 *
 * Note: the long "massive objects" message used by work_by_gravity contains the
 * whitespace produced by the Python source's backslash line-continuation inside the
 * string literal (17 spaces between "objects." and "Mass"), reproduced verbatim.
 */
class Chapter7Test {

    private val calc = Chapter7.Calculate

    // ---- work_constant_force ----

    @Test
    fun workConstantForceSolvingForWork() {
        val constF = listOf(0.0, 100.0, 500.0)
        val distance = listOf(10.0, 10.0, 20.0)
        val theta = listOf(0.0, 0.0, 90.0)
        val expected = listOf(0.0, 1000.0, 0.0)

        for (i in expected.indices) {
            val result = calc.workConstantForce(constF = constF[i], distance = distance[i], theta = theta[i])
            assertEquals(expected[i], result, 0.005)
        }
    }

    @Test
    fun workConstantForceSolvingForConstF() {
        val work = listOf(0.0, 1000.0, 2000.0, 1000.0)
        val theta = listOf(0.0, 0.0, 90.0, 45.0)
        val distance = listOf(10.0, 10.0, 20.0, 0.0)
        val expected = listOf(0.0, 100.0, null, null) // null => expect an exception

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.workConstantForce(work = work[i], distance = distance[i], theta = theta[i])
                }
                assertEquals("Division by zero is undefined.", ex.message)
            } else {
                val result = calc.workConstantForce(work = work[i], distance = distance[i], theta = theta[i])
                assertEquals(e, result, 0.005)
            }
        }
    }

    @Test
    fun workConstantForceSolvingForDistance() {
        val work = listOf(0.0, 500.0, 500.0, 1000.0)
        val constF = listOf(500.0, 0.0, 500.0, 1000.0)
        val theta = listOf(0.0, 0.0, 90.0, 0.0)
        val expected = listOf(0.0, null, null, 1.0)

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.workConstantForce(work = work[i], constF = constF[i], theta = theta[i])
                }
                assertEquals("Division by zero is undefined.", ex.message)
            } else {
                val result = calc.workConstantForce(work = work[i], constF = constF[i], theta = theta[i])
                assertEquals(e, result, 0.005)
            }
        }
    }

    @Test
    fun workConstantForceSolvingForTheta() {
        val work = listOf(1000.0, 0.0, 0.0)
        val constF = listOf(100.0, 0.0, 10.0)
        val distance = listOf(10.0, 10.0, 0.0)
        val expected = listOf(0.0, null, null)

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.workConstantForce(work = work[i], constF = constF[i], distance = distance[i])
                }
                assertEquals("Division by zero is undefined.", ex.message)
            } else {
                val result = calc.workConstantForce(work = work[i], constF = constF[i], distance = distance[i])
                assertEquals(e, result, 0.005)
            }
        }
    }

    // ---- work_by_gravity ----

    @Test
    fun workByGravitySolvingForWork() {
        val mass = listOf(0.0, 5.0, 45.0)
        val initialHeight = listOf(20.0, 10.0, 100.0)
        val finalHeight = listOf(10.0, 10.0, 0.0)
        val expected = listOf(null, 0.0, 44190.0)

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.workByGravity(mass = mass[i], initialHeight = initialHeight[i], finalHeight = finalHeight[i])
                }
                assertEquals(
                    "We are operating with massive objects.                 Mass must be greater than zero.",
                    ex.message,
                )
            } else {
                val result = calc.workByGravity(mass = mass[i], initialHeight = initialHeight[i], finalHeight = finalHeight[i])
                assertEquals(e, result, 0.005)
            }
        }
    }

    @Test
    fun workByGravitySolvingForMass() {
        val work = listOf(1000.0, 1000.0, 1000.0)
        val initialHeight = listOf(1000.0, 0.0, 100.0)
        val finalHeight = listOf(0.0, 100.0, 100.0)
        val expected = listOf(
            0.1018,
            "Mass cannot be negative. Check your signs or initial and final heights.",
            "Division by zero is undefined.",
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.workByGravity(work = work[i], initialHeight = initialHeight[i], finalHeight = finalHeight[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.workByGravity(work = work[i], initialHeight = initialHeight[i], finalHeight = finalHeight[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    @Test
    fun workByGravitySolvingForInitialHeight() {
        val work = listOf(1000.0, 2000.0, 1000.0)
        val mass = listOf(25.0, 25.0, 50.0)
        val finalHeight = listOf(0.0, 0.0, 0.0)
        val expected = listOf(4.07, 8.14, 2.03)

        for (i in expected.indices) {
            val result = calc.workByGravity(work = work[i], mass = mass[i], finalHeight = finalHeight[i])
            assertEquals(expected[i], result, 0.05)
        }
    }

    @Test
    fun workByGravitySolvingForFinalHeight() {
        val work = listOf(1000.0, 2000.0, 1000.0)
        val mass = listOf(25.0, 25.0, 50.0)
        val initialHeight = listOf(4.07, 8.14, 2.03)
        val expected = listOf(0.0, 0.0, 0.0)

        for (i in expected.indices) {
            val result = calc.workByGravity(work = work[i], mass = mass[i], initialHeight = initialHeight[i])
            assertEquals(expected[i], result, 0.05)
        }
    }

    // ---- work_by_spring ----

    @Test
    fun workBySpringSolvingForWork() {
        val springConst = listOf(-1.0, 2.0, 10.0)
        val initialXpos = listOf(0.0, 3.0, 3.0)
        val finalXpos = listOf(0.0, 0.0, 0.0)
        val expected = listOf(null, 9.0, 45.0)

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.workBySpring(springConst = springConst[i], initialXpos = initialXpos[i], finalXpos = finalXpos[i])
                }
                assertEquals("Spring constant cannot be a negative value.", ex.message)
            } else {
                val result = calc.workBySpring(springConst = springConst[i], initialXpos = initialXpos[i], finalXpos = finalXpos[i])
                assertEquals(e, result, 0.005)
            }
        }
    }

    @Test
    fun workBySpringSolvingForSpringConst() {
        val work = listOf(400.0, 400.0, 400.0)
        val initialXpos = listOf(0.25, 0.25, 2.0)
        val finalXpos = listOf(0.0, 0.25, 0.0)
        val expected = listOf(12800.0, null, 200.0)

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.workBySpring(work = work[i], initialXpos = initialXpos[i], finalXpos = finalXpos[i])
                }
                assertEquals("Division by zero is undefined.", ex.message)
            } else {
                val result = calc.workBySpring(work = work[i], initialXpos = initialXpos[i], finalXpos = finalXpos[i])
                assertEquals(e, result, 0.005)
            }
        }
    }

    @Test
    fun workBySpringSolvingForInitialXpos() {
        val work = listOf(500.0, 100.0)
        val springConst = listOf(10.0, 1000.0)
        val finalXpos = listOf(0.0, -10.0)
        val expected = listOf(100.0, 100.2)

        for (i in expected.indices) {
            val result = calc.workBySpring(work = work[i], springConst = springConst[i], finalXpos = finalXpos[i])
            assertEquals(expected[i], result, 0.05)
        }
    }

    @Test
    fun workBySpringSolvingForFinalXpos() {
        val work = listOf(500.0, 100.0)
        val springConst = listOf(10.0, 1000.0)
        val initialXpos = listOf(0.0, -10.0)
        val expected = listOf(-100.0, 99.8)

        for (i in expected.indices) {
            val result = calc.workBySpring(work = work[i], springConst = springConst[i], initialXpos = initialXpos[i])
            assertEquals(expected[i], result, 0.005)
        }
    }

    // ---- kinetic_energy ----

    @Test
    fun kineticEnergySolvingForKineticEnergy() {
        val mass = listOf(0.0, -10.0, 45.0)
        val velocity = listOf(25.0, 10.0, 30.0)
        val expected = listOf(null, null, 20250.0)

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.kineticEnergy(mass = mass[i], velocity = velocity[i])
                }
                assertEquals("We are operating with massive objects. Mass must be greater than zero.", ex.message)
            } else {
                val result = calc.kineticEnergy(mass = mass[i], velocity = velocity[i])
                assertEquals(e, result, 0.005)
            }
        }
    }

    @Test
    fun kineticEnergySolvingForMass() {
        val kineticE = listOf(20250.0, -20250.0)
        val velocity = listOf(30.0, 30.0)
        val expected = listOf(45.0, null)

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.kineticEnergy(kineticE = kineticE[i], velocity = velocity[i], mass = null)
                }
                assertEquals("We are operating with massive objects. Mass must be greater than zero.", ex.message)
            } else {
                val result = calc.kineticEnergy(kineticE = kineticE[i], velocity = velocity[i], mass = null)
                assertEquals(e, result, 0.005)
            }
        }
    }

    @Test
    fun kineticEnergySolvingForVelocity() {
        val kineticE = listOf(20250.0, -20250.0, -20250.0)
        val mass = listOf(45.0, -45.0, 45.0)
        val expected = listOf(
            30.0,
            "We are operating with massive objects. Mass must be greater than zero.",
            "Negative radicand yields an imaginary number. Check the value of your kinetic energy",
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.kineticEnergy(kineticE = kineticE[i], mass = mass[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.kineticEnergy(kineticE = kineticE[i], mass = mass[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    // ---- kinetic_energy_momentum ----

    @Test
    fun kineticEnergyMomentumSolvingForKineticEnergy() {
        val mass = listOf(-45.0, 45.0, 45.0)
        val momentum = listOf(30.0, -30.0, 30.0)
        val expected = listOf(
            "We are operating with massive objects. Mass must be greater than zero.",
            "This is a scalar product. Velocity cannot be negative",
            10.0,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.kineticEnergyMomentum(momentum = momentum[i], mass = mass[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.kineticEnergyMomentum(momentum = momentum[i], mass = mass[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    @Test
    fun kineticEnergyMomentumSolvingForMass() {
        val kineticE = listOf(-2050.0, 2050.0, 10.0)
        val momentum = listOf(30.0, -30.0, 30.0)
        val expected = listOf(
            "We are operating with massive objects. Mass must be greater than zero. Check your value for kinetic energy.",
            "This is a scalar product. Velocity cannot be negative",
            45.0,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.kineticEnergyMomentum(momentum = momentum[i], kineticE = kineticE[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.kineticEnergyMomentum(momentum = momentum[i], kineticE = kineticE[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    @Test
    fun kineticEnergyMomentumSolvingForMomentum() {
        val kineticE = listOf(2050.0, -2050.0, 10.0)
        val mass = listOf(45.0, 45.0, 45.0)
        val expected = listOf(
            429.53,
            "Negative radicand yields an imaginary number. Check the value of your kinetic energy",
            30.0,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.kineticEnergyMomentum(mass = mass[i], kineticE = kineticE[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.kineticEnergyMomentum(mass = mass[i], kineticE = kineticE[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    // ---- work_energy_theorem ----

    @Test
    fun workEnergyTheoremSolvingForNetWork() {
        val mass = listOf(-45.0, 45.0, 45.0)
        val finalVel = listOf(10.0, 10.0, 20.0)
        val initialVel = listOf(20.0, 20.0, 10.0)
        val expected = listOf(null, -6750.0, 6750.0)

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.workEnergyTheorem(mass = mass[i], finalVel = finalVel[i], initialVel = initialVel[i])
                }
                assertEquals("We are operating with massive objects. Mass must be greater than zero.", ex.message)
            } else {
                val result = calc.workEnergyTheorem(mass = mass[i], finalVel = finalVel[i], initialVel = initialVel[i])
                assertEquals(e, result, 0.005)
            }
        }
    }

    @Test
    fun workEnergyTheoremSolvingForMass() {
        val netWork = listOf(-6750.0, 6750.0, 6750.0)
        val finalVel = listOf(10.0, 20.0, 10.0)
        val initialVel = listOf(20.0, 10.0, 20.0)
        val expected = listOf(45.0, 45.0, null)

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.workEnergyTheorem(netWork = netWork[i], finalVel = finalVel[i], initialVel = initialVel[i])
                }
                assertEquals(
                    "We are operating with massive objects. Mass must be greater than zero. Check your values",
                    ex.message,
                )
            } else {
                val result = calc.workEnergyTheorem(netWork = netWork[i], finalVel = finalVel[i], initialVel = initialVel[i])
                assertEquals(e, result, 0.005)
            }
        }
    }

    @Test
    fun workEnergyTheoremSolvingForFinalVelocity() {
        val netWork = listOf(-6750.0, 6750.0, 6750.0)
        val mass = listOf(1.0, 45.0, 45.0)
        val initialVel = listOf(1.0, 10.0, 20.0)
        val expected = listOf(null, 20.0, 26.4575)

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.workEnergyTheorem(netWork = netWork[i], mass = mass[i], initialVel = initialVel[i])
                }
                assertEquals("Negative radicand produces an imaginary number. Check your signs.", ex.message)
            } else {
                val result = calc.workEnergyTheorem(netWork = netWork[i], mass = mass[i], initialVel = initialVel[i])
                assertEquals(e, result, 0.005)
            }
        }
    }

    @Test
    fun workEnergyTheoremSolvingForInitialVelocity() {
        val netWork = listOf(6750.0, -6750.0, 6750.0)
        val mass = listOf(1.0, 45.0, 45.0)
        val finalVel = listOf(1.0, 20.0, 26.4575)
        val expected = listOf(null, 26.4575, 20.0)

        for (i in expected.indices) {
            val e = expected[i]
            if (e == null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.workEnergyTheorem(netWork = netWork[i], mass = mass[i], finalVel = finalVel[i])
                }
                assertEquals("Negative radicand produces an imaginary number. Check your signs.", ex.message)
            } else {
                val result = calc.workEnergyTheorem(netWork = netWork[i], mass = mass[i], finalVel = finalVel[i])
                assertEquals(e, result, 0.005)
            }
        }
    }
}
