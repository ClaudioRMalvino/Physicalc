package io.github.claudiormalvino.physicalc.physics

import kotlin.math.round
import kotlin.math.sqrt

/**
 * Chapter 3: motion along a straight line (one-dimensional kinematics).
 */
class Chapter3 : PhysicsChapter(
    title = "Ch.3 - Motion Along a Straight Line",
    description = "Study of motion along one dimension.",
) {

    override val equations: List<Equation> = listOf(
        Equation(
            name = "Displacement",
            formula = "Δx = x - x_0",
            variables = mapOf(
                "x" to "Final position (m)",
                "x₀" to "Initial position (m)",
            ),
        ),
        Equation(
            name = "Total Displacement",
            formula = "Δx = ∑Δx_i",
            variables = mapOf("Δxᵢ" to "All steps taken"),
        ),
        Equation(
            name = "Average velocity (constant acceleration)",
            formula = "v = Δx/Δt = (x - x_i)/(t - t_i)",
            variables = mapOf(
                "Δx" to "Displacement in direction (m)",
                "Δt" to "Elapsed time (s)",
            ),
        ),
        Equation(
            name = "Instantaneous velocity",
            formula = "v(t) = dx(t)/dt",
        ),
        Equation(
            name = "Average speed",
            formula = "s = d/Δt",
            variables = mapOf(
                "d" to "Total distance traveled (m)",
                "Δt" to "Elapsed time (s)",
            ),
        ),
        Equation(
            name = "Instantaneous speed",
            formula = "|v(t)|",
        ),
        Equation(
            name = "Average acceleration",
            formula = "a = Δv/Δt",
            variables = mapOf(
                "Δv" to "Change in velocity (m/s)",
                "Δt" to "Elapsed time (s)",
            ),
        ),
        Equation(
            name = "Instantaneous acceleration",
            formula = "a(t) = dv(t)/dt",
        ),
        Equation(
            name = "Position from avg. velocity",
            formula = "x(t) = x_0 + vt",
            variables = mapOf(
                "x₀" to "Initial position (m)",
                "v" to "Average velocity (m/s)",
                "t" to "time (s)",
            ),
        ),
        Equation(
            name = "Velocity from acceleration",
            formula = "v(t) = v_0 + at",
            variables = mapOf(
                "v₀" to "Initial velocity (m/s)",
                "a" to "Acceleration (m/s²)",
            ),
        ),
        Equation(
            name = "Position from velocity and acceleration",
            formula = "x(t) = x_0 + v_0t + ½at^2",
            variables = mapOf(
                "x₀" to "Initial position (m)",
                "v₀" to "Initial velocity (m/s)",
                "t" to "Time (s)",
                "a" to "Acceleration (m/s²)",
                "x" to "Final Position (m)",
            ),
            calculation = { v ->
                Calculate.positionFromVelAndAccel(
                    x0 = v["x₀"],
                    v0 = v["v₀"],
                    t = v["t"],
                    accel = v["a"],
                    xf = v["x"],
                )
            },
        ),
        Equation(
            name = "Velocity from distance",
            formula = "v^2 = v_0^2 + 2a(x - x_0)",
            variables = mapOf(
                "x" to "Final position (m)",
                "x₀" to "Initial position (m)",
                "v₀" to "Initial velocity (m/s)",
                "a" to "Acceleration (m/s²)",
                "v" to "Final velocity (m/s)",
            ),
            calculation = { v ->
                Calculate.velocityFromDistance(
                    x0 = v["x₀"],
                    v0 = v["v₀"],
                    accel = v["a"],
                    xf = v["x"],
                    vf = v["v"],
                )
            },
        ),
        Equation(
            name = "Velocity of free fall",
            formula = "v = v_0 - gt",
            variables = mapOf(
                "v₀" to "Initial velocity (m/s)",
                "t" to "Time (s)",
            ),
        ),
        Equation(
            name = "Height of free fall",
            formula = "y(t) = y_0 + v_0t - ½gt^2",
            variables = mapOf(
                "y₀" to "Initial height (m)",
                "v₀" to "Initial velocity (m/s)",
                "t" to "Time (s)",
                "y" to "Final Position (s)",
            ),
            calculation = { v ->
                Calculate.heightOfFreeFall(
                    y0 = v["y₀"],
                    v0 = v["v₀"],
                    t = v["t"],
                    yf = v["y"],
                )
            },
        ),
        Equation(
            name = "Velocity of free fall from height",
            formula = "v^2 = v_0^2 - 2g(y - y_0)",
            variables = mapOf(
                "y" to "Final height (m)",
                "y₀" to "Initial height (m)",
                "v₀" to "Initial velocity (m/s)",
                "v" to "Final velocity (m/s)",
            ),
            calculation = { v ->
                Calculate.velFreeFallFromHeight(
                    y0 = v["y₀"],
                    v0 = v["v₀"],
                    yf = v["y"],
                    vf = v["v"],
                )
            },
        ),
        Equation(
            name = "Velocity from acceleration",
            formula = "v(t) = ∫a(t)dt + C_1",
        ),
        Equation(
            name = "Position from velocity",
            formula = "x(t) = ∫v(t)dt + C_2",
        ),
    )

    override val definitions: List<Definition> = listOf(
        Definition("acceleration due to gravity", "acceleration of an object as a result of gravity"),
        Definition("average acceleration", "the rate of change in velocity; the change in velocity over time"),
        Definition("average speed", "the total distance traveled divided by elapsed time"),
        Definition("average velocity", "the displacement divided by the time over which displacement occurs under constant acceleration"),
        Definition("displacement", "the change in position of an object"),
        Definition("distance traveled", "the total length of the path traveled between two positions"),
        Definition("elapsed time", "the difference between the ending time and the beginning time"),
        Definition("free fall", "the state of movement that results from gravitational force only"),
        Definition("instantaneous acceleration", "acceleration at a specific point in time"),
        Definition("instantaneous speed", "the absolute value of the instantaneous velocity"),
        Definition("instantaneous velocity", "the velocity at a specific instant or time point"),
        Definition("kinematics", "the description of motion through properties such as position, time, velocity, and acceleration"),
        Definition("position", "the location of an object at a particular time"),
        Definition("total displacement", "the sum of individual displacements over a given time period"),
        Definition("two-body pursuit problem", "a kinematics problem in which the unknowns are calculated by solving the kinematic equations simultaneously for two moving objects"),
    )

    /** Calculation functions for Chapter 3. */
    object Calculate {

        /** Gravitational acceleration on Earth [m/s²]. */
        const val G: Double = -9.82

        /** Delegates to roundResult; see PhysicsModels.kt. */
        private fun round4(value: Double): Double = roundResult(value)

        /**
         * Solves ax² + bx + c = 0. Returns the two roots, or null if the
         * discriminant is negative.
         */
        fun quadraticEq(a: Double, b: Double, c: Double): Pair<Double, Double>? {
            val discriminant: Double = (b * b) - 4 * a * c

            if (discriminant < 0) return null
            if (b == 0.0 && c == 0.0) return Pair(0.0, 0.0)

            // Choose the formula based on the sign of b (avoids catastrophic cancellation).
            return if (b >= 0) {
                val x1 = (-b - sqrt(discriminant)) / (2 * a)
                val x2 = (2 * c) / (-b - sqrt(discriminant))
                Pair(x1, x2)
            } else {
                val x1 = (2 * c) / (-b + sqrt(discriminant))
                val x2 = (-b + sqrt(discriminant)) / (2 * a)
                Pair(x1, x2)
            }
        }

        /** x(t) = x₀ + v₀t + ½at². Pass null for the unknown. */
        fun positionFromVelAndAccel(
            x0: Double? = null,
            v0: Double? = null,
            t: Double? = null,
            accel: Double? = null,
            xf: Double? = null,
        ): Double {
            if (t != null && t < 0) throw IllegalArgumentException("Time cannot be a negative value")

            // Solve for x₀
            if (x0 == null) {
                return round4(xf!! - (v0!! * t!!) - (0.5 * accel!! * (t * t)))
            }

            // Solve for v₀
            if (v0 == null) {
                if (t == 0.0) throw IllegalArgumentException("Division by zero is undefined")
                return round4((xf!! - x0 - (0.5 * accel!! * (t!! * t))) / t)
            }

            // Solve for t
            if (t == null) {
                if (accel == 0.0 && v0 == 0.0) {
                    throw IllegalArgumentException("v₀ and a cannot both be equal to zero")
                }
                val c: Double = x0 - xf!!
                val b: Double = v0
                val a: Double = 0.5 * accel!!
                val roots = quadraticEq(a, b, c)
                    ?: throw IllegalArgumentException("No real solution for time")

                if (roots.first < 0) return round4(roots.second)
                if (roots.second < 0) return round4(roots.first)
                return round4(minOf(roots.first, roots.second))
            }

            // Solve for a
            if (accel == null) {
                if (t == 0.0) throw IllegalArgumentException("Divison by zero is undefined.")
                return round4((xf!! - x0 - (v0 * t)) * (2.0 / (t * t)))
            }

            // Solve for x
            return round4(x0 + (v0 * t) + (0.5 * accel * (t * t)))
        }

        /** v² = v₀² + 2a(x - x₀). Pass null for the unknown. */
        fun velocityFromDistance(
            x0: Double? = null,
            v0: Double? = null,
            accel: Double? = null,
            xf: Double? = null,
            vf: Double? = null,
        ): Double {
            // Solve for x₀
            if (x0 == null) {
                if (accel == 0.0) throw IllegalArgumentException("acceleration cannot be equal to zero")
                return round4(-((((vf!! * vf) - (v0!! * v0)) / (2 * accel!!)) - xf!!))
            }

            // Solve for v₀
            if (v0 == null) {
                val discriminant = (vf!! * vf) - (2 * accel!! * (xf!! - x0))
                if (discriminant < 0) throw IllegalArgumentException("The discriminant cannot be negative")
                return round4(sqrt(discriminant))
            }

            // Solve for a
            if (accel == null) {
                if (xf == 0.0 && x0 == 0.0) throw IllegalArgumentException("x_f and x_0 cannot both be equal to zero")
                return round4(((vf!! * vf) - (v0 * v0)) / (2 * (xf!! - x0)))
            }

            // Solve for x
            if (xf == null) {
                if (accel == 0.0) throw IllegalArgumentException("acceleration cannot be equal to zero")
                return round4((((vf!! * vf) - (v0 * v0)) / (2 * accel)) - x0)
            }

            // Solve for v
            val discriminant = (v0 * v0) + 2 * accel * (xf - x0)
            if (discriminant < 0) throw IllegalArgumentException("The discriminant cannot be negative")
            return round4(sqrt(discriminant))
        }

        /** y(t) = y₀ + v₀t + ½gt² (free fall). Pass null for the unknown. */
        fun heightOfFreeFall(
            y0: Double? = null,
            v0: Double? = null,
            t: Double? = null,
            yf: Double? = null,
        ): Double {
            if (t != null && t < 0) throw IllegalArgumentException("Time cannot be a negative value")

            // Solve for y₀
            if (y0 == null) {
                return round4(yf!! - (v0!! * t!!) - (0.5 * G * (t * t)))
            }

            // Solve for v₀
            if (v0 == null) {
                if (t == 0.0) throw IllegalArgumentException("Cannot solve for v_0 when t=0")
                return round4((yf!! - y0 - (0.5 * G * (t!! * t))) / t)
            }

            // Solve for t
            if (t == null) {
                val c = y0 - yf!!
                val b = v0
                val a = 0.5 * G
                val roots = quadraticEq(a, b, c)
                    ?: throw IllegalArgumentException("No real solution for time")

                // Keep only non-negative roots, then take the smallest (earliest time).
                val validRoots = listOf(roots.first, roots.second).filter { it >= 0 }
                if (validRoots.isEmpty()) throw IllegalArgumentException("No positive time solution")
                return round4(validRoots.min())
            }

            // Solve for y
            return round4(y0 + (v0 * t) + (0.5 * G * (t * t)))
        }

        /** v² = v₀² + 2g(y - y₀) (free fall). Pass null for the unknown. */
        fun velFreeFallFromHeight(
            y0: Double? = null,
            v0: Double? = null,
            yf: Double? = null,
            vf: Double? = null,
        ): Double {
            // Solve for y₀
            if (y0 == null) {
                return round4(-((((vf!! * vf) - (v0!! * v0)) / (2 * G)) - yf!!))
            }

            // Solve for v₀
            if (v0 == null) {
                val discriminant = (vf!! * vf) - (2 * G * (yf!! - y0))
                if (discriminant < 0) throw IllegalArgumentException("The discriminant cannot be negative")
                return round4(sqrt(discriminant))
            }

            // Solve for y
            if (yf == null) {
                return round4((((vf!! * vf) - (v0 * v0)) / (2 * G)) - y0)
            }

            // Solve for v
            val discriminant = (v0 * v0) + 2 * G * (yf - y0)
            if (discriminant < 0) throw IllegalArgumentException("The discriminant cannot be negative")
            return round4(sqrt(discriminant))
        }
    }
}
