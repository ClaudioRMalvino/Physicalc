package io.github.claudiormalvino.physicalc.physics

import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/*
 * Smoke tests for Chapter 14 (Fluid Dynamics).
 *
 * The Python project has no tests for this chapter, so each calculation function gets:
 *  - at least one default-solve-path test (all knowns given, the natural output left null),
 *    with the expected value hand-derived from the formula in a comment, and
 *  - at least one error-branch test where the function defines one.
 *
 * Tolerance is 0.005 unless the arithmetic is exact.
 */
class Chapter14Test {

    private val calc = Chapter14.Calculate

    // ---- hydrostatic_pressure: p = p₀ + ρgh (g = 9.82) ----

    @Test
    fun hydrostaticSolvingForPressure() {
        // p = 101325 + 1000 * 9.82 * 10 = 101325 + 98200 = 199525.0
        val result = calc.hydrostaticPressure(pressureAtm = 101325.0, density = 1000.0, depth = 10.0)
        assertEquals(199525.0, result, 0.005)
    }

    @Test
    fun hydrostaticSolvingForDensity() {
        // ρ = (p - p₀)/(g·h) = (199525 - 101325)/(9.82 * 10) = 98200/98.2 = 1000.0
        val result = calc.hydrostaticPressure(pressure = 199525.0, pressureAtm = 101325.0, depth = 10.0)
        assertEquals(1000.0, result, 0.005)
    }

