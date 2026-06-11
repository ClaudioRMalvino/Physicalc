package io.github.claudiormalvino.physicalc.ui.solar

import kotlin.math.abs
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/*
 * Tests for SolarSystemModel's Kepler-orbit planet positions.
 */
class SolarSystemModelTest {

    @Test
    fun julianDayOfUnixEpoch() {
        // 1970-01-01T00:00Z is JD 2440587.5 by definition.
        assertEquals(2_440_587.5, SolarSystemModel.julianDay(0L), 1e-9)
    }

    @Test
    fun keplerSolvesCircularOrbitExactly() {
        // e = 0  =>  E == M for any M.
        assertEquals(1.234, SolarSystemModel.solveKepler(1.234, 0.0), 1e-12)
    }

    @Test
    fun keplerSatisfiesItsOwnEquation() {
        val m = 2.5
        val e = 0.2056 // Mercury's eccentricity, the worst case among planets
        val big = SolarSystemModel.solveKepler(m, e)
        assertEquals(m, big - e * sin(big), 1e-10, "E - e·sinE must equal M")
    }

    @Test
    fun earthLongitudeMatchesEphemerisForJune2026() {
        // JD 2461202.5 = 2026-06-11 00:00 UTC: the Sun's geocentric longitude is ~80.4°,
        // so Earth's heliocentric longitude must be ~260.4°. Allow a generous ±4°.
        val states = SolarSystemModel.positions(2_461_202.5)
        val earth = states.first { it.spec.name == "Earth" }
        assertTrue(abs(earth.longitudeDeg - 260.4) < 4.0, "Earth at ${earth.longitudeDeg}° (expected ~260.4°)")
    }

    @Test
    fun radiiStayWithinEachPlanetsOrbitBounds() {
        // r must lie within [a(1-e), a(1+e)] — sanity for the Kepler pipeline.
        val states = SolarSystemModel.positions(2_461_202.5)
        for (s in states) {
            val a = s.spec.a0
            val e = s.spec.e0 + 0.01 // tiny slack for the secular drift terms
            assertTrue(s.radiusAu > a * (1 - e) && s.radiusAu < a * (1 + e), "${s.spec.name} r=${s.radiusAu}")
        }
    }

    @Test
    fun longitudesAreNormalized() {
        for (s in SolarSystemModel.positions(2_461_202.5)) {
            assertTrue(s.longitudeDeg >= 0.0 && s.longitudeDeg < 360.0, "${s.spec.name}: ${s.longitudeDeg}")
        }
    }

    @Test
    fun formatDateProducesCivilDate() {
        assertEquals("1 Jan 1970", SolarSystemModel.formatDate(0L))
        // 2026-06-11 (20615 days after the epoch)
        assertEquals("11 Jun 2026", SolarSystemModel.formatDate(20_615L * 86_400_000L))
    }
}
