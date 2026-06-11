package io.github.claudiormalvino.physicalc.physics

import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Chapter on Angular Momentum — the Kotlin twin of your Python `chapter11.py`.
 */
class Chapter11 : PhysicsChapter(
    title = "Ch.11 - Angular Momentum",
    description = "Angular momentum, its conservation, and rolling motion.",
) {

    override val equations: List<Equation> = listOf(
        Equation(
            name = "Velocity of center of mass of rolling body",
            formula = "v_{CM} = Rω",
            variables = mapOf(
                "v_{CM}" to "Velocity of the center of mass (m/s)",
                "R" to "Radius of the rolling body (m)",
                "ω" to "Angular velocity about its axis (rads/s)",
            ),
        ),
        Equation(
            name = "Acceleration of center of mass of rolling body",
            formula = "a_{CM} = Rα",
            variables = mapOf(
                "a_{CM}" to "Acceleration of the center of mass (m/s²)",
                "R" to "Radius of the rolling body (m)",
                "α" to "Angular acceleration about its axis (rads/s²)",
            ),
        ),
        Equation(
            name = "Displacement of center of mass of rolling object",
            formula = "d_{CM} = Rθ",
            variables = mapOf(
                "d_{CM}" to "Displacement of cetner of mass (m)",
                "R" to "Radius of the rolling body (m)",
                "θ" to "Angular position of the center of mass (rads)",
            ),
        ),
        Equation(
            name = "Acceleration of an object rolling without slipping",
            formula = "a_{CM} = (mgsinθ)/(m+(I_{CM}/r^2))",
            variables = mapOf(
                "a_{CM}" to "Acceleration of the object (m/s²)",
                "m" to "Mass of the object (kg)",
                "I_{CM}" to "Moment of inertia for the center of mass (kg⋅m²)",
                "r" to "radius of the object (m)",
                "θ" to "The angle between the normal force and gravitational force (rads)",
            ),
            calculation = { v ->
                Calculate.accelWithoutSlipping(
                    accel = v["a_{CM}"],
                    mass = v["m"],
                    momentInertia = v["I_{CM}"],
                    radius = v["r"],
                    theta = v["θ"],
                )
            },
        ),
        Equation(
            name = "Angular momentum",
            formula = "\\vec{L} = \\vec{r} × \\vec{p}",
            variables = mapOf(
                "L" to "Angular momentum (J⋅s)",
                "r" to "Position vector of the object (m)",
                "p" to "Momentum vector of the object (kg⋅m/s)",
            ),
        ),
        Equation(
            name = "Derivative of angular momentum",
            formula = "dL/dt = ∑τ",
        ),
        Equation(
            name = "Angular momentum of a system of particles",
            formula = "L = l_1 + l_2 + ... + l_N",
        ),
        Equation(
            name = "Angular momentum of a rotating rigid body",
            formula = "L = Iω",
            variables = mapOf(
                "L" to "Angular momentum (J⋅s)",
                "I" to "Moment of inertia (kg⋅m²)",
                "ω" to "Angular velocity (m/s)",
            ),
            calculation = { v ->
                Calculate.angMomentumRigidBody(
                    angularMomentum = v["L"],
                    momentInertia = v["I"],
                    angularVel = v["ω"],
                )
            },
        ),
        Equation(
            name = "Conservation of angular momentum",
            formula = "dL/dt = 0",
        ),
        Equation(
            name = "Conservation of angular momentum",
            // Python wrote "I(N)" here — an obvious typo for the Nth particle's angular
            // momentum l_N (matching the equation three entries above).
            formula = "L = l_1 + l_2 + ... + l_N = constant",
        ),
        Equation(
            name = "Processional angular velocity",
            formula = "ω_p = (rMg)/(Iω)",
            variables = mapOf(
                "ωₚ" to "Processional angular velocity (m/s)",
                "r" to "Distance from center of mass and the pivot point (m)",
                "M" to "Mass of the rotating body (kg)",
                "I" to "Moment of inertia (kg⋅m²)",
                "ω" to "Angular velocity (rads/s)",
            ),
            calculation = { v ->
                Calculate.processionalAngVel(
                    processionalAngVel = v["ωₚ"],
                    radius = v["r"],
                    mass = v["M"],
                    momentInertia = v["I"],
                    angularVel = v["ω"],
                )
            },
        ),
    )

    override val definitions: List<Definition> = listOf(
        Definition(
            "angular momentum",
            "rotational analog of linear momentum, found by taking the product of moment of inertia and angular velocity",
        ),
        Definition(
            "law of conservation of angular momentum",
            "angular momentum is conserved, that is, the initial angular momentum is equal to the final angular momentum when no external torque is applied to the system",
        ),
        Definition(
            "precession",
            "circular motion of the pole of the axis of a spinning object around another axis due to a torque",
        ),
        Definition(
            "rolling motion",
            "combination of rotational and translational motion with or without slipping",
        ),
    )

    /**
     * Holds the calculation functions for Chapter 11 — the twin of the nested
     * Python `class Calculate`.
     */
    object Calculate {

        /** Gravitational acceleration on Earth [m/s²]. (`const` = compile-time constant.) */
        const val G: Double = 9.82

        /**
         * The runs of spaces inside these two messages replicate the Python source exactly:
         * the original strings were written with a backslash line-continuation, so the
         * indentation of the second source line became part of the message itself.
         */
        private val MASS_ERROR: String =
            "We are operating with massive objects." +
                " ".repeat(21) +
                "Make sure all objects have a mass greater than zero."

        private val RADICAND_ERROR: String =
            "Negative radicand yields an imaginary number." +
                " ".repeat(25) +
                "Check your values."

        /** Rounds to 4 decimal places, matching Python's `round(x, 4)`. */
        private fun round4(value: Double): Double = round(value * 10000.0) / 10000.0

        /**
         * a = (mg·sinθ) / (m + I/r²) — acceleration of a body rolling without slipping.
         *
         * Pass every value except the one you want solved for; leave that one `null`.
         * NOTE: `theta` is taken in DEGREES and converted to radians internally
         * (and returned in degrees when solving for θ), exactly as in the Python source.
         */
        fun accelWithoutSlipping(
            accel: Double? = null,
            mass: Double? = null,
            momentInertia: Double? = null,
            radius: Double? = null,
            theta: Double? = null,
        ): Double {
            val thetaRadians: Double? = theta?.times(PI / 180.0)

            if (mass != null && mass <= 0.0) throw IllegalArgumentException(MASS_ERROR)

            if (momentInertia != null && momentInertia < 0.0) {
                throw IllegalArgumentException("The moment of inertia cannot be a negative value.")
            }

            if (radius != null && radius <= 0.0) {
                throw IllegalArgumentException("Radius cannot be less than or equal to zero.")
            }

            // Solve for m (mass).
            // NOTE: the Python source computed m = ((a/(g·sinθ)) − 1)·I / r², which is
            // mathematically wrong (it even yields negative masses). The correct
            // inversion of a = mg·sinθ/(m + I/r²) is m = I / (r²·((g·sinθ/a) − 1)).
            if (mass == null) {
                if (accel == 0.0) throw IllegalArgumentException("Division by zero is undefined.")

                val denominator: Double =
                    (radius!! * radius) * (((G * sin(thetaRadians!!)) / accel!!) - 1.0)

                return round4(momentInertia!! / denominator)
            }

            // Solve for I (moment of inertia)
            if (momentInertia == null) {
                if (accel == 0.0) throw IllegalArgumentException("Division by zero is undefined.")

                val terms: Double = ((G * sin(thetaRadians!!)) / accel!!) - 1.0
                val coefficient: Double = mass * (radius!! * radius)

                return round4(terms * coefficient)
            }

            // Solve for r (radius)
            if (radius == null) {
                if (accel == 0.0) throw IllegalArgumentException("Division by zero is undefined.")

                val denominator: Double =
                    (((G * sin(thetaRadians!!)) / accel!!) - 1.0) * mass

                val radicand: Double = momentInertia / denominator

                if (radicand < 0) throw IllegalArgumentException(RADICAND_ERROR)

                return round4(sqrt(radicand))
            }

            // Solve for θ (angle, returned in degrees)
            if (theta == null) {
                val numerator: Double = accel!! * (mass + (momentInertia / (radius * radius)))
                val denominator: Double = mass * G
                val argument: Double = numerator / denominator

                // Python's math.asin raises ValueError("math domain error") outside [-1, 1];
                // Kotlin's asin would silently return NaN, so we throw to match.
                if (argument < -1.0 || argument > 1.0) {
                    throw IllegalArgumentException("math domain error")
                }

                return round4(asin(argument) * (180.0 / PI))
            }

            // Solve for a (acceleration) — the default case
            val numerator: Double = mass * G * sin(thetaRadians!!)
            val denominator: Double = mass + (momentInertia / (radius * radius))

            return round4(numerator / denominator)
        }

        /**
         * L = Iω — angular momentum of a rotating rigid body.
         * Pass every value except the unknown; leave that one `null`.
         */
        fun angMomentumRigidBody(
            angularMomentum: Double? = null,
            momentInertia: Double? = null,
            angularVel: Double? = null,
        ): Double {
            if (momentInertia != null && momentInertia < 0.0) {
                throw IllegalArgumentException("The moment of inertia cannot be a negative value.")
            }

            // Solve for I (moment of inertia)
            if (momentInertia == null) {
                if (angularVel == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                return round4(angularMomentum!! / angularVel!!)
            }

            // Solve for ω (angular velocity).
            // NOTE: the Python source tested `angular_vel == 0.0` here instead of
            // `angular_vel == None`, which made solving for ω crash with a TypeError.
            // The intended branch condition is "ω is the unknown".
            if (angularVel == null) {
                if (momentInertia == 0.0) throw IllegalArgumentException("Divison by zero is undefined.")
                return round4(angularMomentum!! / momentInertia)
            }

            // Solve for L (angular momentum) — the default case
            return round4(momentInertia * angularVel)
        }

        /**
         * ωₚ = (rMg)/(Iω) — precessional angular velocity of a spinning rigid body.
         * Pass every value except the unknown; leave that one `null`.
         */
        fun processionalAngVel(
            processionalAngVel: Double? = null,
            radius: Double? = null,
            mass: Double? = null,
            momentInertia: Double? = null,
            angularVel: Double? = null,
        ): Double {
            if (mass != null && mass <= 0.0) throw IllegalArgumentException(MASS_ERROR)

            if (momentInertia != null && momentInertia < 0.0) {
                throw IllegalArgumentException("The moment of inertia cannot be a negative value.")
            }

            if (radius != null && radius <= 0.0) {
                throw IllegalArgumentException("Radius cannot be less than or equal to zero.")
            }

            // Solve for r (distance from pivot point to center of mass)
            if (radius == null) {
                val numerator: Double = momentInertia!! * angularVel!!
                val denominator: Double = mass!! * G

                return round4(processionalAngVel!! * (numerator / denominator))
            }

            // Solve for M (mass)
            if (mass == null) {
                val numerator: Double = momentInertia!! * angularVel!!
                val denominator: Double = radius * G

                return round4(processionalAngVel!! * (numerator / denominator))
            }

            // Solve for I (moment of inertia)
            if (momentInertia == null) {
                if (angularVel == 0.0 || processionalAngVel == 0.0) {
                    throw IllegalArgumentException("Division by zero is undefined.")
                }

                val numerator: Double = radius * mass * G
                val denominator: Double = processionalAngVel!! * angularVel!!

                return round4(numerator / denominator)
            }

            // Solve for ω (angular velocity).
            // NOTE: the Python source guarded `if angular_vel == 0.0` INSIDE the branch
            // where angular_vel is None — an unreachable check. The meaningful guard is
            // that neither ωₚ nor I may be zero, matching the sibling branches.
            if (angularVel == null) {
                if (processionalAngVel == 0.0 || momentInertia == 0.0) {
                    throw IllegalArgumentException("Division by zero is undefined.")
                }

                val numerator: Double = radius * mass * G
                val denominator: Double = processionalAngVel!! * momentInertia

                return round4(numerator / denominator)
            }

            // Solve for ωₚ (precessional angular velocity) — the default case
            if (angularVel == 0.0 || momentInertia == 0.0) {
                throw IllegalArgumentException("Division by zero is undefined.")
            }

            val numerator: Double = radius * mass * G
            val denominator: Double = momentInertia * angularVel

            return round4(numerator / denominator)
        }
    }
}
