package io.github.claudiormalvino.physicalc.physics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/*
 * Smoke tests for the Chapter10 solvers (Fixed-Axis Rotation); expected values hand-derived.
 * Error messages are asserted byte-for-byte (typos included).
 */
class Chapter10Test {

    private val calc = Chapter10.Calculate

    // ---- angular_position : θ = s/r ----

    @Test
    fun angularPositionSolvingForTheta() {
        // θ = s/r = 6/2 = 3
        val result = calc.angularPosition(arcLength = 6.0, radius = 2.0)
        assertEquals(3.0, result, 0.005)
    }

    @Test
    fun angularPositionSolvingForRadiusWithThetaZeroThrows() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.angularPosition(theta = 0.0, arcLength = 5.0)
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    @Test
    fun angularPositionNegativeRadiusThrows() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.angularPosition(arcLength = 6.0, radius = -2.0)
        }
        assertEquals("Radius of rotational trajectory cannot be less than or equal to zero.", ex.message)
    }

    // ---- tangential_speed : v(t) = rω ----

    @Test
    fun tangentialSpeedSolvingForVt() {
        // v = rω = 2 * 3 = 6
        val result = calc.tangentialSpeed(radius = 2.0, angularVel = 3.0)
        assertEquals(6.0, result, 0.005)
    }

    @Test
    fun tangentialSpeedSolvingForAngularVel() {
        // ω = v/r = 10/4 = 2.5
        val result = calc.tangentialSpeed(tangSpeed = 10.0, radius = 4.0)
        assertEquals(2.5, result, 0.005)
    }

    @Test
    fun tangentialSpeedSolvingForRadiusWithOmegaZeroThrows() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.tangentialSpeed(tangSpeed = 5.0, angularVel = 0.0)
        }
        assertEquals("Divison by zero is undefined.", ex.message)
    }

    // ---- average_angular_vel : ω(ave) = (ω₀ + ω(f))/2 ----

    @Test
    fun averageAngularVelSolvingForAve() {
        // ω(ave) = (10 + 20)/2 = 15
        val result = calc.averageAngularVel(initAngularVel = 10.0, finalAngularVel = 20.0)
        assertEquals(15.0, result, 0.005)
    }

    @Test
    fun averageAngularVelSolvingForInit() {
        // ω₀ = 2*ω(ave) - ω(f) = 2*15 - 20 = 10
        val result = calc.averageAngularVel(aveAngularVel = 15.0, finalAngularVel = 20.0)
        assertEquals(10.0, result, 0.005)
    }

    // ---- angular_displacement : θ(f) = θ₀ + ω(ave)t ----

    @Test
    fun angularDisplacementSolvingForThetaFinal() {
        // θ(f) = 1 + 2*3 = 7
        val result = calc.angularDisplacement(thetaInit = 1.0, aveAngularVel = 2.0, time = 3.0)
        assertEquals(7.0, result, 0.005)
    }

    @Test
    fun angularDisplacementSolvingForAveVelWithTimeZeroThrows() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.angularDisplacement(thetaFinal = 5.0, thetaInit = 1.0, time = 0.0)
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    @Test
    fun angularDisplacementNegativeTimeThrows() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.angularDisplacement(thetaInit = 1.0, aveAngularVel = 2.0, time = -3.0)
        }
        assertEquals("Time cannot be a negative value.", ex.message)
    }

    // ---- angular_vel_const_accel : ω(f) = ω₀ + αt ----

    @Test
    fun angularVelConstAccelSolvingForFinal() {
        // ω(f) = 2 + 3*4 = 14
        val result = calc.angularVelConstAccel(initAngularVel = 2.0, constAngularAccel = 3.0, time = 4.0)
        assertEquals(14.0, result, 0.005)
    }

    @Test
    fun angularVelConstAccelSolvingForTime() {
        // t = (ω(f) - ω₀)/α = (14 - 2)/3 = 4
        val result = calc.angularVelConstAccel(finalAngularVel = 14.0, initAngularVel = 2.0, constAngularAccel = 3.0)
        assertEquals(4.0, result, 0.005)
    }

    @Test
    fun angularVelConstAccelSolvingForAccelWithTimeZeroThrows() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.angularVelConstAccel(finalAngularVel = 14.0, initAngularVel = 2.0, time = 0.0)
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    // ---- angular_displacement_const_accel : θ(f) = θ₀ + ω₀t + ½αt² ----

    @Test
    fun angularDisplacementConstAccelSolvingForThetaFinal() {
        // θ(f) = 1 + 2*3 + 0.5*4*9 = 1 + 6 + 18 = 25
        val result = calc.angularDisplacementConstAccel(
            thetaInit = 1.0,
            initAngularVel = 2.0,
            time = 3.0,
            constAngularAccel = 4.0,
        )
        assertEquals(25.0, result, 0.005)
    }

    @Test
    fun angularDisplacementConstAccelSolvingForTime() {
        // 9 = 0 + 0*t + 0.5*2*t² => t² = 9 => t = 3 (non-negative root)
        val result = calc.angularDisplacementConstAccel(
            thetaFinal = 9.0,
            thetaInit = 0.0,
            initAngularVel = 0.0,
            constAngularAccel = 2.0,
        )
        assertEquals(3.0, result, 0.005)
    }

    @Test
    fun angularDisplacementConstAccelSolvingForInitVelWithTimeZeroThrows() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.angularDisplacementConstAccel(
                thetaFinal = 9.0,
                thetaInit = 0.0,
                time = 0.0,
                constAngularAccel = 2.0,
            )
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    // ---- change_angular_velocity : ω(f)² = ω₀² + 2αΔθ ----

    @Test
    fun changeAngularVelocitySolvingForFinal() {
        // ω(f) = √(3² + 2*2*4) = √(9 + 16) = √25 = 5
        val result = calc.changeAngularVelocity(initAngularVel = 3.0, constAngularAccel = 2.0, deltaTheta = 4.0)
        assertEquals(5.0, result, 0.005)
    }

    @Test
    fun changeAngularVelocitySolvingForDeltaTheta() {
        // Δθ = (ω(f)² - ω₀²)/(2α) = (25 - 9)/(2*2) = 4
        val result = calc.changeAngularVelocity(finalAngularVel = 5.0, initAngularVel = 3.0, constAngularAccel = 2.0)
        assertEquals(4.0, result, 0.005)
    }

    @Test
    fun changeAngularVelocityNegativeRadicandThrows() {
        // radicand = 1² + 2*(-2)*4 = 1 - 16 = -15 < 0
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.changeAngularVelocity(initAngularVel = 1.0, constAngularAccel = -2.0, deltaTheta = 4.0)
        }
        assertEquals("A negative radicand yields an imaginary number. Check your values.", ex.message)
    }

    @Test
    fun changeAngularVelocitySolvingForDeltaThetaWithAccelZeroThrows() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.changeAngularVelocity(finalAngularVel = 5.0, initAngularVel = 3.0, constAngularAccel = 0.0)
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    // ---- rotational_ke : K = ½Iω² ----

    @Test
    fun rotationalKESolvingForK() {
        // K = 0.5 * 2 * 3² = 9
        val result = calc.rotationalKE(momentInertia = 2.0, angularVel = 3.0)
        assertEquals(9.0, result, 0.005)
    }

    @Test
    fun rotationalKESolvingForOmega() {
        // ω = √(2K/I) = √(2*9/2) = 3
        val result = calc.rotationalKE(kineticEnergy = 9.0, momentInertia = 2.0)
        assertEquals(3.0, result, 0.005)
    }

    @Test
    fun rotationalKESolvingForInertiaWithOmegaZeroThrows() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.rotationalKE(kineticEnergy = 9.0, angularVel = 0.0)
        }
        assertEquals("Divison by zero is undefined.", ex.message)
    }

    @Test
    fun rotationalKENegativeInertiaThrows() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.rotationalKE(momentInertia = -2.0, angularVel = 3.0)
        }
        assertEquals("The moment of inertia cannot be a negative value.", ex.message)
    }

    // ---- magnitude_of_torque : |τ| = rFsinθ (θ in degrees) ----

    @Test
    fun magnitudeOfTorqueSolvingForTorque() {
        // |τ| = 2 * 10 * sin(30°) = 2 * 10 * 0.5 = 10
        val result = calc.magnitudeOfTorque(radius = 2.0, force = 10.0, theta = 30.0)
        assertEquals(10.0, result, 0.005)
    }

    @Test
    fun magnitudeOfTorqueSolvingForTheta() {
        // θ = asin(τ/(rF)) = asin(10/(2*10)) = asin(0.5) = 30°
        val result = calc.magnitudeOfTorque(torque = 10.0, radius = 2.0, force = 10.0)
        assertEquals(30.0, result, 0.005)
    }

    @Test
    fun magnitudeOfTorqueSolvingForThetaWithForceZeroThrows() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.magnitudeOfTorque(torque = 10.0, radius = 2.0, force = 0.0)
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    @Test
    fun magnitudeOfTorqueSolvingForThetaOutOfDomainThrows() {
        // argument = 30/(1*10) = 3 > 1 -> asin undefined
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.magnitudeOfTorque(torque = 30.0, radius = 1.0, force = 10.0)
        }
        assertEquals("math domain error", ex.message)
    }

    @Test
    fun magnitudeOfTorqueNonPositiveRadiusThrows() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.magnitudeOfTorque(force = 10.0, theta = 30.0, radius = 0.0)
        }
        assertEquals("The length of the center of axis to applied force cannot be less than or equal to zero.", ex.message)
    }

    // ---- quadratic_eq helper ----

    @Test
    fun quadraticEqRoots() {
        // x² - 5x + 6 = 0 -> roots 2 and 3
        val roots = calc.quadraticEq(1.0, -5.0, 6.0)
        assertEquals(true, roots != null)
        val sorted = listOf(roots!!.first, roots.second).sorted()
        assertEquals(2.0, sorted[0], 0.005)
        assertEquals(3.0, sorted[1], 0.005)
    }

    @Test
    fun quadraticEqNegativeDiscriminantReturnsNull() {
        // x² + 1 = 0 has no real roots
        assertEquals(null, calc.quadraticEq(1.0, 0.0, 1.0))
    }
}
