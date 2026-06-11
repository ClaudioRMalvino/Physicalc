package io.github.claudiormalvino.physicalc.physics

import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt

/** Chapter 10 — Fixed-axis rotation: rotational kinematics, moment of inertia, and torque. */
class Chapter10 : PhysicsChapter(
    title = "Ch.10 - Fixed-Axis Rotation",
    description = "Rotational kinematics, moment of inertia, and torque.",
) {

    override val equations: List<Equation> = listOf(
        Equation(
            name = "Angular position",
            formula = "θ = s/r",
            variables = mapOf(
                "θ" to "Angular position (rads)",
                "s" to "Arc length (m)",
                "r" to "Radius (m)",
            ),
            calculation = { v ->
                Calculate.angularPosition(
                    theta = v["θ"],
                    arcLength = v["s"],
                    radius = v["r"],
                )
            },
        ),
        Equation(
            name = "Angular velocity",
            formula = "ω = lim(Δt→0) Δθ/Δt = dθ/dt",
            variables = mapOf(
                "ω" to "Angular velocity (rads/s)",
                "dθ/dt" to "The change in angular position with respect to time.",
            ),
        ),
        Equation(
            name = "Tangential speed",
            formula = "v_t = rω",
            variables = mapOf(
                "v_t" to "Tangential speed (m/s)",
                "r" to "Radius (m)",
                "ω" to "Angular velocity (rads/s)",
            ),
            calculation = { v ->
                Calculate.tangentialSpeed(
                    tangSpeed = v["v_t"],
                    radius = v["r"],
                    angularVel = v["ω"],
                )
            },
        ),
        Equation(
            name = "Angular acceleration",
            formula = "α = lim(Δt→0) Δω/Δt = dω/dt = d^2θ/dt^2",
            variables = mapOf(
                "α" to "Angular acceleration (m/s²)",
                "d²θ/dt²" to "Second derivative of the angular position with respect to time.",
            ),
        ),
        Equation(
            name = "Tangential acceleration",
            formula = "a_t = rα",
            variables = mapOf(
                "a_t" to "Tangential acceleration (m/s²)",
                "r" to "Radius (m)",
                "α" to "Angular acceleration (rads/s²)",
            ),
        ),
        Equation(
            name = "Average angular velocity",
            formula = "ω_{avg} = (ω_0 + ω_f)/2",
            variables = mapOf(
                "ω_{avg}" to "Average angular velocity (rad/s)",
                "ω₀" to "Initial angular velocity (rad/s)",
                "ω_f" to "Final angular velocity (rad/s)",
            ),
            calculation = { v ->
                Calculate.averageAngularVel(
                    aveAngularVel = v["ω_{avg}"],
                    initAngularVel = v["ω₀"],
                    finalAngularVel = v["ω_f"],
                )
            },
        ),
        Equation(
            name = "Angular displacement",
            formula = "θ_f = θ_0 + ω_{avg}t",
            variables = mapOf(
                "θ_f" to "Final angular position (rads)",
                "θ₀" to "Initial angular position (rads)",
                "ω_{avg}" to "Average angular velocity (rads/s)",
                "t" to "time (s)",
            ),
            calculation = { v ->
                Calculate.angularDisplacement(
                    thetaFinal = v["θ_f"],
                    thetaInit = v["θ₀"],
                    aveAngularVel = v["ω_{avg}"],
                    time = v["t"],
                )
            },
        ),
        Equation(
            name = "Angular velocity from constant angular acceleration",
            formula = "ω_f = ω_0 + αt",
            variables = mapOf(
                "ω_f" to "Final angular velocity (rads/s)",
                "ω₀" to "Initial angular velocity (rads/s)",
                "α" to "Constant angular velocity (rads/s²)",
                "t" to "time (s)",
            ),
            calculation = { v ->
                Calculate.angularVelConstAccel(
                    finalAngularVel = v["ω_f"],
                    initAngularVel = v["ω₀"],
                    constAngularAccel = v["α"],
                    time = v["t"],
                )
            },
        ),
        Equation(
            name = "Angular displacement from angular velocity and constant angular acceleration",
            formula = "θ_f = θ_0 + ω_0t + ½αt^2",
            variables = mapOf(
                "θ_f" to "Final angular position (rads)",
                "θ₀" to "Initial angular position (rads)",
                "ω₀" to "Initial angular velocity (rads/s)",
                "t" to "time (s)",
                "α" to "Angular acceleration (rads/s²)",
            ),
            calculation = { v ->
                Calculate.angularDisplacementConstAccel(
                    thetaFinal = v["θ_f"],
                    thetaInit = v["θ₀"],
                    initAngularVel = v["ω₀"],
                    time = v["t"],
                    constAngularAccel = v["α"],
                )
            },
        ),
        Equation(
            name = "Change in angular velocity",
            formula = "ω_f^2 = ω_0^2 + 2α(Δθ)",
            variables = mapOf(
                "ω_f" to "Final angular velocity (rads/s)",
                "ω₀" to "Initial angular velocity (rads/s)",
                "α" to "Angular acceleration (rads/s²)",
                "Δθ" to "Change in angular position (rads)",
            ),
            calculation = { v ->
                Calculate.changeAngularVelocity(
                    finalAngularVel = v["ω_f"],
                    initAngularVel = v["ω₀"],
                    constAngularAccel = v["α"],
                    deltaTheta = v["Δθ"],
                )
            },
        ),
        Equation(
            name = "Total Acceleration",
            formula = "a = a_c + a_t",
            variables = mapOf(
                "a" to "Total acceleration (m/s²)",
                "a_c" to "Centripetal acceleration (m/s²)",
                "a_t" to "Tangential acceleration (m/s²)",
            ),
        ),
        Equation(
            name = "Rotational kinetic energy",
            formula = "K = ½(∑_j m_jr_j^2)ω^2",
            variables = mapOf(
                "mⱼ" to "Mass of the jth object (kg)",
                "rⱼ" to "Radial distance of the jth object (m)",
                "ω" to "Angular velocity (rads/s)",
            ),
        ),
        Equation(
            name = "Moment of inertia",
            formula = "I = ∑_j m_jr_j^2",
            variables = mapOf(
                "I" to "Moment of inertia (kg⋅m²)",
                "mⱼ" to "Mass of the jth object (kg)",
                "rⱼ" to "Radial distance of the jth object (m)",
            ),
        ),
        Equation(
            name = "Rotational kinetic energy in terms of the moment of inertia of a rigid body",
            formula = "K = ½Iω^2",
            variables = mapOf(
                "K" to "Kinetic energy (J)",
                "I" to "Moment of inertia of a rigid body (kg⋅m²)",
                "ω" to "Angular velocity (rads/s)",
            ),
            calculation = { v ->
                Calculate.rotationalKE(
                    kineticEnergy = v["K"],
                    momentInertia = v["I"],
                    angularVel = v["ω"],
                )
            },
        ),
        Equation(
            name = "Moment of inertia of a continuous object",
            // Corrected from upstream: integrand is r², not r.
            formula = "I = ∫r^2dm",
            variables = mapOf(
                "I" to "Moment of inertia (kg⋅m²)",
                "r" to "Distance to the axis of rotation (m)",
                "dm" to "Infinitesimal change in mass (kg)",
            ),
        ),
        Equation(
            name = "Parallel-axis theorem",
            formula = "I = I_{CM} + md^2",
            variables = mapOf(
                "I" to "Moment of interia of an object about any axis parallel to the axis through the center of mass (kg⋅m²)",
                "I_{CM}" to "Moment of inertia through the center of mass (kg⋅m²)",
                "m" to "Mass of the object (kg)",
                "d" to "Distance from an axis through the object's center of mass to a new axis (m)",
            ),
        ),
        Equation(
            name = "Moment of inertia of a compound object",
            formula = "I_{tot} = ∑_i I_i",
            variables = mapOf(
                "I_{tot}" to "Total moment of inertia (kg⋅m²)",
                "∑ᵢ Iᵢ" to "The sum of all moments of inertia within the system (kg⋅m²)",
            ),
        ),
        Equation(
            name = "Torque vector",
            formula = "τ = r × F",
            variables = mapOf(
                "τ" to "Torque (N⋅m)",
                "r" to "Length of the lever arm to the axis of rotation (m)",
                "F" to "The force applied onto the lever arm (N)",
            ),
        ),
        Equation(
            name = "Magnitude of torque",
            formula = "|τ| = r⊥F = rFsinθ",
            variables = mapOf(
                "|τ|" to "Magnitude of the applied torque (N⋅m)",
                "r" to "The distance from where the force is being applied and the axis of rotation (m)",
                "F" to "The applied force (N)",
                "θ" to "The angle of the applied force relative to r (rads)",
            ),
            calculation = { v ->
                Calculate.magnitudeOfTorque(
                    torque = v["|τ|"],
                    radius = v["r"],
                    force = v["F"],
                    theta = v["θ"],
                )
            },
        ),
        Equation(
            name = "Total torque",
            formula = "τ_{net} = ∑_i|τ_i|",
            variables = mapOf(
                "τ_{net}" to "Total torque in a system (N⋅m)",
                "∑ᵢ|τᵢ|" to "The sum of all discrete torque in the system (N⋅m)",
            ),
        ),
        Equation(
            name = "Newton's second law for rotation",
            formula = "∑_iτ_i = Iα",
            variables = mapOf(
                "∑ᵢτᵢ" to "The sum of all torque force (N⋅m)",
                "I" to "Moment of inertia (kg⋅m²)",
                "α" to "Angular acceleration (rads/s²)",
            ),
        ),
        Equation(
            name = "Incremental work done by a torque",
            formula = "dW = (∑_i τ_i) dθ",
            variables = mapOf(
                "dW" to "Infinitesimal change of work (J)",
                "(∑ᵢ τᵢ)" to "The sum of all discrete torque in the system (N⋅m)",
                "dθ" to "Infinitesimal change in angular position (rads)",
            ),
        ),
        Equation(
            name = "Work-energy theorem",
            formula = "W_{AB} = K_B - K_A",
            variables = mapOf(
                "W_{AB}" to "The total work done in the system (J)",
                "K_B" to "Kinetic energy at event/location B (J)",
                "K_A" to "Kinetic energy at event/location A (J)",
            ),
        ),
        Equation(
            name = "Rotational work done by a net force",
            formula = "W_{AB} = ∫_{θ_A}^{θ_B} (∑_i τ_i) dθ",
        ),
        Equation(
            name = "Rotational power",
            formula = "P = τω",
            variables = mapOf(
                "P" to "Power generated by the system (J⋅s)",
                "τ" to "Applied torque in the system (N⋅m)",
                "ω" to "Angular velocity (m/s)",
            ),
        ),
    )

    override val definitions: List<Definition> = listOf(
        Definition("angular acceleration", "time rate of change of angular velocity"),
        Definition("angular position", "angle a body has rotated through in a fixed coordinate system"),
        Definition("angular velocity", "time rate of change of angular position"),
        Definition("instantaneous angular acceleration", "derivative of angular velocity with respect to time"),
        Definition("instantaneous angular velocity", "derivative of angular position with respect to time"),
        Definition("kinematics of rotational motion", "describes the relationships among rotation angle, angular velocity, angular acceleration, and time"),
        Definition("lever arm", "perpendicular distance from the line that the force vector lies on to a given axis"),
        Definition("linear mass density", "the mass per unit length λ of a one dimensional object"),
        Definition("moment of inertia", "rotational mass of rigid bodies that relates to how easy or hard it will be to change the angular velocity of the rotating rigid body"),
        Definition("Newton's second law for rotation", "sum of the torques on a rotating system equals its moment of inertia times its angular acceleration"),
        Definition("parallel axis", "axis of rotation that is parallel to an axis about which the moment of inertia of an object is known"),
        Definition("parallel-axis theorem", "if the moment of inertia is known for a given axis, it can be found for any axis parallel to it"),
        Definition("rotational dynamics", "analysis of rotational motion using the net torque and moment of inertia to find the angular acceleration"),
        Definition("rotational kinetic energy", "kinetic energy due to the rotation of an object; this is part of its total kinetic energy"),
        Definition("rotational work", "work done on a rigid body due to the sum of the torques integrated over the angle through which the body rotates"),
        Definition("surface mass density", "mass per unit area σ of a two dimensional object"),
        Definition("torque", "cross product of a force and a lever arm to a given axis"),
        Definition("total linear acceleration", "vector sum of the centripetal acceleration vector and the tangential acceleration vector"),
    )

    /** Calculation functions for Chapter 10. */
    object Calculate {

        private fun round4(value: Double): Double = roundResult(value)

        /** Solves ax² + bx + c = 0. Returns both roots, or null if the discriminant is negative. */
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

        /** θ = s/r. Pass null for the unknown. */
        fun angularPosition(
            theta: Double? = null,
            arcLength: Double? = null,
            radius: Double? = null,
        ): Double {
            if (radius != null && radius <= 0.0) {
                throw IllegalArgumentException("Radius of rotational trajectory cannot be less than or equal to zero.")
            }

            if (arcLength != null && arcLength < 0) {
                throw IllegalArgumentException("An arc length cannot be a negative value.")
            }

            // Solve for s
            if (arcLength == null) {
                return round4(theta!! * radius!!)
            }

            // Solve for r
            if (radius == null) {
                if (theta == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                return round4(arcLength / theta!!)
            }

            // Solve for θ
            return round4(arcLength / radius)
        }

        /** v(t) = rω. Pass null for the unknown. */
        fun tangentialSpeed(
            tangSpeed: Double? = null,
            radius: Double? = null,
            angularVel: Double? = null,
        ): Double {
            if (radius != null && radius <= 0.0) {
                throw IllegalArgumentException("Radius must be greater than zero.")
            }

            // Solve for r
            if (radius == null) {
                if (angularVel == 0.0) throw IllegalArgumentException("Divison by zero is undefined.")
                return round4(tangSpeed!! / angularVel!!)
            }

            // Solve for ω
            if (angularVel == null) {
                return round4(tangSpeed!! / radius)
            }

            // Solve for v(t)
            return round4(radius * angularVel)
        }

        /** ω(ave) = (ω₀ + ω(f))/2. Pass null for the unknown. */
        fun averageAngularVel(
            aveAngularVel: Double? = null,
            initAngularVel: Double? = null,
            finalAngularVel: Double? = null,
        ): Double {
            // Solve for ω₀
            if (initAngularVel == null) {
                return round4((2.0 * aveAngularVel!!) - finalAngularVel!!)
            }

            // Solve for ω(f)
            if (finalAngularVel == null) {
                return round4((2.0 * aveAngularVel!!) - initAngularVel)
            }

            // Solve for ω(ave)
            return round4((initAngularVel + finalAngularVel) / 2.0)
        }

        /** θ(f) = θ₀ + ω(ave)t. Pass null for the unknown. */
        fun angularDisplacement(
            thetaFinal: Double? = null,
            thetaInit: Double? = null,
            aveAngularVel: Double? = null,
            time: Double? = null,
        ): Double {
            if (time != null && time < 0) throw IllegalArgumentException("Time cannot be a negative value.")

            // Solve for θ₀
            if (thetaInit == null) {
                return round4(thetaFinal!! - (aveAngularVel!! * time!!))
            }

            // Solve for ω(ave)
            if (aveAngularVel == null) {
                if (time == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                return round4((thetaFinal!! - thetaInit) / time!!)
            }

            // Solve for t
            if (time == null) {
                if (aveAngularVel == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                return round4((thetaFinal!! - thetaInit) / aveAngularVel)
            }

            // Solve for θ(f)
            return round4(thetaInit + (aveAngularVel * time))
        }

        /** ω(f) = ω₀ + αt. Pass null for the unknown. */
        fun angularVelConstAccel(
            finalAngularVel: Double? = null,
            initAngularVel: Double? = null,
            constAngularAccel: Double? = null,
            time: Double? = null,
        ): Double {
            if (time != null && time < 0) throw IllegalArgumentException("Time cannot be a negative value.")

            // Solve for ω₀
            if (initAngularVel == null) {
                return round4(finalAngularVel!! - (constAngularAccel!! * time!!))
            }

            // Solve for α
            if (constAngularAccel == null) {
                if (time == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                return round4((finalAngularVel!! - initAngularVel) / time!!)
            }

            // Solve for t (absent upstream; added so the equation's "t" chip is solvable)
            if (time == null) {
                if (constAngularAccel == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                return round4((finalAngularVel!! - initAngularVel) / constAngularAccel)
            }

            // Solve for ω(f)
            return round4(initAngularVel + (constAngularAccel * time))
        }

        /** θ(f) = θ₀ + ω₀t + ½αt². Pass null for the unknown. */
        fun angularDisplacementConstAccel(
            thetaFinal: Double? = null,
            thetaInit: Double? = null,
            initAngularVel: Double? = null,
            time: Double? = null,
            constAngularAccel: Double? = null,
        ): Double {
            if (time != null && time < 0) throw IllegalArgumentException("Time cannot be a negative value.")

            // Solve for θ₀
            if (thetaInit == null) {
                return round4(
                    thetaFinal!! - (initAngularVel!! * time!!) - (0.5 * constAngularAccel!! * (time * time))
                )
            }

            // Solve for ω₀
            if (initAngularVel == null) {
                if (time == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                return round4(
                    (thetaFinal!! - thetaInit - (0.5 * constAngularAccel!! * (time!! * time))) / time
                )
            }

            // Solve for α
            if (constAngularAccel == null) {
                if (time == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                return round4(
                    (thetaFinal!! - thetaInit - (initAngularVel * time!!)) * (2.0 / (time * time))
                )
            }

            // Solve for t
            if (time == null) {
                val c: Double = thetaInit - thetaFinal!!
                val b: Double = initAngularVel
                val a: Double = 0.5 * constAngularAccel
                val roots = quadraticEq(a, b, c)
                    ?: throw IllegalArgumentException("No real solution for time")

                // Return the non-negative root, preferring the smaller positive one.
                val validRoots = listOf(roots.first, roots.second).filter { it >= 0 }
                if (validRoots.isEmpty()) throw IllegalArgumentException("No positive time solution")
                return round4(validRoots.min())
            }

            // Solve for θ(f)
            return round4(
                thetaInit + (initAngularVel * time) + (0.5 * (constAngularAccel * (time * time)))
            )
        }

        /** ω(f)² = ω₀² + 2α(Δθ). Pass null for the unknown. */
        fun changeAngularVelocity(
            finalAngularVel: Double? = null,
            initAngularVel: Double? = null,
            constAngularAccel: Double? = null,
            deltaTheta: Double? = null,
        ): Double {
            // Solve for ω₀
            if (initAngularVel == null) {
                val radicand: Double =
                    (finalAngularVel!! * finalAngularVel) - (2 * constAngularAccel!! * deltaTheta!!)
                if (radicand < 0) {
                    throw IllegalArgumentException("A negative radicand yields an imaginary number. Check your values.")
                }
                return round4(sqrt(radicand))
            }

            // Solve for α
            if (constAngularAccel == null) {
                if (deltaTheta == 0.0) throw IllegalArgumentException("Divison by zero is undefined.")
                return round4(
                    ((finalAngularVel!! * finalAngularVel) - (initAngularVel * initAngularVel)) / (2 * deltaTheta!!)
                )
            }

            // Solve for Δθ
            if (deltaTheta == null) {
                if (constAngularAccel == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                return round4(
                    ((finalAngularVel!! * finalAngularVel) - (initAngularVel * initAngularVel)) / (2 * constAngularAccel)
                )
            }

            // Solve for ω(f)
            val radicand: Double =
                (initAngularVel * initAngularVel) + 2 * constAngularAccel * deltaTheta
            if (radicand < 0.0) {
                throw IllegalArgumentException("A negative radicand yields an imaginary number. Check your values.")
            }
            return round4(sqrt(radicand))
        }

        /** K = ½Iω². Pass null for the unknown. */
        fun rotationalKE(
            kineticEnergy: Double? = null,
            momentInertia: Double? = null,
            angularVel: Double? = null,
        ): Double {
            if (momentInertia != null && momentInertia < 0.0) {
                throw IllegalArgumentException("The moment of inertia cannot be a negative value.")
            }

            // Solve for I
            if (momentInertia == null) {
                if (angularVel == 0.0) throw IllegalArgumentException("Divison by zero is undefined.")
                return round4((2.0 * kineticEnergy!!) / (angularVel!! * angularVel))
            }

            // Solve for ω
            if (angularVel == null) {
                if (momentInertia == 0.0) throw IllegalArgumentException("Divison by zero is undefined.")

                val radicand: Double = (2.0 * kineticEnergy!!) / momentInertia
                if (radicand < 0) {
                    throw IllegalArgumentException("A negative radicand yields an imaginary number. Check your values.")
                }
                return round4(sqrt(radicand))
            }

            // Solve for K
            return round4(0.5 * momentInertia * (angularVel * angularVel))
        }

        /** |τ| = rFsinθ. Pass null for the unknown. θ is in degrees (converted internally). */
        fun magnitudeOfTorque(
            torque: Double? = null,
            radius: Double? = null,
            force: Double? = null,
            theta: Double? = null,
        ): Double {
            val thetaRadians: Double? = theta?.times(PI / 180.0)

            if (radius != null && radius <= 0.0) {
                throw IllegalArgumentException("The length of the center of axis to applied force cannot be less than or equal to zero.")
            }

            // Solve for r
            if (radius == null) {
                return round4(torque!! / (force!! * sin(thetaRadians!!)))
            }

            // Solve for F
            if (force == null) {
                return round4(torque!! / (radius * sin(thetaRadians!!)))
            }

            // Solve for θ
            if (theta == null) {
                if (force == 0.0) throw IllegalArgumentException("Division by zero is undefined.")

                val argument: Double = torque!! / (radius * force)
                // "math domain error" preserved verbatim from the original implementation; tests assert it.
                if (argument < -1.0 || argument > 1.0) throw IllegalArgumentException("math domain error")
                return round4(asin(argument) * (180 / PI))
            }

            // Solve for |τ|
            return round4(radius * force * sin(thetaRadians!!))
        }
    }
}
