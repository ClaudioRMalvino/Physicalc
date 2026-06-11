package io.github.claudiormalvino.physicalc.physics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/*
 * Smoke tests for Chapter 12 (Static Equilibrium and Elasticity).
 *
 * The Python source has no test suite for this chapter, so these tests were written
 * from the formulas directly; each expected value is hand-derived in a comment.
 * Structure mirrors Chapter3Test.kt.
 */
class Chapter12Test {

    private val calc = Chapter12.Calculate

    // ---- young_modulus ----

    @Test
    fun youngModulusSolvingForY() {
        // Y = (F/A) × (L₀/ΔL) = (100/0.5) × (2/0.01) = 200 × 200 = 40000
        val result = calc.youngModulus(force = 100.0, crossSection = 0.5, initLength = 2.0, deltaLength = 0.01)
        assertEquals(40000.0, result, 0.005)
    }

    @Test
    fun youngModulusSolvingForForce() {
        // F = ΔL·A·Y / L₀ = (0.01 × 0.5 × 40000) / 2 = 200 / 2 = 100
        val result = calc.youngModulus(youngMod = 40000.0, crossSection = 0.5, initLength = 2.0, deltaLength = 0.01)
        assertEquals(100.0, result, 0.005)
    }

    @Test
    fun youngModulusSolvingForCrossSection() {
        // A = L₀·F / (Y·ΔL) = (2 × 100) / (40000 × 0.01) = 200 / 400 = 0.5
        val result = calc.youngModulus(youngMod = 40000.0, force = 100.0, initLength = 2.0, deltaLength = 0.01)
        assertEquals(0.5, result, 0.005)
    }

    @Test
    fun youngModulusSolvingForInitLength() {
        // L₀ = Y·ΔL·A / F = (40000 × 0.01 × 0.5) / 100 = 200 / 100 = 2
        val result = calc.youngModulus(youngMod = 40000.0, force = 100.0, crossSection = 0.5, deltaLength = 0.01)
        assertEquals(2.0, result, 0.005)
    }

    @Test
    fun youngModulusSolvingForDeltaLength() {
        // ΔL = L₀·F / (Y·A) = (2 × 100) / (40000 × 0.5) = 200 / 20000 = 0.01
        val result = calc.youngModulus(youngMod = 40000.0, force = 100.0, crossSection = 0.5, initLength = 2.0)
        assertEquals(0.01, result, 0.005)
    }

