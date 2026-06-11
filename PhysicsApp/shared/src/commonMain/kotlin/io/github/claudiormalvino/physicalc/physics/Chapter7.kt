package io.github.claudiormalvino.physicalc.physics

import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sqrt

/**
 * Chapter on Work and Kinetic Energy — the Kotlin twin of your Python `chapter7.py`.
 */
class Chapter7 : PhysicsChapter(
    title = "Ch.7 - Work and Kinetic Energy",
    description = "Study of work, kinetic energy, and power.",
) {

    override val equations: List<Equation> = listOf(
        Equation(
            name = "Work done by constant force",
            formula = "W = Fdcos(θ)",
            variables = mapOf(
                "W" to "Work (J)",
                "F" to "Constant force (N)",
                "d" to "Distance travelled (m)",
                "θ" to "Angle between the direction of motion and force vector (degrees)",
            ),
            calculation = { v ->
                Calculate.workConstantForce(
                    work = v["W"],
                    constF = v["F"],
                    distance = v["d"],
                    theta = v["θ"],
                )
            },
        ),
        Equation(
            name = "Work done by gravity",
            formula = "W = -mg(y - y_0)",
            variables = mapOf(
                "W" to "Work (J)",
                "m" to "Mass (kg)",
                "y₁" to "Initial height (m)",
                "y₂" to "Final height (m)",
            ),
            calculation = { v ->
                Calculate.workByGravity(
                    work = v["W"],
                    mass = v["m"],
                    initialHeight = v["y₁"],
                    finalHeight = v["y₂"],
                )
            },
        ),
        Equation(
            name = "Work done by a spring",
            formula = "W = -½k(x^2 - x_0^2)",
            variables = mapOf(
                "W" to "Work (J)",
                "k" to "Spring constant (kg/s²)",
                "x₁" to "Initial position (m)",
                "x₂" to "Final position",
            ),
            calculation = { v ->
                Calculate.workBySpring(
                    work = v["W"],
                    springConst = v["k"],
                    initialXpos = v["x₁"],
                    finalXpos = v["x₂"],
                )
            },
        ),
        Equation(
            name = "Kinetic energy",
            formula = "K = ½mv^2",
            variables = mapOf(
                "K" to "Kinetic energy (J)",
                "m" to "Mass (kg)",
                "v" to "Velocity (m/s)",
            ),
            calculation = { v ->
                Calculate.kineticEnergy(
                    kineticE = v["K"],
                    mass = v["m"],
                    velocity = v["v"],
                )
            },
        ),
        Equation(
            name = "Kinetic energy (momentum representation)",
            formula = "K = ½(p^2/m)",
            variables = mapOf(
                "K" to "Kinetic energy (J)",
                "m" to "Mass (kg)",
                "p" to "Momentum (N*s)",
            ),
            calculation = { v ->
                Calculate.kineticEnergyMomentum(
                    kineticE = v["K"],
                    mass = v["m"],
                    momentum = v["p"],
                )
            },
        ),
        Equation(
            name = "Work-Energy theorem",
            formula = "W_{net} = ½mv^2 - ½mv_0^2",
            variables = mapOf(
                "W_{net}" to "Net work (J)",
                "m" to "Mass (kg)",
                "v₂" to "Final velocity (m/s)",
                "v₁" to "Initial velocity (m/s)",
            ),
            calculation = { v ->
                Calculate.workEnergyTheorem(
                    netWork = v["W_{net}"],
                    mass = v["m"],
                    finalVel = v["v₂"],
                    initialVel = v["v₁"],
                )
            },
        ),
        Equation(
            name = "Average power",
            formula = "P_{avg} = ΔW/Δt",
            variables = mapOf(
                "P_{avg}" to "Average power (J/s)",
                "ΔW" to "Elapsed work done (W)",
                "Δt" to "Elapsed time (s)",
            ),
        ),
    )

    override val definitions: List<Definition> = listOf(
        Definition("average power", "work done in a time interval divided by the time interval"),
        Definition("kinetic energy", "energy of motion, one-half an object's mass times the square of its speed"),
        Definition("net work", "work done by all the forces acting on an object"),
        Definition("power", "(or instantaneous power) rate of doing work"),
        Definition("work", "done when a force acts on something that undergoes a displacement from one position to another"),
        Definition("work-energy theorem", "net work done on a particle is equal to the change in its kinetic energy"),
    )

    /**
     * Holds the calculation functions for Chapter 7 — the twin of the nested
     * Python `class Calculate`.
     */
    object Calculate {

        /** Gravitational acceleration on Earth [m/s²]. */
        const val G: Double = 9.82

        /** Rounds to 4 decimal places, matching Python's `round(x, 4)`. */
        private fun round4(value: Double): Double = round(value * 10000.0) / 10000.0

        /**
         * W = Fd·cos(θ). Pass every value except the one you want solved for;
         * leave that one as `null`. θ is taken/returned in degrees.
         */
        fun workConstantForce(
            work: Double? = null,
            constF: Double? = null,
            distance: Double? = null,
            theta: Double? = null,
        ): Double {
            // Converts user input of degrees into SI units of radians.
            // (null only when θ itself is the unknown, in which case it is never read.)
            val thetaRadians: Double? = theta?.times(PI / 180.0)

            // Solve for F (constant force)
            if (constF == null) {
                if (theta == 90.0 || theta == 270.0 || distance == 0.0) {
                    throw IllegalArgumentException("Division by zero is undefined.")
                }
                return round4(work!! / (cos(thetaRadians!!) * distance!!))
            }

            // Solve for d (distance)
            if (distance == null) {
                if (theta == 90.0 || theta == 270.0) {
                    throw IllegalArgumentException("Division by zero is undefined.")
                }
                if (constF == 0.0) {
                    throw IllegalArgumentException("Division by zero is undefined.")
                }
                return round4(work!! / (constF * cos(thetaRadians!!)))
            }

            // Solve for θ (angle), returned in degrees
            if (theta == null) {
                if (constF == 0.0 || distance == 0.0) {
                    throw IllegalArgumentException("Division by zero is undefined.")
                }
                val argument: Double = work!! / (constF * distance)
                return round4(acos(argument) * (180.0 / PI))
            }

            // Solve for W (work) — the default case
            return round4(constF * distance * cos(thetaRadians!!))
        }

        /**
         * W = -mg(y₂ - y₁). Pass every value except the unknown; leave that one `null`.
         */
        fun workByGravity(
            work: Double? = null,
            mass: Double? = null,
            initialHeight: Double? = null,
            finalHeight: Double? = null,
        ): Double {
            if (mass != null && mass <= 0) {
                throw IllegalArgumentException(
                    "We are operating with massive objects.                 Mass must be greater than zero."
                )
            }

            // Solve for m (mass)
            if (mass == null) {
                if (finalHeight == initialHeight) {
                    throw IllegalArgumentException("Division by zero is undefined.")
                }

                val result: Double = -work!! / (G * (finalHeight!! - initialHeight!!))

                if (result < 0) {
                    throw IllegalArgumentException(
                        "Mass cannot be negative. Check your signs or initial and final heights."
                    )
                }
                return round4(result)
            }

            // Solve for y₁ (initial height)
            if (initialHeight == null) {
                return round4((work!! / (mass * G)) + finalHeight!!)
            }

            // Solve for y₂ (final height)
            if (finalHeight == null) {
                return round4((-work!! / (mass * G)) + initialHeight)
            }

            // Solve for W (work) — the default case
            return round4(-mass * G * (finalHeight - initialHeight))
        }

        /**
         * W = -½k(x₂² - x₁²). Pass every value except the unknown; leave that one `null`.
         *
         * NOTE (faithful to the Python source): when solving for x₁ or x₂ this returns
         * the SQUARE of the position (x² = ±2W/k + x²), exactly as the Python does —
         * the Python test suite encodes those squared values as the expected results.
         */
        fun workBySpring(
            work: Double? = null,
            springConst: Double? = null,
            initialXpos: Double? = null,
            finalXpos: Double? = null,
        ): Double {
            if (springConst != null && springConst < 0) {
                throw IllegalArgumentException("Spring constant cannot be a negative value.")
            }

            // Solve for k (spring constant)
            if (springConst == null) {
                if (finalXpos == initialXpos) {
                    throw IllegalArgumentException("Division by zero is undefined.")
                }
                return round4(
                    (-2.0 * work!!) / ((finalXpos!! * finalXpos) - (initialXpos!! * initialXpos))
                )
            }

            // Solve for x₁ (initial position, squared — see note above)
            if (initialXpos == null) {
                return round4(((2.0 * work!!) / springConst) + (finalXpos!! * finalXpos))
            }

            // Solve for x₂ (final position, squared — see note above)
            if (finalXpos == null) {
                return round4(((-2.0 * work!!) / springConst) + (initialXpos * initialXpos))
            }

            // Solve for W (work) — the default case
            return round4(
                -0.5 * springConst * ((finalXpos * finalXpos) - (initialXpos * initialXpos))
            )
        }

        /**
         * K = ½mv². Pass every value except the unknown; leave that one `null`.
         */
        fun kineticEnergy(
            kineticE: Double? = null,
            mass: Double? = null,
            velocity: Double? = null,
        ): Double {
            if (mass != null && mass <= 0) {
                throw IllegalArgumentException(
                    "We are operating with massive objects. Mass must be greater than zero."
                )
            }

            if (velocity != null && velocity < 0) {
                throw IllegalArgumentException("This is a scalar product. Velocity cannot be negative")
            }

            // Solve for m (mass)
            if (mass == null) {
                val result: Double = kineticE!! * (2.0 / (velocity!! * velocity))
                if (result < 0.0) {
                    throw IllegalArgumentException(
                        "We are operating with massive objects. Mass must be greater than zero."
                    )
                }
                return round4(result)
            }

            // Solve for v (velocity)
            if (velocity == null) {
                val radicand: Double = kineticE!! * (2.0 / mass)
                if (radicand < 0.0) {
                    throw IllegalArgumentException(
                        "Negative radicand yields an imaginary number. Check the value of your kinetic energy"
                    )
                }
                return round4(sqrt(radicand))
            }

            // Solve for K (kinetic energy) — the default case
            return round4(0.5 * mass * (velocity * velocity))
        }

        /**
         * K = ½(p²/m). Pass every value except the unknown; leave that one `null`.
         */
        fun kineticEnergyMomentum(
            kineticE: Double? = null,
            mass: Double? = null,
            momentum: Double? = null,
        ): Double {
            if (mass != null && mass < 0) {
                throw IllegalArgumentException(
                    "We are operating with massive objects. Mass must be greater than zero."
                )
            }

            if (momentum != null && momentum < 0) {
                throw IllegalArgumentException("This is a scalar product. Velocity cannot be negative")
            }

            // Solve for m (mass)
            if (mass == null) {
                val result: Double = (momentum!! * momentum) / (kineticE!! * 2.0)
                if (result < 0.0) {
                    throw IllegalArgumentException(
                        "We are operating with massive objects. Mass must be greater than zero. Check your value for kinetic energy."
                    )
                }
                return round4(result)
            }

            // Solve for p (momentum)
            if (momentum == null) {
                val radicand: Double = kineticE!! * 2.0 * mass
                if (radicand < 0.0) {
                    throw IllegalArgumentException(
                        "Negative radicand yields an imaginary number. Check the value of your kinetic energy"
                    )
                }
                return round4(sqrt(radicand))
            }

            // Solve for K (kinetic energy) — the default case
            return round4((momentum * momentum) / (2.0 * mass))
        }

        /**
         * W(net) = ½mv₂² - ½mv₁². Pass every value except the unknown; leave that one `null`.
         */
        fun workEnergyTheorem(
            netWork: Double? = null,
            mass: Double? = null,
            finalVel: Double? = null,
            initialVel: Double? = null,
        ): Double {
            if (mass != null && mass < 0) {
                throw IllegalArgumentException(
                    "We are operating with massive objects. Mass must be greater than zero."
                )
            }

            // Solve for m (mass)
            if (mass == null) {
                val result: Double = (2.0 * netWork!!) /
                    ((finalVel!! * finalVel) - (initialVel!! * initialVel))
                if (result < 0.0) {
                    throw IllegalArgumentException(
                        "We are operating with massive objects. Mass must be greater than zero. Check your values"
                    )
                }
                return round4(result)
            }

            // Solve for v₂ (final velocity)
            if (finalVel == null) {
                val radicand: Double = ((2.0 * netWork!!) / mass) + (initialVel!! * initialVel)
                if (radicand < 0) {
                    throw IllegalArgumentException(
                        "Negative radicand produces an imaginary number. Check your signs."
                    )
                }
                return round4(sqrt(radicand))
            }

            // Solve for v₁ (initial velocity)
            if (initialVel == null) {
                val radicand: Double = ((-2.0 * netWork!!) / mass) + (finalVel * finalVel)
                if (radicand < 0) {
                    throw IllegalArgumentException(
                        "Negative radicand produces an imaginary number. Check your signs."
                    )
                }
                return round4(sqrt(radicand))
            }

            // Solve for W(net) (net work) — the default case
            return round4((0.5 * mass) * ((finalVel * finalVel) - (initialVel * initialVel)))
        }
    }
}
