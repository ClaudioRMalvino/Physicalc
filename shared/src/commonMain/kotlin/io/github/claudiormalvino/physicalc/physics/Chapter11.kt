package io.github.claudiormalvino.physicalc.physics

import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt

/** Chapter 11 — Angular momentum, its conservation, rolling motion, and precession. */
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
            formula = "a_{CM} = \\frac{mgsinθ}{m + \\frac{I_{CM}}{r^2}}",
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
            formula = "\\frac{dL}{dt} = ∑τ",
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
            formula = "\\frac{dL}{dt} = 0",
        ),
        Equation(
            name = "Conservation of angular momentum",
            // Corrected from upstream: "I(N)" was a typo for l_N.
            formula = "L = l_1 + l_2 + ... + l_N = constant",
        ),
        Equation(
            name = "Processional angular velocity",
            formula = "ω_p = \\frac{rMg}{Iω}",
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

    /** Calculation functions for Chapter 11. */
    object Calculate {

        /** Gravitational acceleration on Earth [m/s²]. */
        const val G: Double = 9.82

        // The " ".repeat runs reproduce indentation that backslash line-continuations embedded
        // in the original messages; tests assert the exact strings.
        private val MASS_ERROR: String =
            "We are operating with massive objects." +
                " ".repeat(21) +
                "Make sure all objects have a mass greater than zero."

        private val RADICAND_ERROR: String =
            "Negative radicand yields an imaginary number." +
                " ".repeat(25) +
                "Check your values."

        private fun round4(value: Double): Double = roundResult(value)

        /**
         * a = (mg·sinθ) / (m + I/r²), rolling without slipping. Pass null for the unknown.
         * θ is in degrees (converted internally).
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

            // Solve for m
            // Corrected from upstream: m = I / (r²·((g·sinθ/a) − 1)).
            if (mass == null) {
                if (accel == 0.0) throw IllegalArgumentException("Division by zero is undefined.")

                val denominator: Double =
                    (radius!! * radius) * (((G * sin(thetaRadians!!)) / accel!!) - 1.0)

                return round4(momentInertia!! / denominator)
            }

            // Solve for I
            if (momentInertia == null) {
                if (accel == 0.0) throw IllegalArgumentException("Division by zero is undefined.")

                val terms: Double = ((G * sin(thetaRadians!!)) / accel!!) - 1.0
                val coefficient: Double = mass * (radius!! * radius)

                return round4(terms * coefficient)
            }

            // Solve for r
            if (radius == null) {
                if (accel == 0.0) throw IllegalArgumentException("Division by zero is undefined.")

                val denominator: Double =
                    (((G * sin(thetaRadians!!)) / accel!!) - 1.0) * mass

                val radicand: Double = momentInertia / denominator

                if (radicand < 0) throw IllegalArgumentException(RADICAND_ERROR)

                return round4(sqrt(radicand))
            }

            // Solve for θ
            if (theta == null) {
                val numerator: Double = accel!! * (mass + (momentInertia / (radius * radius)))
                val denominator: Double = mass * G
                val argument: Double = numerator / denominator

                // asin would return NaN out of domain; throw instead. Message preserved
                // verbatim from the original implementation; tests assert it.
                if (argument < -1.0 || argument > 1.0) {
                    throw IllegalArgumentException("math domain error")
                }

                return round4(asin(argument) * (180.0 / PI))
            }

            // Solve for a
            val numerator: Double = mass * G * sin(thetaRadians!!)
            val denominator: Double = mass + (momentInertia / (radius * radius))

            return round4(numerator / denominator)
        }

        /** L = Iω, angular momentum of a rotating rigid body. Pass null for the unknown. */
        fun angMomentumRigidBody(
            angularMomentum: Double? = null,
            momentInertia: Double? = null,
            angularVel: Double? = null,
        ): Double {
            if (momentInertia != null && momentInertia < 0.0) {
                throw IllegalArgumentException("The moment of inertia cannot be a negative value.")
            }

            // Solve for I
            if (momentInertia == null) {
                if (angularVel == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                return round4(angularMomentum!! / angularVel!!)
            }

            // Solve for ω
            // Corrected from upstream: branch condition tested == 0.0 instead of null.
            if (angularVel == null) {
                if (momentInertia == 0.0) throw IllegalArgumentException("Divison by zero is undefined.")
                return round4(angularMomentum!! / momentInertia)
            }

            // Solve for L
            return round4(momentInertia * angularVel)
        }

        /** ωₚ = (rMg)/(Iω), precessional angular velocity. Pass null for the unknown. */
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

            // Solve for r
            if (radius == null) {
                val numerator: Double = momentInertia!! * angularVel!!
                val denominator: Double = mass!! * G

                return round4(processionalAngVel!! * (numerator / denominator))
            }

            // Solve for M
            if (mass == null) {
                val numerator: Double = momentInertia!! * angularVel!!
                val denominator: Double = radius * G

                return round4(processionalAngVel!! * (numerator / denominator))
            }

            // Solve for I
            if (momentInertia == null) {
                if (angularVel == 0.0 || processionalAngVel == 0.0) {
                    throw IllegalArgumentException("Division by zero is undefined.")
                }

                val numerator: Double = radius * mass * G
                val denominator: Double = processionalAngVel!! * angularVel!!

                return round4(numerator / denominator)
            }

            // Solve for ω
            // Corrected from upstream: guard ωₚ and I against zero, not the unknown ω.
            if (angularVel == null) {
                if (processionalAngVel == 0.0 || momentInertia == 0.0) {
                    throw IllegalArgumentException("Division by zero is undefined.")
                }

                val numerator: Double = radius * mass * G
                val denominator: Double = processionalAngVel!! * momentInertia

                return round4(numerator / denominator)
            }

            // Solve for ωₚ
            if (angularVel == 0.0 || momentInertia == 0.0) {
                throw IllegalArgumentException("Division by zero is undefined.")
            }

            val numerator: Double = radius * mass * G
            val denominator: Double = momentInertia * angularVel

            return round4(numerator / denominator)
        }
    }
}
