package io.github.claudiormalvino.physicalc.physics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

/*
 * Smoke tests for Chapter 13 (Gravitation).
 *
 * The Python source has NO tests for this chapter, so these were written from
 * scratch: for every calculation function there is at least one test of each
 * solve path with hand-derived expected values (the derivation is shown in a
 * comment), plus at least one error branch where one exists.
 *
 * G = 6.674e-11 and c = 2.998e8 throughout (the chapter's constants).
 *
 * The expected error messages reproduce the Python ValueError strings verbatim,
 * including the whitespace runs that Python's backslash line-continuations
 * embedded into them (hence the `" ".repeat(n)` below).
 */
class Chapter13Test {

    @Test
    fun tinyForcesSurviveRounding() {
        // Two 70 kg people 1 m apart: F = G·m1·m2/r² ≈ 3.27e-7 N.
        // Plain 4-decimal rounding used to collapse this to 0.0; significant-
        // figure rounding for small magnitudes must preserve it.
        val f = Chapter13.Calculate.lawOfGravitation(mass1 = 70.0, mass2 = 70.0, distance = 1.0)
        assertTrue(f > 3.2e-7 && f < 3.4e-7, "F = $f")
    }


    private val calc = Chapter13.Calculate

    // Verbatim Python error messages (see Chapter13.kt for the whitespace explanation).
    private val msgMassiveObjects21 =
        "We are operating with massive objects." + " ".repeat(21) +
            "Make sure all objects have a mass greater than zero."
    private val msgMassiveObjects17 =
        "We are operating with massive objects." + " ".repeat(17) +
            "Make sure all objects have a mass greater than zero."
    private val msgMassCancels =
        "Mass of orbiting body cancels out and" + " ".repeat(21) + "cannot be determined."

    // ---- law_of_gravitation: F = G·m₁·m₂/r² ----

    @Test
    fun gravitationSolvingForForce() {
        // F = 6.674e-11 · 1e10 · 2e10 / 100² = 6.674e-11 · 2e20 / 1e4 = 1,334,800 N
        val result = calc.lawOfGravitation(mass1 = 1e10, mass2 = 2e10, distance = 100.0)
        assertEquals(1334800.0, result, 1e-6)
    }

    @Test
    fun gravitationSolvingForMass1() {
        // m₁ = F·r²/(G·m₂) = 1,334,800 · 1e4 / (6.674e-11 · 2e10) = 1.3348e10 / 1.3348 = 1e10 kg
        val result = calc.lawOfGravitation(force12 = 1334800.0, mass2 = 2e10, distance = 100.0)
        assertEquals(1e10, result, 1.0)
    }

    @Test
    fun gravitationSolvingForMass2() {
        // m₂ = F·r²/(G·m₁) = 1,334,800 · 1e4 / (6.674e-11 · 1e10) = 1.3348e10 / 0.6674 = 2e10 kg
        val result = calc.lawOfGravitation(force12 = 1334800.0, mass1 = 1e10, distance = 100.0)
        assertEquals(2e10, result, 1.0)
    }

    @Test
    fun gravitationSolvingForDistance() {
        // r = √(G·m₁·m₂/F) = √(6.674e-11 · 2e20 / 1.3348e6) = √(1e4) = 100 m
        val result = calc.lawOfGravitation(force12 = 1334800.0, mass1 = 1e10, mass2 = 2e10)
        assertEquals(100.0, result, 1e-6)
    }