    @Test
    fun youngModulusRejectsNonPositiveInitLength() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.youngModulus(force = 100.0, crossSection = 0.5, initLength = 0.0, deltaLength = 0.01)
        }
        assertEquals("The initial length and cross section cannot be less than or equal to zero.", ex.message)
    }

    @Test
    fun youngModulusRejectsNegativeDeltaLength() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.youngModulus(force = 100.0, crossSection = 0.5, initLength = 2.0, deltaLength = -0.01)
        }
        assertEquals("The change in length cannot be less than zero.", ex.message)
    }

    @Test
    fun youngModulusCrossSectionDivisionByZero() {
        // Solving for A with Y = 0 divides by zero
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.youngModulus(youngMod = 0.0, force = 100.0, initLength = 2.0, deltaLength = 0.01)
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    @Test
    fun youngModulusDeltaLengthDivisionByZero() {
        // Solving for ΔL with Y = 0 divides by zero (Python message typo kept verbatim)
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.youngModulus(youngMod = 0.0, force = 100.0, crossSection = 0.5, initLength = 2.0)
        }
        assertEquals("Divison by zero is undefined.", ex.message)
    }

    @Test
    fun youngModulusInitLengthDivisionByZero() {
        // Solving for L₀ with F = 0 divides by zero
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.youngModulus(youngMod = 40000.0, force = 0.0, crossSection = 0.5, deltaLength = 0.01)
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    // ---- bulk_modulus ----

    @Test
    fun bulkModulusSolvingForB() {
        // B = −Δp·V₀ / ΔV = −(100 × 2) / (−0.01) = 20000
        val result = calc.bulkModulus(deltaPressure = 100.0, initVolume = 2.0, deltaVolume = -0.01)
        assertEquals(20000.0, result, 0.005)
    }

    @Test
    fun bulkModulusSolvingForDeltaPressure() {
        // Δp = −B·ΔV / V₀ = −(20000 × (−0.01)) / 2 = 200 / 2 = 100
        val result = calc.bulkModulus(bulkMod = 20000.0, initVolume = 2.0, deltaVolume = -0.01)
        assertEquals(100.0, result, 0.005)
    }

    @Test
    fun bulkModulusSolvingForInitVolume() {
        // V₀ = −B·ΔV / Δp = −(20000 × (−0.01)) / 100 = 200 / 100 = 2
        // (uses the corrected formula; the Python branch was buggy and returned −B)
        val result = calc.bulkModulus(bulkMod = 20000.0, deltaPressure = 100.0, deltaVolume = -0.01)
        assertEquals(2.0, result, 0.005)
    }

    @Test
    fun bulkModulusSolvingForDeltaVolume() {
        // ΔV = −Δp·V₀ / B = −(100 × 2) / 20000 = −0.01
        val result = calc.bulkModulus(bulkMod = 20000.0, deltaPressure = 100.0, initVolume = 2.0)
        assertEquals(-0.01, result, 0.005)
    }

    @Test
    fun bulkModulusRejectsNonPositiveInitVolume() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.bulkModulus(bulkMod = 20000.0, deltaPressure = 100.0, initVolume = 0.0)
        }
        assertEquals("The volume of the object must be greater than zero.", ex.message)
    }

    @Test
    fun bulkModulusDefaultDivisionByZero() {
        // Solving for B with ΔV = 0 divides by zero
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.bulkModulus(deltaPressure = 100.0, initVolume = 2.0, deltaVolume = 0.0)
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    @Test
    fun bulkModulusInitVolumeDivisionByZero() {
        // Solving for V₀ with Δp = 0 divides by zero
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.bulkModulus(bulkMod = 20000.0, deltaPressure = 0.0, deltaVolume = -0.01)
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    @Test
    fun bulkModulusDeltaVolumeDivisionByZero() {
        // Solving for ΔV with B = 0 divides by zero
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.bulkModulus(bulkMod = 0.0, deltaPressure = 100.0, initVolume = 2.0)
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    // ---- shear_modulus ----

    @Test
    fun shearModulusSolvingForS() {
        // S = (F/A) × (L₀/Δx) = (50/0.25) × (4/0.02) = 200 × 200 = 40000
        val result = calc.shearModulus(force = 50.0, crossSection = 0.25, initLength = 4.0, deltaLayers = 0.02)
        assertEquals(40000.0, result, 0.005)
    }

    @Test
    fun shearModulusSolvingForForce() {
        // F = Δx·A·S / L₀ = (0.02 × 0.25 × 40000) / 4 = 200 / 4 = 50
        val result = calc.shearModulus(shearMod = 40000.0, crossSection = 0.25, initLength = 4.0, deltaLayers = 0.02)
        assertEquals(50.0, result, 0.005)
    }

    @Test
    fun shearModulusSolvingForCrossSection() {
        // A = L₀·F / (S·Δx) = (4 × 50) / (40000 × 0.02) = 200 / 800 = 0.25
        val result = calc.shearModulus(shearMod = 40000.0, force = 50.0, initLength = 4.0, deltaLayers = 0.02)
        assertEquals(0.25, result, 0.005)
    }

    @Test
    fun shearModulusSolvingForInitLength() {
        // L₀ = S·Δx·A / F = (40000 × 0.02 × 0.25) / 50 = 200 / 50 = 4
        val result = calc.shearModulus(shearMod = 40000.0, force = 50.0, crossSection = 0.25, deltaLayers = 0.02)
        assertEquals(4.0, result, 0.005)
    }

    @Test
    fun shearModulusSolvingForDeltaLayers() {
        // Δx = L₀·F / (S·A) = (4 × 50) / (40000 × 0.25) = 200 / 10000 = 0.02
        val result = calc.shearModulus(shearMod = 40000.0, force = 50.0, crossSection = 0.25, initLength = 4.0)
        assertEquals(0.02, result, 0.005)
    }

    @Test
    fun shearModulusRejectsNonPositiveInitLength() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.shearModulus(force = 50.0, crossSection = 0.25, initLength = -1.0, deltaLayers = 0.02)
        }
        assertEquals("The initial length and cross section cannot be less than or equal to zero.", ex.message)
    }

    @Test
    fun shearModulusRejectsNegativeDeltaLayers() {
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.shearModulus(force = 50.0, crossSection = 0.25, initLength = 4.0, deltaLayers = -0.02)
        }
        assertEquals("The change in length cannot be less than zero.", ex.message)
    }

    @Test
    fun shearModulusCrossSectionDivisionByZero() {
        // Solving for A with Δx = 0 divides by zero
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.shearModulus(shearMod = 40000.0, force = 50.0, initLength = 4.0, deltaLayers = 0.0)
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }

    @Test
    fun shearModulusDeltaLayersDivisionByZero() {
        // Solving for Δx with S = 0 divides by zero (Python message typo kept verbatim)
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.shearModulus(shearMod = 0.0, force = 50.0, crossSection = 0.25, initLength = 4.0)
        }
        assertEquals("Divison by zero is undefined.", ex.message)
    }

    @Test
    fun shearModulusInitLengthDivisionByZero() {
        // Solving for L₀ with F = 0 divides by zero
        val ex = assertFailsWith<IllegalArgumentException> {
            calc.shearModulus(shearMod = 40000.0, force = 0.0, crossSection = 0.25, deltaLayers = 0.02)
        }
        assertEquals("Division by zero is undefined.", ex.message)
    }
}
