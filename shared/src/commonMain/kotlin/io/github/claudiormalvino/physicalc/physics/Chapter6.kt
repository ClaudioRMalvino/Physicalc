package io.github.claudiormalvino.physicalc.physics

import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.round
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * Chapter on Applications of Newton's Laws — the Kotlin twin of your Python `chapter6.py`.
 */
class Chapter6 : PhysicsChapter(
    title = "Ch.6 - Applications of Newton's Laws",
    description = "Friction, drag, and the dynamics of circular motion.",
) {

    override val equations: List<Equation> = listOf(
        Equation(
            name = "Magnitude of static friction",
            formula = "f_s ≤ μ_sN",
            variables = mapOf(
                "fₛ" to "Static friction [N]",
                "μₛ" to "coefficient of static friction (dimensionless)",
                "N" to "Normal force (N)",
            ),
        ),
        Equation(
            name = "Magnitude of kinetic friction",
            formula = "f_k = μ_kN",
            variables = mapOf(
                "fₖ" to "Kinetic friction (N)",
                "μₖ" to "Coefficient of kinetic friction (dimensionless)",
                "N" to "Normal force (N)",
            ),
        ),
        Equation(
            name = "Centripetal force with tangential velocity",
            formula = "F_c = mv^2/r",
            variables = mapOf(
                "F_c" to "Centripetal force (N)",
                "m" to "Mass of the object (kg)",
                "v" to "Tangential velocity (m/s)",
                "r" to "Radius (m)",
            ),
            // NOTE: the Python source wired this equation to the *angular*-velocity
            // solver (and vice versa). The wiring here matches the formula shown.
            calculation = { v ->
                Calculate.centripetalForceTangVel(
                    centripetalF = v["F_c"],
                    mass = v["m"],
                    velocity = v["v"],
                    radius = v["r"],
                )
            },
        ),
        Equation(
            name = "Centripetal force with angular velocity",
            formula = "F_c = mrω^2",
            variables = mapOf(
                "F_c" to "Centripetal force (N)",
                "m" to "Mass of the object (kg)",
                "ω" to "Tangential velocity (rads/s)",
                "r" to "Radius (m)",
            ),
            calculation = { v ->
                Calculate.centripetalForceAngVel(
                    centripetalF = v["F_c"],
                    mass = v["m"],
                    angularVel = v["ω"],
                    radius = v["r"],
                )
            },
        ),
        Equation(
            name = "Ideal angle of a banked curve",
            formula = "tan θ = v^2/rg",
            variables = mapOf(
                "θ" to "Ideal angle (degrees)",
                "v" to "Velocity (m/s)",
                "r" to "Radius of curvature (m)",
            ),
            calculation = { v ->
                Calculate.idealAngBankedCurve(
                    theta = v["θ"],
                    velocity = v["v"],
                    radius = v["r"],
                )
            },
        ),
        Equation(
            name = "Drag force",
            formula = "F_D = ½CρAv^2",
            variables = mapOf(
                "F_D" to "Drag force (N)",
                "C" to "Drag coefficient (dimensionless)",
                "ρ" to "Fluid density (kg/m³)",
                "A" to "Area of the object (m²)",
                "v" to "Velocity of the object (m/s)",
            ),
            calculation = { v ->
                Calculate.dragForce(
                    dragF = v["F_D"],
                    dragCoeff = v["C"],
                    fluidDens = v["ρ"],
                    area = v["A"],
                    velocity = v["v"],
                )
            },
        ),
        Equation(
            name = "Stoke's law",
            formula = "F_s = 6πrηv",
            variables = mapOf(
                "Fₛ" to "Drag force (Stokes force) (N)",
                "r" to "Radius of the object (m)",
                "η" to "Dynamic viscosity of the fluid (N⋅s/m²)",
                "v" to "Velocity of the object (m/s)",
            ),
            calculation = { v ->
                Calculate.stokesLaw(
                    dragFs = v["Fₛ"],
                    radius = v["r"],
                    viscosity = v["η"],
                    velocity = v["v"],
                )
            },
        ),
        Equation(
            name = "Terminal velocity",
            formula = "v_t = √(2mg/ρCA)",
            variables = mapOf(
                "vₜ" to "Terminal velocity (m/s)",
                "m" to "Mass of the object (kg)",
                "C" to "Drag coefficient (dimensionless)",
                "A" to "Area of the object (m²)",
                "ρ" to "Fluid density (kg/m³)",
            ),
            calculation = { v ->
                Calculate.terminalVelocity(
                    terminalVel = v["vₜ"],
                    mass = v["m"],
                    dragCoeff = v["C"],
                    area = v["A"],
                    fluidDens = v["ρ"],
                )
            },
        ),
    )

    override val definitions: List<Definition> = listOf(
        Definition("banked curve", "curve in a road that is sloping in a manner that helps a vehicle negotiate the curve"),
        Definition("centripetal force", "any net force causing uniform circular motion"),
        Definition("Coriolis force", "inertial force causing the apparent deflection of moving objects when viewed in a rotating frame of reference"),
        Definition("drag force", "force that always opposes the motion of an object in a fluid; unlike simple friction, the drag force is proportional to some function of the velocity of the object in that fluid"),
        Definition("friction", "force that opposes relative motion or attempts at motion between systems in contact"),
        Definition("ideal banking", "sloping of a curve in a road, where the angle of the slope allows the vehicle to negotiate the curve at a certain speed without the aid of friction between the tires and the road; the net external force on the vehicle equals the horizontal centripetal force in the absence of friction"),
        Definition("inertial force", "force that has no physical origin"),
        Definition("kinetic friction", "force that opposes the motion of two systems that are in contact and moving relative to each other"),
        Definition("noninertial frame of reference", "accelerated frame of reference"),
        Definition("static friction", "force that opposes the motion of two systems that are in contact and are not moving relative to each other"),
        Definition("terminal velocity", "constant velocity achieved by a falling object, which occurs when the weight of the object is balanced by the upward drag force"),
    )

    /**
     * Holds the calculation functions for Chapter 6 — the twin of your nested
     * Python `class Calculate`.
     */
    object Calculate {

        /** Gravitational acceleration on Earth [m/s²]. */
        const val G: Double = 9.82

        /*
         * The Python source wrapped the messages below across lines with a backslash
         * continuation INSIDE the string literal, so the trailing space plus the next
         * line's indentation (20 or 24 spaces) are part of the message. They are kept
         * verbatim here, typos included, because the tests assert them exactly.
         */
        private const val MASS_ERROR =
            "We are operating with massive objects. " +
                "                    Mass must be greater than zero."
        private const val MASS_SIGN_ERROR =
            "We are operating with massive objects. " +
                "                    Mass must be greater than zero. Check your signs."
        private const val RADICAND_SIGN_ERROR =
            "Negative radicand produces an imaginary number. " +
                "                        Check your signs."
        private const val DRAG_COEFF_SIGN_ERROR =
            "Drag coefficient is a positive value. " +
                "                        Check your signs."
        private const val FLUID_DENS_SIGN_ERROR =
            "Fluid density is a positive value. " +
                "                        Check your signs."
        private const val AREA_SIGN_ERROR =
            "Area cannot be negative. " +
                "                        Check your signs."
        private const val RADIUS_SIGN_ERROR =
            "The radius cannot be negative. " +
                "                        Check your signs."
        private const val VISCOSITY_VELOCITY_ERROR =
            "Velocity cannot be less than or euqal to 0. " +
                "                        This makes viscosity a negative value."

        /** Rounds to 4 decimal places, matching Python's `round(x, 4)`. */
        private fun round4(value: Double): Double = round(value * 10000.0) / 10000.0

        /**
         * F(c) = mv²/r. Pass every value except the one you want solved for;
         * leave that one as `null`.
         */
        fun centripetalForceTangVel(
            centripetalF: Double? = null,
            mass: Double? = null,
            velocity: Double? = null,
            radius: Double? = null,
        ): Double {
            if (mass != null && mass <= 0) throw IllegalArgumentException(MASS_ERROR)

            if (radius != null && radius <= 0) {
                throw IllegalArgumentException("Radius must be greater than zero.")
            }

            // Solve for m (mass)
            if (mass == null) {
                if (velocity == 0.0) throw IllegalArgumentException("Divison by zero is undefined.")

                val massResult: Double = (radius!! * centripetalF!!) / (velocity!! * velocity)

                if (massResult <= 0) throw IllegalArgumentException(MASS_SIGN_ERROR)
                return round4(massResult)
            }

            // Solve for v (tangential velocity)
            if (velocity == null) {
                val radicand: Double = (centripetalF!! * radius!!) / mass

                if (radicand < 0) throw IllegalArgumentException(RADICAND_SIGN_ERROR)
                return round4(sqrt(radicand))
            }

            // Solve for r (radius)
            if (radius == null) {
                if (centripetalF == 0.0) throw IllegalArgumentException("Divison by zero is undefined.")

                val radiusResult: Double = (mass * (velocity * velocity)) / centripetalF!!

                if (radiusResult < 0) {
                    throw IllegalArgumentException("Radius cannot be negative. Check your signs.")
                }
                return round4(radiusResult)
            }

            // Solve for F(c) (centripetal force) — the default case
            return round4((mass * (velocity * velocity)) / radius)
        }

        /**
         * F(c) = mrω². Pass every value except the one you want solved for;
         * leave that one as `null`.
         */
        fun centripetalForceAngVel(
            centripetalF: Double? = null,
            mass: Double? = null,
            angularVel: Double? = null,
            radius: Double? = null,
        ): Double {
            if (mass != null && mass <= 0) throw IllegalArgumentException(MASS_ERROR)

            if (radius != null && radius <= 0) {
                throw IllegalArgumentException("Radius must be greater than zero.")
            }

            // Solve for m (mass)
            if (mass == null) {
                if (angularVel == 0.0) throw IllegalArgumentException("Division by zero is undefined.")

                if (centripetalF!! < 0 || angularVel!! < 0) {
                    throw IllegalArgumentException("Mass cannot be negative. Check your signs.")
                }

                return round4(centripetalF / (radius!! * (angularVel * angularVel)))
            }

            // Solve for ω (angular velocity)
            if (angularVel == null) {
                val radicand: Double = centripetalF!! / (mass * radius!!)

                if (radicand < 0) {
                    throw IllegalArgumentException("Negative radicand produces an imaginary number.")
                }
                return round4(sqrt(radicand))
            }

            // Solve for r (radius)
            if (radius == null) {
                if (angularVel == 0.0) throw IllegalArgumentException("Divison by zero is undefined.")

                if (centripetalF!! < 0 || angularVel < 0) {
                    throw IllegalArgumentException("Radius cannot be negative. Check your signs.")
                }

                return round4(centripetalF / (mass * (angularVel * angularVel)))
            }

            // Solve for F(c) (centripetal force) — the default case
            return round4(mass * (angularVel * angularVel) * radius)
        }

        /**
         * tan θ = v²/(rg). Pass every value except the one you want solved for;
         * leave that one as `null`. Theta is taken and returned in degrees.
         */
        fun idealAngBankedCurve(
            theta: Double? = null,
            velocity: Double? = null,
            radius: Double? = null,
        ): Double {
            var thetaRadians = 0.0
            if (theta != null) {
                if (theta < 0) {
                    throw IllegalArgumentException("Reconsider if theta can physically be a negative value.")
                }

                // Converts degrees into radians
                thetaRadians = theta * (PI / 180)
            }

            if (radius != null && radius <= 0) {
                throw IllegalArgumentException("Radius cannot be less than or equal to zero.")
            }

            // Solve for v (velocity)
            if (velocity == null) {
                if (theta == 90.0 || theta == 270.0) {
                    throw IllegalArgumentException("Tangent function is undefined at 90.0 and 270.0 degrees.")
                }

                val radicand: Double = radius!! * G * tan(thetaRadians)

                return round4(sqrt(radicand))
            }

            // Solve for r (radius)
            if (radius == null) {
                if (velocity == 0.0) throw IllegalArgumentException("Division by zero is undefined.")

                // NOTE: the Python source computed ((v²)/g)·tan θ, which is the wrong
                // inversion of tan θ = v²/(rg). The correct solution is r = v²/(g·tan θ).
                return round4((velocity * velocity) / (G * tan(thetaRadians)))
            }

            // Solve for θ (ideal angle) — the default case
            val argument: Double = (velocity * velocity) / (radius * G)

            return round4(atan(argument) * (180 / PI))
        }

        /**
         * F(D) = ½CρAv². Pass every value except the one you want solved for;
         * leave that one as `null`. The drag force itself is returned negative
         * (it opposes the motion), exactly like the Python source.
         */
        fun dragForce(
            dragF: Double? = null,
            dragCoeff: Double? = null,
            fluidDens: Double? = null,
            area: Double? = null,
            velocity: Double? = null,
        ): Double {
            if (dragCoeff != null && dragCoeff <= 0.0) {
                throw IllegalArgumentException("The drag coefficient cannot be less than or equalt to zero.")
            }

            if (area != null && area <= 0) {
                throw IllegalArgumentException("Area cannot be less than zero or equal to zero.")
            }

            if (fluidDens != null && fluidDens <= 0) {
                throw IllegalArgumentException("Fluid density cannot be less than or equal to zero.")
            }

            // Solve for C (drag coefficient)
            if (dragCoeff == null) {
                if (velocity == 0.0) throw IllegalArgumentException("Divison by zero is undefined.")
                if (velocity!! < 0) throw IllegalArgumentException(DRAG_COEFF_SIGN_ERROR)

                return round4(dragF!! / (0.5 * fluidDens!! * area!! * (velocity * velocity)))
            }

            // Solve for ρ (fluid density)
            if (fluidDens == null) {
                if (velocity == 0.0) throw IllegalArgumentException("Divison by zero is undefined.")
                if (velocity!! < 0) throw IllegalArgumentException(FLUID_DENS_SIGN_ERROR)

                return round4(dragF!! / (0.5 * dragCoeff * area!! * (velocity * velocity)))
            }

            // Solve for A (area)
            if (area == null) {
                if (velocity == 0.0) throw IllegalArgumentException("Divison by zero is undefined.")
                if (velocity!! < 0) throw IllegalArgumentException(AREA_SIGN_ERROR)

                return round4(dragF!! / (0.5 * dragCoeff * fluidDens * (velocity * velocity)))
            }

            // Solve for v (velocity)
            if (velocity == null) {
                val radicand: Double = dragF!! / (0.5 * dragCoeff * fluidDens * area)
                return round4(sqrt(radicand))
            }

            // Solve for F(D) (drag force) — the default case
            return round4(-0.5 * dragCoeff * fluidDens * area * (velocity * velocity))
        }

        /**
         * Fₛ = 6πrηv. Pass every value except the one you want solved for;
         * leave that one as `null`. The Stokes force itself is returned negative
         * (it opposes the motion), exactly like the Python source.
         */
        fun stokesLaw(
            dragFs: Double? = null,
            radius: Double? = null,
            viscosity: Double? = null,
            velocity: Double? = null,
        ): Double {
            val coefficient: Double = 6 * PI // Constant coefficient

            if (radius != null && radius <= 0) {
                throw IllegalArgumentException("Radius cannot be less than zero or equal to zero.")
            }

            if (viscosity != null && viscosity < 0) {
                throw IllegalArgumentException("Viscocity cannot be a negative value.")
            }

            // Solve for r (radius)
            if (radius == null) {
                if (velocity == 0.0 || viscosity == 0.0) {
                    throw IllegalArgumentException("Divison by zero is undefined.")
                }
                if (velocity!! < 0) throw IllegalArgumentException(RADIUS_SIGN_ERROR)

                return round4(dragFs!! / (coefficient * viscosity!! * velocity))
            }

            // Solve for η (viscosity)
            if (viscosity == null) {
                if (velocity!! <= 0) throw IllegalArgumentException(VISCOSITY_VELOCITY_ERROR)

                return round4(dragFs!! / (coefficient * radius * velocity))
            }

            // Solve for v (velocity)
            if (velocity == null) {
                return round4(dragFs!! / (coefficient * radius * viscosity))
            }

            // Solve for Fₛ (Stokes force) — the default case
            return round4(-coefficient * radius * viscosity * velocity)
        }

        /**
         * vₜ = √(2mg/(ρCA)). Pass every value except the one you want solved for;
         * leave that one as `null`.
         */
        fun terminalVelocity(
            terminalVel: Double? = null,
            mass: Double? = null,
            dragCoeff: Double? = null,
            area: Double? = null,
            fluidDens: Double? = null,
        ): Double {
            if (dragCoeff != null && dragCoeff < 0.0) {
                throw IllegalArgumentException("The drag coefficient cannot be a negative value.")
            }

            if (area != null && area <= 0) {
                throw IllegalArgumentException("Area cannot be less than zero or equal to zero.")
            }

            if (fluidDens != null && fluidDens <= 0) {
                throw IllegalArgumentException("Fluid density cannot be less than or equal to zero.")
            }

            if (mass != null && mass <= 0) throw IllegalArgumentException(MASS_ERROR)

            // NOTE: the Python source used sqrt(vₜ) in the three inversions below; the
            // correct inversion of vₜ = √(2mg/(ρCA)) uses vₜ² instead.

            // Solve for m (mass)
            if (mass == null) {
                return round4((terminalVel!! * terminalVel) * ((dragCoeff!! * area!! * fluidDens!!) / (2 * G)))
            }

            // Solve for C (drag coefficient)
            if (dragCoeff == null) {
                return round4((2 * mass * G) / ((terminalVel!! * terminalVel) * area!! * fluidDens!!))
            }

            // Solve for A (area)
            if (area == null) {
                if (dragCoeff == 0.0) throw IllegalArgumentException("Division by zero is undefined.")

                return round4((2 * mass * G) / ((terminalVel!! * terminalVel) * dragCoeff * fluidDens!!))
            }

            // Solve for ρ (fluid density)
            // NOTE: the Python source tested `fluid_dens == 0` here, which is unreachable
            // (zero already raises above and None == 0 is False), so its fluid-density
            // solver could never run. Translated as the intended null check.
            if (fluidDens == null) {
                if (dragCoeff == 0.0) throw IllegalArgumentException("Division by zero is undefined.")

                return round4((2 * mass * G) / ((terminalVel!! * terminalVel) * dragCoeff * area))
            }

            // Solve for vₜ (terminal velocity) — the default case
            val radicand: Double = (2 * mass * G) / (fluidDens * dragCoeff * area)

            return round4(sqrt(radicand))
        }
    }
}
