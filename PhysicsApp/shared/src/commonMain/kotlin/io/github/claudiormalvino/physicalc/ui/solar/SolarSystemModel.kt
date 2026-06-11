package io.github.claudiormalvino.physicalc.ui.solar

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/*
 * Approximate heliocentric planet positions, good to well under a degree for
 * 1800-2050 AD — far more precision than a home-screen visualization needs.
 *
 * Method (JPL "Approximate Positions of the Planets", E.M. Standish):
 *   1. Each planet has Keplerian orbital elements at epoch J2000 plus linear
 *      drift rates per Julian century.
 *   2. For a given date: mean longitude L and longitude of perihelion ϖ give
 *      the mean anomaly M = L - ϖ.
 *   3. Solve Kepler's equation  E - e·sin(E) = M  for the eccentric anomaly
 *      (Newton's method — the same equation as the app's gravitation chapter).
 *   4. Convert to the true anomaly ν, then the heliocentric ecliptic
 *      longitude λ = ν + ϖ. (Inclinations are a few degrees at most, so the
 *      2-D projection onto the ecliptic is visually exact.)
 */

/** Visual + orbital description of one planet. */
data class PlanetSpec(
    val name: String,
    val color: Long,          // ARGB for the dot
    val relativeSize: Float,  // visual dot size, fraction of the outer orbit radius
    // J2000 elements and per-Julian-century rates (degrees / AU):
    val a0: Double, val aDot: Double,       // semi-major axis (AU)
    val e0: Double, val eDot: Double,       // eccentricity
    val l0: Double, val lDot: Double,       // mean longitude (deg)
    val peri0: Double, val periDot: Double, // longitude of perihelion (deg)
)

/** A planet's state at a particular time. */
data class PlanetState(
    val spec: PlanetSpec,
    val longitudeDeg: Double, // heliocentric ecliptic longitude, 0..360
    val radiusAu: Double,     // current distance from the Sun
)

object SolarSystemModel {

    // JPL/Standish Table 1 (valid 1800 AD - 2050 AD).
    val planets: List<PlanetSpec> = listOf(
        PlanetSpec("Mercury", 0xFFB5A7A0, 0.022f, 0.38709927, 0.00000037, 0.20563593, 0.00001906, 252.25032350, 149472.67411175, 77.45779628, 0.16047689),
        PlanetSpec("Venus",   0xFFE6C89C, 0.032f, 0.72333566, 0.00000390, 0.00677672, -0.00004107, 181.97909950, 58517.81538729, 131.60246718, 0.00268329),
        PlanetSpec("Earth",   0xFF6BA8FF, 0.034f, 1.00000261, 0.00000562, 0.01671123, -0.00004392, 100.46457166, 35999.37244981, 102.93768193, 0.32327364),
        PlanetSpec("Mars",    0xFFE0795B, 0.027f, 1.52371034, 0.00001847, 0.09339410, 0.00007882, -4.55343205, 19140.30268499, -23.94362959, 0.44441088),
        PlanetSpec("Jupiter", 0xFFD9B38C, 0.062f, 5.20288700, -0.00011607, 0.04838624, -0.00013253, 34.39644051, 3034.74612775, 14.72847983, 0.21252668),
        PlanetSpec("Saturn",  0xFFE8D29B, 0.054f, 9.53667594, -0.00125060, 0.05386179, -0.00050991, 49.95424423, 1222.49362201, 92.59887831, -0.41897216),
        PlanetSpec("Uranus",  0xFF9BD4E4, 0.044f, 19.18916464, -0.00196176, 0.04725744, -0.00004397, 313.23810451, 428.48202785, 170.95427630, 0.40805281),
        PlanetSpec("Neptune", 0xFF5B7FE0, 0.043f, 30.06992276, 0.00026291, 0.00859048, 0.00005105, -55.12002969, 218.45945325, 44.96476227, -0.32241464),
    )

    /** Unix epoch milliseconds -> Julian day number. */
    fun julianDay(epochMillis: Long): Double = epochMillis / 86_400_000.0 + 2_440_587.5

    /** Positions of all planets at the given Julian day. */
    fun positions(jd: Double): List<PlanetState> {
        val t = (jd - 2_451_545.0) / 36_525.0 // Julian centuries since J2000
        return planets.map { p ->
            val a = p.a0 + p.aDot * t
            val e = p.e0 + p.eDot * t
            val l = p.l0 + p.lDot * t
            val peri = p.peri0 + p.periDot * t

            val mDeg = normalizeDeg(l - peri)
            val eAnom = solveKepler(mDeg.toRadians(), e)
            // True anomaly from eccentric anomaly.
            val nu = 2.0 * atan2(sqrt(1 + e) * sin(eAnom / 2), sqrt(1 - e) * cos(eAnom / 2))
            val lambda = normalizeDeg(nu.toDegrees() + peri)
            val r = a * (1 - e * cos(eAnom))

            PlanetState(p, lambda, r)
        }
    }

    /**
     * Newton's method on Kepler's equation E - e·sin(E) = M (radians).
     * Converges in a handful of iterations for planetary eccentricities.
     */
    fun solveKepler(mRad: Double, e: Double): Double {
        var eAnom = mRad + e * sin(mRad) // good starting guess
        repeat(8) {
            val delta = (eAnom - e * sin(eAnom) - mRad) / (1 - e * cos(eAnom))
            eAnom -= delta
        }
        return eAnom
    }

    /** Normalize an angle in degrees to [0, 360). */
    fun normalizeDeg(deg: Double): Double {
        var d = deg % 360.0
        if (d < 0) d += 360.0
        return d
    }

    private fun Double.toRadians(): Double = this * PI / 180.0
    private fun Double.toDegrees(): Double = this * 180.0 / PI

    // ---- Civil date from epoch millis (for the "positions for <date>" caption) ----
    // Howard Hinnant's days-from-civil inverse; avoids needing kotlinx-datetime.

    private val MONTHS = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    /** "11 Jun 2026" for the given epoch millis (UTC). */
    fun formatDate(epochMillis: Long): String {
        val days = (epochMillis / 86_400_000L).toInt()
        val z = days + 719_468
        val era = z / 146_097
        val doe = z - era * 146_097
        val yoe = (doe - doe / 1460 + doe / 36_524 - doe / 146_096) / 365
        var y = yoe + era * 400
        val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
        val mp = (5 * doy + 2) / 153
        val d = doy - (153 * mp + 2) / 5 + 1
        val m = mp + if (mp < 10) 3 else -9
        if (m <= 2) y += 1
        return "$d ${MONTHS[m - 1]} $y"
    }
}
