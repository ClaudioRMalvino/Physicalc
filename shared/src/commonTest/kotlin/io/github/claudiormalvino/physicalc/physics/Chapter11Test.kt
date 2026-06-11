package io.github.claudiormalvino.physicalc.physics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/*
 * Smoke tests for the Chapter11 solvers (Angular Momentum); g = 9.82 m/s².
 * Error messages are asserted byte-for-byte, including line-continuation whitespace and typos.
 */
class Chapter11Test {

    private val calc = Chapter11.Calculate

    private val massError =
        "We are operating with massive objects." +
            " ".repeat(21) +
            "Make sure all objects have a mass greater than zero."

    private val radicandError =
        "Negative radicand yields an imaginary number." +
            " ".repeat(25) +
            "Check your values."

    // ---- accel_without_slipping: a = (m·g·sinθ) / (m + I/r²), θ in degrees ----

    @Test
    fun accelSolvingForAccel() {
        // m=2, I=1, r=1, θ=30° : a = 2·9.82·sin(30°)/(2 + 1/1²) = 9.82/3 = 3.27333… → 3.2733
        val result = calc.accelWithoutSlipping(mass = 2.0, momentInertia = 1.0, radius = 1.0, theta = 30.0)
        assertEquals(3.2733, result, 0.005)
    }

    @Test
    fun accelSolvingForMass() {
        // a = 2.455, θ=30° : g·sinθ/a = 4.91/2.455 = 2, so m = I/(r²·(2−1)) = 3/1 = 3
        val result = calc.accelWithoutSlipping(accel = 2.455, momentInertia = 3.0, radius = 1.0, theta = 30.0)
        assertEquals(3.0, result, 0.005)
    }

    @Test
    fun accelSolvingForMomentInertia() {
        // a = 2.455, θ=30° : I = m·r²·(g·sinθ/a − 1) = 2·1·(2−1) = 2
        val result = calc.accelWithoutSlipping(accel = 2.455, mass = 2.0, radius = 1.0, theta = 30.0)
        assertEquals(2.0, result, 0.005)
    }

    @Test
    fun accelSolvingForRadius() {
        // a = 2.455, θ=30° : r = √(I/(m·(g·sinθ/a − 1))) = √(2/(2·1)) = 1
        val result = calc.accelWithoutSlipping(accel = 2.455, mass = 2.0, momentInertia = 2.0, theta = 30.0)
        assertEquals(1.0, result, 0.005)
    }

    @Test
    fun accelSolvingForTheta() {
        // a = 2.455, m=2, I=2, r=1 : θ = asin(a·(m + I/r²)/(m·g)) = asin(0.5) = 30°
        val result = calc.accelWithoutSlipping(accel = 2.455, mass = 2.0, momentInertia = 2.0, radius = 1.0)
        assertEquals(30.0, result, 0.005)
    }