    @Test
    fun hydrostaticNegativePressureThrows() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.hydrostaticPressure(pressure = -5.0, pressureAtm = 101325.0, density = 1000.0)
        }
        assertEquals("Hydrostatic or atmospheric pressure cannot be a negative value.", ex.message)
    }

    @Test
    fun hydrostaticDensityDivisionByZeroThrows() {
        // Solving for ρ with h = 0 divides by zero.
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.hydrostaticPressure(pressure = 199525.0, pressureAtm = 101325.0, depth = 0.0)
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    // ---- pascals_principle: F₁/A₁ = F₂/A₂ ----

    @Test
    fun pascalsSolvingForForce2() {
        // F₂ = (F₁/A₁)·A₂ = (100/2) * 10 = 500.0
        val result = calc.pascalsPrinciple(force1 = 100.0, area1 = 2.0, area2 = 10.0)
        assertEquals(500.0, result, 0.005)
    }

    @Test
    fun pascalsSolvingForForce1() {
        // F₁ = (F₂/A₂)·A₁ = (500/10) * 2 = 100.0
        val result = calc.pascalsPrinciple(area1 = 2.0, force2 = 500.0, area2 = 10.0)
        assertEquals(100.0, result, 0.005)
    }

    @Test
    fun pascalsForce1DivisionByZeroThrows() {
        // Solving for F₁ with A₂ = 0 divides by zero.
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.pascalsPrinciple(area1 = 2.0, force2 = 500.0, area2 = 0.0)
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    // ---- continuity_const_density: A₁v₁ = A₂v₂ ----

    @Test
    fun continuitySolvingForVelocity2() {
        // v₂ = A₁v₁/A₂ = (2 * 3)/1.5 = 4.0
        val result = calc.continuityConstDensity(area1 = 2.0, velocity1 = 3.0, area2 = 1.5)
        assertEquals(4.0, result, 0.005)
    }

    @Test
    fun continuityNonPositiveAreaThrows() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.continuityConstDensity(area1 = -2.0, velocity1 = 3.0, area2 = 1.5)
        }
        assertEquals("Area cannot be less than or equal to zero.", ex.message)
    }

    @Test
    fun continuityArea1DivisionByZeroThrows() {
        // Solving for A₁ with v₁ = 0 divides by zero. (Message typo is verbatim from Python.)
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.continuityConstDensity(velocity1 = 0.0, area2 = 1.5, velocity2 = 4.0)
        }
        assertEquals("Divison by zero is undefined.", ex.message)
    }

    // ---- continuity_const_general: ρ₁A₁v₁ = ρ₂A₂v₂ ----

    @Test
    fun generalContinuitySolvingForVelocity2() {
        // v₂ = ρ₁A₁v₁/(ρ₂A₂) = (1000 * 2 * 3)/(500 * 4) = 6000/2000 = 3.0
        val result = calc.continuityConstGeneral(
            density1 = 1000.0, area1 = 2.0, velocity1 = 3.0, density2 = 500.0, area2 = 4.0,
        )
        assertEquals(3.0, result, 0.005)
    }

    @Test
    fun generalContinuitySolvingForDensity1() {
        // ρ₁ = ρ₂A₂v₂/(A₁v₁) = (500 * 4 * 3)/(2 * 3) = 6000/6 = 1000.0
        val result = calc.continuityConstGeneral(
            area1 = 2.0, velocity1 = 3.0, density2 = 500.0, area2 = 4.0, velocity2 = 3.0,
        )
        assertEquals(1000.0, result, 0.005)
    }

    @Test
    fun generalContinuityNonPositiveDensityThrows() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.continuityConstGeneral(
                density1 = 0.0, area1 = 2.0, velocity1 = 3.0, density2 = 500.0, area2 = 4.0,
            )
        }
        assertEquals("Density of a fluid cannot be less than or equal to zero.", ex.message)
    }

    @Test
    fun generalContinuityDensity1DivisionByZeroThrows() {
        // Solving for ρ₁ with v₁ = 0 divides by zero.
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.continuityConstGeneral(
                area1 = 2.0, velocity1 = 0.0, density2 = 500.0, area2 = 4.0, velocity2 = 3.0,
            )
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    // ---- bernoullis_equation: p₁ + ½ρv₁² + ρgy₁ = p₂ + ½ρv₂² + ρgy₂ ----

    @Test
    fun bernoulliSolvingForPressure2() {
        // With ρ=1000, p₁=200000, v₁=2, y₁=10, v₂=4, y₂=2 (g = 9.82):
        //   ½ρv₁² = 0.5*1000*4   = 2000
        //   ρgy₁  = 1000*9.82*10 = 98200
        //   ½ρv₂² = 0.5*1000*16  = 8000
        //   ρgy₂  = 1000*9.82*2  = 19640
        // p₂ = 200000 + 2000 + 98200 - 8000 - 19640 = 272560.0
        val result = calc.bernoullisEquation(
            density = 1000.0, pressure1 = 200000.0, velocity1 = 2.0, height1 = 10.0,
            velocity2 = 4.0, height2 = 2.0,
        )
        assertEquals(272560.0, result, 0.005)
    }

    @Test
    fun bernoulliSolvingForVelocity1() {
        // Same state as above, now solving for v₁ (expected 2.0):
        // radicand = p₂ - p₁ + ½ρv₂² + ρgy₂ - ρgy₁ = 72560 + 8000 + 19640 - 98200 = 2000
        // v₁ = √(2000 / (0.5*1000)) = √4 = 2.0
        val result = calc.bernoullisEquation(
            density = 1000.0, pressure1 = 200000.0, height1 = 10.0,
            pressure2 = 272560.0, velocity2 = 4.0, height2 = 2.0,
        )
        assertEquals(2.0, result, 0.005)
    }

    @Test
    fun bernoulliSolvingForDensity() {
        // ρ = (p₂ - p₁)/(½v₁² + gy₁ - ½v₂² - gy₂)
        //   = 72560 / (2 + 98.2 - 8 - 19.64) = 72560/72.56 = 1000.0
        val result = calc.bernoullisEquation(
            pressure1 = 200000.0, velocity1 = 2.0, height1 = 10.0,
            pressure2 = 272560.0, velocity2 = 4.0, height2 = 2.0,
        )
        assertEquals(1000.0, result, 0.005)
    }

    @Test
    fun bernoulliNonPositiveDensityThrows() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.bernoullisEquation(
                density = -1.0, pressure1 = 200000.0, velocity1 = 2.0, height1 = 10.0,
                velocity2 = 4.0, height2 = 2.0,
            )
        }
        assertEquals("Density of a fluid cannot be less than or equal to zero.", ex.message)
    }

    @Test
    fun bernoulliNegativeRadicandThrows() {
        // Solving for v₁ with p₁ > p₂, equal heights, v₂ = 0:
        // radicand = 200000 - 300000 + 0 + 0 - 0 = -100000 < 0
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.bernoullisEquation(
                density = 1000.0, pressure1 = 300000.0, height1 = 0.0,
                pressure2 = 200000.0, velocity2 = 0.0, height2 = 0.0,
            )
        }
        assertEquals("Negative radicand yields an imaginary number", ex.message)
    }

    // ---- viscocity: η = FL/(vA) ----

    @Test
    fun viscocitySolvingForViscocity() {
        // η = FL/(vA) = (10 * 2)/(4 * 5) = 20/20 = 1.0
        val result = calc.viscocity(force = 10.0, distance = 2.0, area = 5.0, velocity = 4.0)
        assertEquals(1.0, result, 0.005)
    }

    @Test
    fun viscocitySolvingForForce() {
        // F = ηvA/L = 1 * (4 * 5)/2 = 10.0
        val result = calc.viscocity(viscocity = 1.0, distance = 2.0, area = 5.0, velocity = 4.0)
        assertEquals(10.0, result, 0.005)
    }

    @Test
    fun viscocityDefaultDivisionByZeroThrows() {
        // Solving for η with v = 0 divides by zero.
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.viscocity(force = 10.0, distance = 2.0, area = 5.0, velocity = 0.0)
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    @Test
    fun viscocityNonPositiveDimensionsThrows() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.viscocity(force = 10.0, distance = -1.0, area = 5.0, velocity = 4.0)
        }
        assertEquals("Dimensions of length and area cannot be less than or equal to zero.", ex.message)
    }

    // ---- poiseuilles_law_resistance: R = 8ηl/(πr⁴) ----

    @Test
    fun poiseuilleResistanceSolvingForResistance() {
        // R = 8*2*3/(π*1⁴) = 48/π ≈ 15.27887
        val result = calc.poiseuillesLawResistance(viscocity = 2.0, length = 3.0, radius = 1.0)
        assertEquals(48.0 / PI, result, 0.005)
    }

    @Test
    fun poiseuilleResistanceSolvingForRadius() {
        // r = [8ηl/(πR)]^(1/4) with R = 48/π: radicand = 48/(π * 48/π) = 1 → r = 1.0
        val result = calc.poiseuillesLawResistance(resistance = 48.0 / PI, viscocity = 2.0, length = 3.0)
        assertEquals(1.0, result, 0.005)
    }

    @Test
    fun poiseuilleResistanceRadiusDivisionByZeroThrows() {
        // Solving for r with R = 0 divides by zero.
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.poiseuillesLawResistance(resistance = 0.0, viscocity = 2.0, length = 3.0)
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    @Test
    fun poiseuilleResistanceNonPositiveDimensionsThrows() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.poiseuillesLawResistance(viscocity = 2.0, length = 0.0, radius = 1.0)
        }
        assertEquals("Dimensions of length and radius cannot be less than or equal to zero.", ex.message)
    }

    // ---- poiseuilles_law: Q = (p₁ - p₂)πr⁴/(8ηl) ----

    @Test
    fun poiseuilleLawSolvingForFlow() {
        // Q = (1000 - 200)*π*1⁴/(8*2*5) = 800π/80 = 10π ≈ 31.41593
        val result = calc.poiseuillesLaw(
            viscocity = 2.0, length = 5.0, radius = 1.0, pressure1 = 1000.0, pressure2 = 200.0,
        )
        assertEquals(10.0 * PI, result, 0.005)
    }

    @Test
    fun poiseuilleLawSolvingForPressure1() {
        // p₁ = 8ηlQ/(πr⁴) + p₂ = (10π * 8 * 2 * 5)/(π * 1) + 200 = 800 + 200 = 1000.0
        val result = calc.poiseuillesLaw(
            flow = 10.0 * PI, viscocity = 2.0, length = 5.0, radius = 1.0, pressure2 = 200.0,
        )
        assertEquals(1000.0, result, 0.005)
    }

    @Test
    fun poiseuilleLawSolvingForPressure2() {
        // p₂ = p₁ - 8ηlQ/(πr⁴) = 1000 - 800 = 200.0
        val result = calc.poiseuillesLaw(
            flow = 10.0 * PI, viscocity = 2.0, length = 5.0, radius = 1.0, pressure1 = 1000.0,
        )
        assertEquals(200.0, result, 0.005)
    }

    @Test
    fun poiseuilleLawViscocityDivisionByZeroThrows() {
        // Solving for η with Q = 0 divides by zero.
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.poiseuillesLaw(
                flow = 0.0, length = 5.0, radius = 1.0, pressure1 = 1000.0, pressure2 = 200.0,
            )
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    @Test
    fun poiseuilleLawNonPositiveDimensionsThrows() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.poiseuillesLaw(
                viscocity = 2.0, length = 5.0, radius = -1.0, pressure1 = 1000.0, pressure2 = 200.0,
            )
        }
        assertEquals("Dimensions of length and radius cannot be less than or equal to zero.", ex.message)
    }
}