    @Test
    fun gravitationRejectsNonPositiveMass() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.lawOfGravitation(mass1 = -1.0, mass2 = 2e10, distance = 100.0)
        }
        assertEquals(msgMassiveObjects21, ex.message)
    }

    @Test
    fun gravitationRejectsNegativeDistance() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.lawOfGravitation(mass1 = 1e10, mass2 = 2e10, distance = -100.0)
        }
        assertEquals("Distance cannot be a negative value.", ex.message)
    }

    // ---- gravitational_acceleration: g = GM/r² ----

    @Test
    fun gravAccelSolvingForG() {
        // g = GM/r² = 6.674e-11 · 1e24 / (1e6)² = 6.674e13 / 1e12 = 66.74 m/s²
        val result = calc.gravitationalAcceleration(massBody = 1e24, distance = 1e6)
        assertEquals(66.74, result, 1e-9)
    }

    @Test
    fun gravAccelSolvingForMass() {
        // M = g·r²/G = 66.74 · 1e12 / 6.674e-11 = 1e24 kg
        val result = calc.gravitationalAcceleration(g = 66.74, distance = 1e6)
        assertEquals(1e24, result, 1e12)
    }

    @Test
    fun gravAccelSolvingForDistance() {
        // r = √(GM/g) = √(6.674e13 / 66.74) = √(1e12) = 1e6 m
        val result = calc.gravitationalAcceleration(g = 66.74, massBody = 1e24)
        assertEquals(1e6, result, 1e-4)
    }

    @Test
    fun gravAccelRejectsNonPositiveDistance() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.gravitationalAcceleration(massBody = 1e24, distance = 0.0)
        }
        assertEquals(
            "Distance cannot be a value that is" + " ".repeat(21) + "less than or equal to zero.",
            ex.message,
        )
    }

    // ---- gravitational_potential: U = -GMm/r ----

    @Test
    fun gravPotentialSolvingForU() {
        // U = -GMm/r = -(6.674e-11 · 1e24 · 1000)/1e6 = -6.674e16/1e6 = -6.674e10 J
        val result = calc.gravitationalPotential(massBody = 1e24, massObject = 1000.0, distance = 1e6)
        assertEquals(-6.674e10, result, 1.0)
    }

    @Test
    fun gravPotentialSolvingForMassBody() {
        // M = -U·r/(G·m) = -(-6.674e10 · 1e6)/(6.674e-11 · 1000) = 6.674e16/6.674e-8 = 1e24 kg
        val result = calc.gravitationalPotential(potentialEnergy = -6.674e10, massObject = 1000.0, distance = 1e6)
        assertEquals(1e24, result, 1e12)
    }

    @Test
    fun gravPotentialSolvingForMassObject() {
        // m = -U·r/(G·M) = 6.674e16/(6.674e-11 · 1e24) = 6.674e16/6.674e13 = 1000 kg
        val result = calc.gravitationalPotential(potentialEnergy = -6.674e10, massBody = 1e24, distance = 1e6)
        assertEquals(1000.0, result, 1e-4)
    }

    @Test
    fun gravPotentialSolvingForDistance() {
        // r = -GMm/U = -(6.674e16)/(-6.674e10) = 1e6 m
        val result = calc.gravitationalPotential(potentialEnergy = -6.674e10, massBody = 1e24, massObject = 1000.0)
        assertEquals(1e6, result, 1e-2)
    }

    @Test
    fun gravPotentialRejectsPositiveEnergyMass() {
        // Positive U makes the solved mass negative, which must be rejected.
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.gravitationalPotential(potentialEnergy = 6.674e10, massObject = 1000.0, distance = 1e6)
        }
        assertEquals("Mass cannot be negative. Check your values.", ex.message)
    }

    @Test
    fun gravPotentialRejectsNonPositiveDistance() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.gravitationalPotential(massBody = 1e24, massObject = 1000.0, distance = 0.0)
        }
        assertEquals(
            "Distance between the two bodies must be" + " ".repeat(17) + "greater than zero.",
            ex.message,
        )
    }

    // ---- conservation_of_grav_energy: ½mv₁² - GMm/r₁ = ½mv₂² - GMm/r₂ ----

    @Test
    fun conservationSolvingForMassBody() {
        // M = ½(v₁² - v₂²)/(G(1/r₁ - 1/r₂))
        //   = 0.5·(14000² - 10000²)/(6.674e-11·(1/1e6 - 1/2e6))
        //   = 4.8e7/(6.674e-11 · 5e-7) = 4.8e7/3.337e-17 ≈ 1.438418e24 kg
        val result = calc.conservationOfGravEnergy(
            massObject = 100.0, velocity1 = 14000.0, velocity2 = 10000.0,
            distance1 = 1e6, distance2 = 2e6,
        )
        assertEquals(1.4384177404854664e24, result, 1e18)
    }

    @Test
    fun conservationSolvingForVelocity1() {
        // With r₁ = r₂ the potential terms cancel: v₁ = v₂ = 5000 m/s
        val result = calc.conservationOfGravEnergy(
            massObject = 100.0, massBody = 1e24, velocity2 = 5000.0,
            distance1 = 1e6, distance2 = 1e6,
        )
        assertEquals(5000.0, result, 1e-6)
    }

    @Test
    fun conservationSolvingForVelocity2() {
        // v₂ = √(v₁² - 2GM/r₁ + 2GM/r₂), GM = 6.674e13
        //    = √(20000² - 2·6.674e13/1e6 + 2·6.674e13/2e6)
        //    = √(4e8 - 1.3348e8 + 6.674e7) = √(3.3326e8) ≈ 18255.4102 m/s
        val result = calc.conservationOfGravEnergy(
            massObject = 100.0, massBody = 1e24, velocity1 = 20000.0,
            distance1 = 1e6, distance2 = 2e6,
        )
        assertEquals(18255.4102, result, 1e-3)
    }

    @Test
    fun conservationSolvingForDistance2() {
        // With v₁ = v₂ the kinetic terms cancel: r₂ = GM/(GM/r₁) = r₁ = 1e6 m
        val result = calc.conservationOfGravEnergy(
            massObject = 100.0, massBody = 1e24, velocity1 = 10000.0,
            velocity2 = 10000.0, distance1 = 1e6,
        )
        assertEquals(1e6, result, 1e-2)
    }

    @Test
    fun conservationSolvingForDistance1() {
        // With v₁ = v₂: r₁ = GM/(GM/r₂) = r₂ = 2e6 m
        val result = calc.conservationOfGravEnergy(
            massObject = 100.0, massBody = 1e24, velocity1 = 10000.0,
            velocity2 = 10000.0, distance2 = 2e6,
        )
        assertEquals(2e6, result, 1e-2)
    }

    @Test
    fun conservationMassObjectCannotBeSolved() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.conservationOfGravEnergy(
                massBody = 1e24, velocity1 = 10000.0, velocity2 = 10000.0,
                distance1 = 1e6, distance2 = 2e6,
            )
        }
        assertEquals(msgMassCancels, ex.message)
    }

    @Test
    fun conservationRejectsNonPositiveDistance() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.conservationOfGravEnergy(
                massObject = 100.0, massBody = 1e24, velocity1 = 10000.0,
                velocity2 = 10000.0, distance1 = -1.0, distance2 = 2e6,
            )
        }
        assertEquals("Distance cannot be a negative value.", ex.message)
    }

    @Test
    fun conservationRejectsZeroVelocityWhenSolvingDistance() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.conservationOfGravEnergy(
                massObject = 100.0, massBody = 1e24, velocity1 = 0.0,
                velocity2 = 10000.0, distance1 = 1e6,
            )
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    @Test
    fun conservationAllKnownsReturnsZero() {
        // The Python fallthrough returns 0.0 when nothing is left to solve for.
        val result = calc.conservationOfGravEnergy(
            massObject = 100.0, massBody = 1e24, velocity1 = 10000.0,
            velocity2 = 10000.0, distance1 = 1e6, distance2 = 1e6,
        )
        assertEquals(0.0, result, 0.0)
    }

    // ---- escape_velocity: v = √(2GM/R) ----

    @Test
    fun escapeVelocitySolvingForV() {
        // v = √(2·6.674e-11·1e24/1.3348e6) = √(1.3348e14/1.3348e6) = √(1e8) = 10000 m/s
        val result = calc.escapeVelocity(massBody = 1e24, radius = 1.3348e6)
        assertEquals(10000.0, result, 1e-6)
    }

    @Test
    fun escapeVelocitySolvingForMass() {
        // M = v²R/(2G) = 1e8 · 1.3348e6 / 1.3348e-10 = 1e24 kg
        val result = calc.escapeVelocity(escapeVel = 10000.0, radius = 1.3348e6)
        assertEquals(1e24, result, 1e12)
    }

    @Test
    fun escapeVelocitySolvingForRadius() {
        // R = 2GM/v² = 1.3348e14 / 1e8 = 1.3348e6 m
        val result = calc.escapeVelocity(escapeVel = 10000.0, massBody = 1e24)
        assertEquals(1.3348e6, result, 1e-2)
    }

    @Test
    fun escapeVelocityRejectsNonPositiveRadius() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.escapeVelocity(massBody = 1e24, radius = 0.0)
        }
        assertEquals("Radius of stellar body must be greater than zero.", ex.message)
    }

    @Test
    fun escapeVelocityRejectsZeroVelocityWhenSolvingRadius() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.escapeVelocity(escapeVel = 0.0, massBody = 1e24)
        }
        assertEquals("Divison by zero is undefined.", ex.message)
    }

    // ---- orbital_velocity: v = √(GM/r) ----

    @Test
    fun orbitalVelocitySolvingForV() {
        // v = √(GM/r) = √(6.674e13/6.674e5) = √(1e8) = 10000 m/s
        val result = calc.orbitalVelocity(massBody = 1e24, distance = 6.674e5)
        assertEquals(10000.0, result, 1e-6)
    }

    @Test
    fun orbitalVelocitySolvingForMass() {
        // M = v²r/G = 1e8 · 6.674e5 / 6.674e-11 = 1e24 kg
        val result = calc.orbitalVelocity(orbitalVel = 10000.0, distance = 6.674e5)
        assertEquals(1e24, result, 1e12)
    }

    @Test
    fun orbitalVelocitySolvingForDistance() {
        // r = GM/v² = 6.674e13 / 1e8 = 6.674e5 m
        val result = calc.orbitalVelocity(orbitalVel = 10000.0, massBody = 1e24)
        assertEquals(6.674e5, result, 1e-2)
    }

    @Test
    fun orbitalVelocityRejectsNonPositiveDistance() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.orbitalVelocity(massBody = 1e24, distance = -5.0)
        }
        assertEquals("Radius of stellar body must be greater than zero.", ex.message)
    }

    @Test
    fun orbitalVelocityRejectsZeroVelocityWhenSolvingDistance() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.orbitalVelocity(orbitalVel = 0.0, massBody = 1e24)
        }
        assertEquals("Divison by zero is undefined.", ex.message)
    }

    // ---- orbital_period: T = 2π√(r³/GM) ----

    @Test
    fun orbitalPeriodSolvingForT() {
        // GM = 6.674e-11 · 5.972e24 = 3.985713e14; r³ = (6.371e6)³ = 2.58597e20
        // T = 2π√(2.58597e20/3.985713e14) = 2π√(648815.7…) ≈ 5061.0226 s
        val result = calc.orbitalPeriod(distance = 6.371e6, massBody = 5.972e24)
        assertEquals(5061.0226, result, 1e-3)
    }

    @Test
    fun orbitalPeriodSolvingForDistance() {
        // r = ∛((T/2π)²·GM) with T = 5061.022584149725 recovers r = 6.371e6 m
        val result = calc.orbitalPeriod(period = 5061.022584149725, massBody = 5.972e24)
        assertEquals(6.371e6, result, 1.0)
    }

    @Test
    fun orbitalPeriodSolvingForMass() {
        // M = (2π/T)²·r³/G with T = 5061.022584149725, r = 6.371e6 recovers M = 5.972e24 kg
        val result = calc.orbitalPeriod(period = 5061.022584149725, distance = 6.371e6)
        assertEquals(5.972e24, result, 1e17)
    }

    @Test
    fun orbitalPeriodRejectsZeroPeriodWhenSolvingMass() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.orbitalPeriod(period = 0.0, distance = 6.371e6)
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    // ---- orbital_equation: α/r = 1 + e·cosθ (θ in degrees) ----

    @Test
    fun orbitalEquationSolvingForSemiLatusRectum() {
        // α = r(1 + e·cos60°) = 2·(1 + 0.5·0.5) = 2.5
        val result = calc.orbitalEquation(eccentricity = 0.5, distance = 2.0, theta = 60.0)
        assertEquals(2.5, result, 1e-9)
    }

    @Test
    fun orbitalEquationSolvingForEccentricity() {
        // e = (α/r - 1)/cos60° = (2.5/2 - 1)/0.5 = 0.25/0.5 = 0.5
        val result = calc.orbitalEquation(semiLatusRectum = 2.5, distance = 2.0, theta = 60.0)
        assertEquals(0.5, result, 1e-9)
    }

    @Test
    fun orbitalEquationSolvingForDistance() {
        // r = α/(1 + e·cos60°) = 2.5/1.25 = 2
        val result = calc.orbitalEquation(semiLatusRectum = 2.5, eccentricity = 0.5, theta = 60.0)
        assertEquals(2.0, result, 1e-9)
    }

    @Test
    fun orbitalEquationSolvingForTheta() {
        // cosθ = (α/r - 1)/e = 0.25/0.5 = 0.5 → θ = 60°
        val result = calc.orbitalEquation(semiLatusRectum = 2.5, eccentricity = 0.5, distance = 2.0)
        assertEquals(60.0, result, 1e-4)
    }

    @Test
    fun orbitalEquationRejectsNonPositiveDistance() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.orbitalEquation(eccentricity = 0.5, distance = 0.0, theta = 60.0)
        }
        assertEquals("Distance cannot be less than or equal to zero.", ex.message)
    }

    @Test
    fun orbitalEquationRejectsZeroEccentricityWhenSolvingTheta() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.orbitalEquation(semiLatusRectum = 2.5, eccentricity = 0.0, distance = 2.0)
        }
        assertEquals("Divison by zero is undefined.", ex.message)
    }

    @Test
    fun orbitalEquationRejectsZeroDenominatorWhenSolvingDistance() {
        // e = 1, θ = 180° → 1 + 1·cos180° = 0
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.orbitalEquation(semiLatusRectum = 2.5, eccentricity = 1.0, theta = 180.0)
        }
        assertEquals("Divison by zero is undefined.", ex.message)
    }

    // ---- keplers_third_law: T² = (4π²/GM)a³ ----

    @Test
    fun keplerSolvingForPeriod() {
        // T = √(4π²a³/(GM)) with a = 1.496e11, M = 1.989e30 (Earth-Sun)
        //   = √(4π² · 3.34827e33 / 1.327459e20) ≈ 3.15549e7 s (≈ one year)
        val result = calc.keplersThirdLaw(semiMajorAxis = 1.496e11, massBody = 1.989e30)
        assertEquals(31554896.9288, result, 1e-2)
    }

    @Test
    fun keplerSolvingForSemiMajorAxis() {
        // a = ∛(T²·GM/4π²) with T = 31554896.928761967 recovers a = 1.496e11 m
        val result = calc.keplersThirdLaw(period = 31554896.928761967, massBody = 1.989e30)
        assertEquals(1.496e11, result, 1e5)
    }

    @Test
    fun keplerSolvingForMass() {
        // M = 4π²a³/(G·T²) with T = 31554896.928761967, a = 1.496e11 recovers M = 1.989e30 kg
        val result = calc.keplersThirdLaw(period = 31554896.928761967, semiMajorAxis = 1.496e11)
        assertEquals(1.989e30, result, 1e23)
    }

    @Test
    fun keplerRejectsZeroPeriodWhenSolvingMass() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.keplersThirdLaw(period = 0.0, semiMajorAxis = 1.496e11)
        }
        assertEquals("Divison by zero is undefined.", ex.message)
    }

    // ---- schwarzschild_radius: R(S) = 2GM/c² ----

    @Test
    fun schwarzschildSolvingForRadius() {
        // R(S) = 2GM/c² = 2 · 6.674e-11 · 1.989e30 / (2.998e8)²
        //      = 2.654919e20 / 8.98800e16 ≈ 2953.8451 m (the Sun)
        val result = calc.schwarzschildRadius(massBody = 1.989e30)
        assertEquals(2953.8451, result, 1e-3)
    }

    @Test
    fun schwarzschildSolvingForMass() {
        // M = R(S)·c²/(2G) = 2953.845147376436 · 8.98800e16 / 1.3348e-10 = 1.989e30 kg
        val result = calc.schwarzschildRadius(schwarzRadius = 2953.845147376436)
        assertEquals(1.989e30, result, 1e23)
    }

    @Test
    fun schwarzschildRejectsNonPositiveMass() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.schwarzschildRadius(massBody = 0.0)
        }
        assertEquals(msgMassiveObjects17, ex.message)
    }
}