    @Test
    fun accelMassMustBePositive() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.accelWithoutSlipping(mass = 0.0, momentInertia = 1.0, radius = 1.0, theta = 30.0)
        }
        assertEquals(massError, ex.message)
    }

    @Test
    fun accelMomentInertiaCannotBeNegative() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.accelWithoutSlipping(mass = 2.0, momentInertia = -1.0, radius = 1.0, theta = 30.0)
        }
        assertEquals("The moment of inertia cannot be a negative value.", ex.message)
    }

    @Test
    fun accelRadiusMustBePositive() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.accelWithoutSlipping(mass = 2.0, momentInertia = 1.0, radius = 0.0, theta = 30.0)
        }
        assertEquals("Radius cannot be less than or equal to zero.", ex.message)
    }

    @Test
    fun accelSolvingForMomentInertiaWithZeroAccel() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.accelWithoutSlipping(accel = 0.0, mass = 2.0, radius = 1.0, theta = 30.0)
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    @Test
    fun accelSolvingForRadiusNegativeRadicand() {
        // a = 10 > g·sinθ = 4.91, so (g·sinθ/a − 1) < 0 and the radicand I/denominator < 0.
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.accelWithoutSlipping(accel = 10.0, mass = 2.0, momentInertia = 2.0, theta = 30.0)
        }
        assertEquals(radicandError, ex.message)
    }

    @Test
    fun accelSolvingForThetaOutOfDomain() {
        // arg = a·(m + I/r²)/(m·g) = 9.82·2/9.82 = 2 > 1 → asin is undefined.
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.accelWithoutSlipping(accel = 9.82, mass = 1.0, momentInertia = 1.0, radius = 1.0)
        }
        assertEquals("math domain error", ex.message)
    }

    // ---- ang_momentum_rigid_body: L = Iω ----

    @Test
    fun angMomentumSolvingForL() {
        // I=2, ω=3 : L = 2·3 = 6
        val result = calc.angMomentumRigidBody(momentInertia = 2.0, angularVel = 3.0)
        assertEquals(6.0, result, 0.005)
    }

    @Test
    fun angMomentumSolvingForMomentInertia() {
        // L=6, ω=3 : I = 6/3 = 2
        val result = calc.angMomentumRigidBody(angularMomentum = 6.0, angularVel = 3.0)
        assertEquals(2.0, result, 0.005)
    }

    @Test
    fun angMomentumSolvingForAngularVel() {
        // L=6, I=2 : ω = 6/2 = 3
        val result = calc.angMomentumRigidBody(angularMomentum = 6.0, momentInertia = 2.0)
        assertEquals(3.0, result, 0.005)
    }

    @Test
    fun angMomentumSolvingForMomentInertiaWithZeroAngularVel() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.angMomentumRigidBody(angularMomentum = 6.0, angularVel = 0.0)
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    @Test
    fun angMomentumSolvingForAngularVelWithZeroMomentInertia() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.angMomentumRigidBody(angularMomentum = 6.0, momentInertia = 0.0)
        }
        assertEquals("Divison by zero is undefined.", ex.message)
    }

    @Test
    fun angMomentumMomentInertiaCannotBeNegative() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.angMomentumRigidBody(angularMomentum = 6.0, momentInertia = -2.0)
        }
        assertEquals("The moment of inertia cannot be a negative value.", ex.message)
    }

    // ---- processional_ang_vel: ωₚ = (rMg)/(Iω) ----

    @Test
    fun precessionSolvingForOmegaP() {
        // r=0.5, M=2, I=1, ω=10 : ωₚ = 0.5·2·9.82/(1·10) = 9.82/10 = 0.982
        val result = calc.processionalAngVel(radius = 0.5, mass = 2.0, momentInertia = 1.0, angularVel = 10.0)
        assertEquals(0.982, result, 0.005)
    }

    @Test
    fun precessionSolvingForRadius() {
        // ωₚ=0.982, M=2, I=1, ω=10 : r = ωₚ·Iω/(Mg) = 0.982·10/19.64 = 0.5
        val result = calc.processionalAngVel(processionalAngVel = 0.982, mass = 2.0, momentInertia = 1.0, angularVel = 10.0)
        assertEquals(0.5, result, 0.005)
    }

    @Test
    fun precessionSolvingForMass() {
        // ωₚ=0.982, r=0.5, I=1, ω=10 : M = ωₚ·Iω/(rg) = 0.982·10/4.91 = 2
        val result = calc.processionalAngVel(processionalAngVel = 0.982, radius = 0.5, momentInertia = 1.0, angularVel = 10.0)
        assertEquals(2.0, result, 0.005)
    }

    @Test
    fun precessionSolvingForMomentInertia() {
        // ωₚ=0.982, r=0.5, M=2, ω=10 : I = rMg/(ωₚ·ω) = 9.82/9.82 = 1
        val result = calc.processionalAngVel(processionalAngVel = 0.982, radius = 0.5, mass = 2.0, angularVel = 10.0)
        assertEquals(1.0, result, 0.005)
    }

    @Test
    fun precessionSolvingForAngularVel() {
        // ωₚ=0.982, r=0.5, M=2, I=1 : ω = rMg/(ωₚ·I) = 9.82/0.982 = 10
        val result = calc.processionalAngVel(processionalAngVel = 0.982, radius = 0.5, mass = 2.0, momentInertia = 1.0)
        assertEquals(10.0, result, 0.005)
    }

    @Test
    fun precessionSolvingForOmegaPWithZeroAngularVel() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.processionalAngVel(radius = 0.5, mass = 2.0, momentInertia = 1.0, angularVel = 0.0)
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    @Test
    fun precessionSolvingForMomentInertiaWithZeroOmegaP() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.processionalAngVel(processionalAngVel = 0.0, radius = 0.5, mass = 2.0, angularVel = 10.0)
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    @Test
    fun precessionSolvingForAngularVelWithZeroOmegaP() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.processionalAngVel(processionalAngVel = 0.0, radius = 0.5, mass = 2.0, momentInertia = 1.0)
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    @Test
    fun precessionMassMustBePositive() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.processionalAngVel(radius = 0.5, mass = -1.0, momentInertia = 1.0, angularVel = 10.0)
        }
        assertEquals(massError, ex.message)
    }

    @Test
    fun precessionRadiusMustBePositive() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.processionalAngVel(radius = -1.0, mass = 2.0, momentInertia = 1.0, angularVel = 10.0)
        }
        assertEquals("Radius cannot be less than or equal to zero.", ex.message)
    }
}
