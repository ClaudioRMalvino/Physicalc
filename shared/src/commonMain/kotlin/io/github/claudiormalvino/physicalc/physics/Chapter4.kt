package io.github.claudiormalvino.physicalc.physics

import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * Chapter on two and three dimensional motion — the Kotlin twin of your Python `chapter4.py`.
 */
class Chapter4 : PhysicsChapter(
    title = "Ch.4 - Motion in Two and Three Dimensions",
    description = "Study of motion in two and three dimensions.",
) {

    override val equations: List<Equation> = listOf(
        Equation(
            name = "Position vector",
            formula = "r(t) = x(t)\\hat{i} + y(t)\\hat{j} + z(t)\\hat{k}",
        ),
        Equation(
            name = "Displacement vector",
            formula = "Δr = r(t_2) − r(t_1)",
        ),
        Equation(
            name = "Velocity vector",
            formula = "v(t) = lim(Δt→0) ((r(t + Δt) − r(t)) / Δt) = dr/dt",
            variables = mapOf("Δt" to "Elapsed time (s)"),
        ),
        Equation(
            name = "Velocity in terms of components",
            formula = "v(t) = v_x(t)\\hat{i} + v_y(t)\\hat{j} + v_z(t)\\hat{k}",
        ),
        Equation(
            name = "Velocity components",
            formula = "v_x = dx/dt, v_y = dy/dt, v_z = dz/dt",
        ),
        Equation(
            name = "Average velocity",
            formula = "v_{avg} = (r(t_2) − r(t_1)) / (t_2 − t_1)",
        ),
        Equation(
            name = "Instantaneous acceleration",
            formula = "a(t) = lim(Δt→0) ((v(t + Δt) − v(t)) / Δt) = dv/dt",
        ),
        Equation(
            name = "Acceleration components",
            formula = "a(t) = (d^2x/dt^2)\\hat{i} + (d^2y/dt^2)\\hat{j} + (d^2z/dt^2)\\hat{k}",
        ),
        Equation(
            name = "Time of Flight",
            formula = "T_{tot} = 2v_0sinθ / g",
            variables = mapOf(
                "v₀" to "Initial velocity (m/s)",
                "θ" to "Launch angle (degrees)",
            ),
            calculation = { v ->
                Calculate.timeOfFlight(
                    v0 = v["v₀"],
                    theta = v["θ"],
                )
            },
        ),
        Equation(
            name = "Trajectory",
            formula = "y = (tanθ)x − (g / (2(v_0cosθ)^2))x^2",
            variables = mapOf(
                "θ" to "Launch angle (degrees)",
                "v₀" to "Initial velocity (m/s)",
                "x" to "Position along he x-axis (m)",
            ),
            calculation = { v ->
                Calculate.trajectory(
                    theta = v["θ"],
                    v0 = v["v₀"],
                    x = v["x"],
                )
            },
        ),
        Equation(
            name = "Range",
            formula = "R = (v_0^2sin2θ) / g",
            variables = mapOf(
                "θ" to "Launch angle (degrees)",
                "v₀" to "Initial velocity (m/2)",
            ),
            calculation = { v ->
                Calculate.projectileRange(
                    theta = v["θ"],
                    v0 = v["v₀"],
                )
            },
        ),
        Equation(
            name = "Centripetal acceleration",
            formula = "a_c = v^2 / r",
            variables = mapOf(
                "v" to "Velocity (m/s)",
                "r" to "Radius (m)",
            ),
            calculation = { v ->
                Calculate.centripetalAccel(
                    velocity = v["v"],
                    radius = v["r"],
                )
            },
        ),
        Equation(
            name = "Position vector (uniform cirular motion)",
            formula = "r(t) = A cos ωt \\hat{i} + A sin ωt \\hat{j}",
            variables = mapOf(
                "A" to "Amplitude (m)",
                "ω" to "Angular frequency (rads/s)",
                "t" to "time (s)",
            ),
        ),
        Equation(
            name = "Velocity vector (uniform cirular motion)",
            formula = "v(t) = dr(t)/dt = −Aω sin ωt \\hat{i} + Aω cos ωt \\hat{j}",
            variables = mapOf(
                "A" to "Amplitude (m)",
                "ω" to "Angular frequency (rads/s)",
                "t" to "time (s)",
            ),
        ),
        Equation(
            name = "Acceleration vector (uniform circular motion)",
            formula = "a(t) = dv(t)/dt = −Aω^2 cos ωt \\hat{i} − Aω^2 sin ωt \\hat{j}",
            variables = mapOf(
                "A" to "Amplitude (m)",
                "ω" to "Angular frequency (rads/s)",
                "t" to "time (s)",
            ),
        ),
        Equation(
            name = "Tangential acceleration",
            formula = "a_T = d|v|/dt",
        ),
        Equation(
            name = "Total acceleration",
            formula = "a(t) = a_c + a_T",
            variables = mapOf(
                "a_c" to "Centripetal acceleration (m/s²)",
                "a_T" to "Tangential acceleration (m/s²)",
            ),
        ),
        Equation(
            name = "Position vector in frame",
            formula = "r_{PS} = r_{PS'} + r_{S'S}",
            variables = mapOf(
                "r_{PS'}" to "Position vector in frame S'",
                "r_{S'S}" to "Position vector from the origin of S to the origin of S'",
            ),
        ),
        Equation(
            name = "Relative velocity equation (two reference frames)",
            formula = "v_{PS} = v_{PS'} + v_{S'S}",
            variables = mapOf(
                "v_{PS'}" to "Velocity vector in frame S'",
                "v_{S'S}" to "Velocity vector between frames S and S'",
            ),
        ),
        Equation(
            name = "Relative velocity equation (more than two reference frames)",
            formula = "v_{PC} = v_{PA} + v_{AB} + v_{BC}",
            variables = mapOf(
                "v_{PA}" to "Relative velocity between points P and A",
                "v_{AB}" to "Relative velocity between points A and B",
                "v_{BC}" to "Relative velocity between points B and C",
            ),
        ),
        Equation(
            name = "Relative acceleration equation",
            formula = "a_{PS} = a_{PS'} + a_{S'S}",
            variables = mapOf(
                "a_{PS'}" to "Acceleration vector in frame S'",
                "a_{S'S}" to "Acceleration vector between frames S and S'",
            ),
        ),
    )

    override val definitions: List<Definition> = listOf(
        Definition("acceleration vector", "Instantaneous acceleration found by taking the derivative of the velocity function with respect to time in unit vector notation."),
        Definition("angular frequency", "ω, rate of change of an angle with which an object that is moving on a circular path."),
        Definition("centripetal acceleration", "Component of acceleration of an object moving in a circle that is directed radially inward toward the center of the circle."),
        Definition("displacement vector", "Vector from the initial position to a final position on a trajectory of a particle."),
        Definition("position vector", "Vector from the origin of a chosen coordinate system to the position of a particle in two- or three-dimensional space."),
        Definition("projectile motion", "Motion of an object subject only to the acceleration of gravity."),
        Definition("range", "Maximum horizontal distance a projectile travels."),
        Definition("reference frame", "Coordinate system in which the position, velocity, and acceleration of an object at rest or moving is measured."),
        Definition("relative velocity", "Velocity of an object as observed from a particular reference frame, or the velocity of one reference frame with respect to another reference frame."),
        Definition("tangential acceleration", "Magnitude of which is the time rate of change of speed. Its direction is tangent to the circle."),
        Definition("time of flight", "Elapsed time a projectile is in the air."),
        Definition("total acceleration", "Vector sum of centripetal and tangential accelerations."),
        Definition("trajectory", "Path of a projectile through the air."),
        Definition("velocity vector", "Vector that gives the instantaneous speed and direction of a particle; tangent to the trajectory."),
    )

    /**
     * Holds the calculation functions for Chapter 4 — the twin of the nested
     * Python `class Calculate`.
     */
    object Calculate {

        /** Gravitational acceleration on Earth [m/s²]. */
        const val G: Double = 9.82

        /** Rounds to 4 decimal places, matching Python's `round(x, 4)`. */
        private fun round4(value: Double): Double = roundResult(value)

        /**
         * T = 2v₀sinθ / g. Pass every value except the one you want solved for;
         * leave that one as `null`. Theta is always required — this equation cannot
         * be inverted for theta without complex numbers.
         */
        fun timeOfFlight(
            v0: Double? = null,
            theta: Double? = null,
            t: Double? = null,
        ): Double {
            // Theta is mandatory; convert degrees to radians
            if (theta == null) {
                throw IllegalArgumentException(
                    "Cannot solve for theta with this equation. Yields complex numbers. \n Please input a value for theta."
                )
            }
            val thetaRadians: Double = theta * (PI / 180)

            if (t != null && t < 0) throw IllegalArgumentException("Time cannot be a negative value")

            // Solve for v₀ (initial velocity)
            if (v0 == null) {
                return round4((t!! * G) / (2.0 * sin(thetaRadians)))
            }

            // Solve for T (time of flight) — the default case
            return round4((2 * v0 * sin(thetaRadians)) / G)
        }

        /**
         * y = (tanθ)x − (g / (2(v₀cosθ)²))x². Pass every value except the unknown;
         * leave that one `null`. Theta is always required.
         */
        fun trajectory(
            theta: Double? = null,
            v0: Double? = null,
            x: Double? = null,
            y: Double? = null,
        ): Double {
            // Theta is mandatory; convert degrees to radians
            if (theta == null) {
                throw IllegalArgumentException(
                    "Cannot solve for theta with this equation. Please input a value for theta."
                )
            }
            val thetaRadian: Double = theta * (PI / 180)

            // Solve for v₀ (initial velocity)
            if (v0 == null) {
                if (y == 0.0) throw IllegalArgumentException("Division by zero is undefined")

                val radicand: Double = (G / 2) * (((-(x!! * x)) / y!!) + (x / tan(thetaRadian)))

                if (radicand < 0) {
                    throw IllegalArgumentException("Radicand cannot be negative. Outputs imaginary number.")
                }

                return round4(sqrt(radicand) / cos(thetaRadian))
            }

            // Solving for x is not supported by this equation
            if (x == null) {
                throw IllegalArgumentException(
                    "Cannot solve for x with this equation. Consider calculating the range."
                )
            }

            if (v0 == 0.0 || theta == 90.0 || theta == 270.0) {
                throw IllegalArgumentException("Division by zero is undefined")
            }

            // Solve for y (vertical position) — the default case
            val vCos: Double = v0 * cos(thetaRadian)
            return round4(tan(thetaRadian) * x - ((G / (2 * vCos * vCos)) * (x * x)))
        }

        /**
         * R = (v₀²sin2θ) / g. Pass every value except the unknown; leave that one `null`.
         */
        fun projectileRange(
            rTotal: Double? = null,
            v0: Double? = null,
            theta: Double? = null,
        ): Double {
            // Solve for v₀ (initial velocity)
            if (v0 == null) {
                if (theta == 0.0) throw IllegalArgumentException("Division by zero is undefined.")

                val thetaRadian: Double = theta!! * (PI / 180.0)
                val radicand: Double = (rTotal!! * G) / sin(2 * thetaRadian)

                if (radicand < 0) {
                    throw IllegalArgumentException("Radicand cannot be negative. Outputs imaginary number.")
                }
                return round4(sqrt(radicand))
            }

            // Solve for θ (launch angle)
            if (theta == null) {
                if (v0 == 0.0) throw IllegalArgumentException("Division by zero is undefined.")

                val argument: Double = (rTotal!! * G) / (v0 * v0)

                if (argument > 1) {
                    throw IllegalArgumentException("No real solution exists. Range too large for given velocity.")
                }
                if (argument < 0) {
                    throw IllegalArgumentException("Range cannot be negative.")
                }

                return round4((asin(argument) / 2.0) * (180 / PI))
            }

            // Solve for R (range) — the default case
            val thetaRadian: Double = theta * (PI / 180.0)
            return round4(((v0 * v0) * sin(2 * thetaRadian)) / G)
        }

        /**
         * a_c = v² / r. Pass every value except the unknown; leave that one `null`.
         */
        fun centripetalAccel(
            accel: Double? = null,
            velocity: Double? = null,
            radius: Double? = null,
        ): Double {
            if (radius != null && radius <= 0) {
                throw IllegalArgumentException("Radius cannot be less than or equal to zero.")
            }

            // Solve for v (velocity)
            if (velocity == null) {
                val radicand: Double = accel!! * radius!!

                if (radicand < 0) {
                    throw IllegalArgumentException("Radicand cannot be negative. Yields an imaginary number.")
                }

                return round4(sqrt(radicand))
            }

            // Solve for r (radius)
            if (radius == null) {
                if (accel == 0.0) throw IllegalArgumentException("Division by zero is undefined.")

                return round4((velocity * velocity) / accel!!)
            }

            // Solve for a_c (centripetal acceleration) — the default case
            return round4((velocity * velocity) / radius)
        }
    }
}
