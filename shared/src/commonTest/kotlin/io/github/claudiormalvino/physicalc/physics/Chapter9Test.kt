package io.github.claudiormalvino.physicalc.physics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/*
 * These tests are a port of the Python `tests/chapter9_calc_test.py`, plus smoke tests
 * for every calculation branch the (very small) Python suite did not cover.
 *
 * Note: the Python file also declared `test_solving_for_mass_2`, but its body was
 * incomplete (an empty `expected` list and no assertions), so there was nothing to port;
 * the Kotlin smoke test below covers that branch instead.
 *
 * Tolerances follow the Python `places` convention: places=2 -> 0.005, places=4 -> 5e-5.
 */
class Chapter9Test {

    private val calc = Chapter9.Calculate

    // ---- inelastic_collision_momentum (ported from Python) ----

    @Test
    fun inelasticSolvingForMass1() {
        val mass2 = listOf(-10.0, 10.0, 10.0)
        val velocity1 = listOf(10.0, 0.0, 10.0)
        val velocity2 = listOf(30.0, 30.0, 30.0)
        val velocityF = listOf(20.0, 20.0, 20.0)
        val massF = listOf(10.0, 20.0, 20.0)

        // null => expect an exception (message listed alongside)
        val expectedMessages = listOf(
            "We are operating with massive objects. Make sure all objects have a mass greater than zero.",
            "Division by zero is undefined.",
            null,
        )
        val expectedValues = listOf(null, null, 10.0)

        for (i in expectedMessages.indices) {
            val msg = expectedMessages[i]
            if (msg != null) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.inelasticCollisionMomentum(
                        mass2 = mass2[i],
                        velocity1 = velocity1[i],
                        velocity2 = velocity2[i],
                        velocityF = velocityF[i],
                        massF = massF[i],
                    )
                }
                assertEquals(msg, ex.message)
            } else {
                val result = calc.inelasticCollisionMomentum(
                    mass2 = mass2[i],
                    velocity1 = velocity1[i],
                    velocity2 = velocity2[i],
                    velocityF = velocityF[i],
                    massF = massF[i],
                )
                assertEquals(expectedValues[i]!!, result, 0.005)
            }
        }
    }

    // ---- inelastic_collision_momentum (smoke tests) ----
    // Consistent data set: m1=10, v1=10, m2=10, v2=30, M=20, v=20
    // since m1*v1 + m2*v2 = 100 + 300 = 400 = M*v = 20*20.

    @Test
    fun inelasticSolvingForMass2() {
        // m2 = (M*v - m1*v1)/v2 = (400 - 100)/30 = 10.0
        val result = calc.inelasticCollisionMomentum(
            mass1 = 10.0, velocity1 = 10.0, velocity2 = 30.0, velocityF = 20.0, massF = 20.0,
        )
        assertEquals(10.0, result, 5e-5)
    }

    @Test
    fun inelasticSolvingForMass2DivisionByZero() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.inelasticCollisionMomentum(
                mass1 = 10.0, velocity1 = 10.0, velocity2 = 0.0, velocityF = 20.0, massF = 20.0,
            )
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    @Test
    fun inelasticSolvingForVelocity1() {
        // v1 = (M*v - m2*v2)/m1 = (400 - 300)/10 = 10.0
        val result = calc.inelasticCollisionMomentum(
            mass1 = 10.0, mass2 = 10.0, velocity2 = 30.0, velocityF = 20.0, massF = 20.0,
        )
        assertEquals(10.0, result, 5e-5)
    }

    @Test
    fun inelasticSolvingForVelocity2() {
        // v2 = (M*v - m1*v1)/m2 = (400 - 100)/10 = 30.0
        val result = calc.inelasticCollisionMomentum(
            mass1 = 10.0, mass2 = 10.0, velocity1 = 10.0, velocityF = 20.0, massF = 20.0,
        )
        assertEquals(30.0, result, 5e-5)
    }

    @Test
    fun inelasticSolvingForMassF() {
        // M = (m1*v1 + m2*v2)/v = 400/20 = 20.0
        val result = calc.inelasticCollisionMomentum(
            mass1 = 10.0, mass2 = 10.0, velocity1 = 10.0, velocity2 = 30.0, velocityF = 20.0,
        )
        assertEquals(20.0, result, 5e-5)
    }

    @Test
    fun inelasticSolvingForMassFDivisionByZero() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.inelasticCollisionMomentum(
                mass1 = 10.0, mass2 = 10.0, velocity1 = 10.0, velocity2 = 30.0, velocityF = 0.0,
            )
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    @Test
    fun inelasticSolvingForVelocityF() {
        // v = (m1*v1 + m2*v2)/M = 400/20 = 20.0
        val result = calc.inelasticCollisionMomentum(
            mass1 = 10.0, mass2 = 10.0, velocity1 = 10.0, velocity2 = 30.0, massF = 20.0,
        )
        assertEquals(20.0, result, 5e-5)
    }

    // ---- elastic_collision_momentum (smoke tests) ----
    // Consistent 1D elastic data set: m1=2, m2=4, v_i1=6, v_i2=0 gives
    // v_f1 = (m1-m2)/(m1+m2)*v_i1 = -2 and v_f2 = 2m1/(m1+m2)*v_i1 = 4.
    // Momentum check: 2*6 + 4*0 = 12 = 2*(-2) + 4*4.

    @Test
    fun elasticSolvingForMass1() {
        // m1 = m2(v_i2 - v_f2)/(v_f1 - v_i1) = 4*(0-4)/(-2-6) = -16/-8 = 2.0
        val result = calc.elasticCollisionMomentum(
            mass2 = 4.0, velocityI1 = 6.0, velocityI2 = 0.0, velocityF1 = -2.0, velocityF2 = 4.0,
        )
        assertEquals(2.0, result, 5e-5)
    }

    @Test
    fun elasticSolvingForMass1DivisionByZero() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.elasticCollisionMomentum(
                mass2 = 4.0, velocityI1 = 6.0, velocityI2 = 0.0, velocityF1 = 6.0, velocityF2 = 4.0,
            )
        }
        assertEquals("Divison by zero is undefined.", ex.message)
    }

    @Test
    fun elasticSolvingForMass2() {
        // m2 = m1(v_f1 - v_i1)/(v_i2 - v_f2) = 2*(-2-6)/(0-4) = -16/-4 = 4.0
        val result = calc.elasticCollisionMomentum(
            mass1 = 2.0, velocityI1 = 6.0, velocityI2 = 0.0, velocityF1 = -2.0, velocityF2 = 4.0,
        )
        assertEquals(4.0, result, 5e-5)
    }

    @Test
    fun elasticSolvingForMass2DivisionByZero() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.elasticCollisionMomentum(
                mass1 = 2.0, velocityI1 = 6.0, velocityI2 = 4.0, velocityF1 = -2.0, velocityF2 = 4.0,
            )
        }
        assertEquals("Divison by zero is undefined.", ex.message)
    }

    @Test
    fun elasticSolvingForVelocityI1() {
        // v_i1 = (m1*v_f1 + m2*v_f2 - m2*v_i2)/m1 = (-4 + 16 - 0)/2 = 6.0
        val result = calc.elasticCollisionMomentum(
            mass1 = 2.0, mass2 = 4.0, velocityI2 = 0.0, velocityF1 = -2.0, velocityF2 = 4.0,
        )
        assertEquals(6.0, result, 5e-5)
    }

    @Test
    fun elasticSolvingForVelocityI2() {
        // v_i2 = (m1*v_f1 + m2*v_f2 - m1*v_i1)/m2 = (-4 + 16 - 12)/4 = 0.0
        val result = calc.elasticCollisionMomentum(
            mass1 = 2.0, mass2 = 4.0, velocityI1 = 6.0, velocityF1 = -2.0, velocityF2 = 4.0,
        )
        assertEquals(0.0, result, 5e-5)
    }

    @Test
    fun elasticSolvingForVelocityF1() {
        // v_f1 = (m1*v_i1 + m2*v_i2 - m2*v_f2)/m1 = (12 + 0 - 16)/2 = -2.0
        val result = calc.elasticCollisionMomentum(
            mass1 = 2.0, mass2 = 4.0, velocityI1 = 6.0, velocityI2 = 0.0, velocityF2 = 4.0,
        )
        assertEquals(-2.0, result, 5e-5)
    }

    @Test
    fun elasticSolvingForVelocityF2() {
        // v_f2 = (m1*v_i1 + m2*v_i2 - m1*v_f1)/m2 = (12 + 0 + 4)/4 = 4.0
        val result = calc.elasticCollisionMomentum(
            mass1 = 2.0, mass2 = 4.0, velocityI1 = 6.0, velocityI2 = 0.0, velocityF1 = -2.0,
        )
        assertEquals(4.0, result, 5e-5)
    }

    @Test
    fun elasticNonPositiveMassThrows() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.elasticCollisionMomentum(
                mass1 = -1.0, velocityI1 = 6.0, velocityI2 = 0.0, velocityF1 = -2.0, velocityF2 = 4.0,
            )
        }
        assertEquals(
            "We are operating with massive objects.                     Make sure all objects have a mass greater than zero.",
            ex.message,
        )
    }

    // ---- rocket_equation (smoke tests) ----
    // Data set: u=100, m_i=200, m=100 => Δv = 100*ln(2) = 69.31471805599453,
    // round4 -> 69.3147. The inverse solves below feed Δv = 69.3147 back in;
    // the tiny rounding error vanishes again under round4.

    @Test
    fun rocketSolvingForDeltaV() {
        // Δv = u*ln(m_i/m) = 100*ln(2) = 69.3147 (rounded to 4 places)
        val result = calc.rocketEquation(velExhaust = 100.0, initialMass = 200.0, finalMass = 100.0)
        assertEquals(69.3147, result, 5e-5)
    }

    @Test
    fun rocketSolvingForVelExhaust() {
        // u = Δv/ln(m_i/m) = 69.3147/ln(2) = 99.99997... -> round4 = 100.0
        val result = calc.rocketEquation(deltaV = 69.3147, initialMass = 200.0, finalMass = 100.0)
        assertEquals(100.0, result, 5e-5)
    }

    @Test
    fun rocketSolvingForInitialMass() {
        // m_i = e^(Δv/u)*m = e^0.693147 * 100 = 199.99996... -> round4 = 200.0
        val result = calc.rocketEquation(deltaV = 69.3147, velExhaust = 100.0, finalMass = 100.0)
        assertEquals(200.0, result, 5e-5)
    }

    @Test
    fun rocketSolvingForFinalMass() {
        // m = m_i/e^(Δv/u) = 200/e^0.693147 = 100.000002... -> round4 = 100.0
        val result = calc.rocketEquation(deltaV = 69.3147, velExhaust = 100.0, initialMass = 200.0)
        assertEquals(100.0, result, 5e-5)
    }

    @Test
    fun rocketNonPositiveMassThrows() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.rocketEquation(deltaV = 69.3147, velExhaust = 100.0, initialMass = 200.0, finalMass = -5.0)
        }
        assertEquals(
            "We are operating with massive objects.                     Mass must be greater than zero.",
            ex.message,
        )
    }
}
