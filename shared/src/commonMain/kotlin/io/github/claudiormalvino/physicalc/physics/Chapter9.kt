package io.github.claudiormalvino.physicalc.physics

import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.round

/** Chapter 9 — Linear momentum, impulse, and collisions. */
class Chapter9 : PhysicsChapter(
    title = "Ch.9 - Linear Momentum and Collisions",
    description = "Momentum, impulse, and elastic and inelastic collisions.",
) {

    override val equations: List<Equation> = listOf(
        Equation(
            name = "Definiiton of momentum",
            formula = "p = mv",
            variables = mapOf(
                "p" to "Momentum (N⋅s)",
                "m" to "Mass of the object (kg)",
                "velocity" to "m/s)",
            ),
        ),
        Equation(
            name = "Impulse",
            formula = "J = ∫_{t_1}^{t_2} F(t)dt = F_{avg}Δt",
            variables = mapOf(
                "J" to "Impulse (N⋅s)",
                "F(t)" to "Force as a function of time (N)",
                "F_{avg}" to "Average velocity",
                "Δt" to "Elapsed time",
            ),
        ),
        Equation(
            name = "Impulse-momentum theorem",
            formula = "J = Δp",
            variables = mapOf(
                "J" to "Impulse (N⋅s)",
                "Δp" to "Change in momentum (N⋅s)",
            ),
        ),
        Equation(
            name = "Average force from momentum",
            formula = "F = \\frac{Δp}{Δt}",
            variables = mapOf(
                "F" to "Average force (N)",
                "Δp" to "Change in momentum (N⋅s)",
                "Δt" to "Elapsed time (s)",
            ),
        ),
        Equation(
            name = "Instantaneous force from momentum",
            formula = "F(t) = \\frac{dp}{dt}",
            variables = mapOf(
                "F(t)" to "Instantaneous force (N)",
                "dp/dt" to "The rate of change of momentum with respect to time (N)",
            ),
        ),
        Equation(
            name = "Conservation of momentum",
            formula = "\\frac{dp_1}{dt} + \\frac{dp_2}{dt} = 0 ⟺ m_1v_1 + m_2v_2 = constant",
            variables = mapOf(
                "p₁" to "Momentum from the first object (N⋅s)",
                "p₂" to "Momentum from the second object (N⋅s)",
                "m_1" to "Mass of the first object (kg)",
                "v_1" to "Velocity of the first object (m/s)",
                "m_2" to "Mass of the second object (m)",
                "v_2" to "Velocity of the second object (m/s)",
            ),
        ),
        Equation(
            name = "Generalized conservation of momentum",
            formula = "∑_{j=1}^{N} p_j = constant",
            variables = mapOf("∑p_j" to "The sum of all momenta in the system"),
        ),
        Equation(
            name = "Conservation of momentum in two dimensions",
            formula = "p_{1,x} = p_{1,i,x} + p_{2,i,x}, p_{1,y} = p_{1,i,y} + p_{2,i,y}",
        ),
        Equation(
            name = "Inelastic collision of two objects (momentum)",
            formula = "m_1v_1 + m_2v_2 = Mv",
            variables = mapOf(
                "m_1" to "Mass of the first object",
                "v_1" to "Velocity of the first object (m/s)",
                "m_2" to "Mass of the second object (m)",
                "v_2" to "Velocity of the second object (m/s)",
                "M" to "Total mass of the object after collision (m(1) + m(2)) (kg)",
                "v" to "Velocity after the collision (m/s)",
            ),
            calculation = { v ->
                Calculate.inelasticCollisionMomentum(
                    mass1 = v["m_1"],
                    mass2 = v["m_2"],
                    velocity1 = v["v_1"],
                    velocity2 = v["v_2"],
                    velocityF = v["v"],
                    massF = v["M"],
                )
            },
        ),
        Equation(
            name = "Elastic collision of two objects (momentum)",
            formula = "m_1v_{i1} + m_2v_{i2} = m_1v_{f1} + m_2v_{f2}",
            variables = mapOf(
                "m_1" to "Mass of the first object (kg)",
                "m_2" to "Mass of the second object (kg)",
                "v_{i1}" to "Initial velocity of the first object (m/s)",
                "v_{i2}" to "Initial velocity of the second object (m/s)",
                "v_{f1}" to "Final velocity of the first object (m/s)",
                "v_{f2}" to "Final velocity of the second object (m/s)",
            ),
            calculation = { v ->
                Calculate.elasticCollisionMomentum(
                    mass1 = v["m_1"],
                    mass2 = v["m_2"],
                    velocityI1 = v["v_{i1}"],
                    velocityI2 = v["v_{i2}"],
                    velocityF1 = v["v_{f1}"],
                    velocityF2 = v["v_{f2}"],
                )
            },
        ),
        Equation(
            name = "External forces",
            formula = "F_{ext} = ∑_{j=1}^{N} \\frac{dp_j}{dt}",
            variables = mapOf(
                "F_{ext}" to "External force (N)",
                "∑dp_j/dt" to "The sum of all the rate of change of momenta with respect to time in the system",
            ),
        ),
        Equation(
            name = "Newton's second law for an extended object",
            formula = "F = \\frac{dp_{CM}}{dt}",
            variables = mapOf(
                "F" to "Force (N)",
                "dp_{CM}/dt" to "Rate of change of momentum from the center of mass with respect to time",
            ),
        ),
        Equation(
            name = "Acceleration of the center of mass",
            formula = "a_{CM} = \\frac{d^2}{dt^2} (\\frac{1}{M}∑_{j=1}^{N} m_jr_j) = \\frac{1}{M}∑_{j=1}^{N} m_ja_j",
            variables = mapOf(
                "a_{CM}" to "Acceleration of the center of mass (m/s²)",
                "M" to "Total mass of the system (kg)",
                "∑m_ja_j" to "Sum of the product of mass and acceleration of each individual object in the system",
            ),
        ),
        Equation(
            name = "Position of the center of mass for a system of particles",
            formula = "r_{CM} = \\frac{1}{M}∑_{j=1}^{N} m_jr_j",
            variables = mapOf(
                "r_{CM}" to "Position of the center of mass of the system (m)",
                "M" to "Total mass of the system",
                "∑m_jr_j" to "Sum of the product of mass and position of each object in the system",
            ),
        ),
        Equation(
            name = "Velocity of the center of mass",
            formula = "v_{CM} = \\frac{d}{dt} (\\frac{1}{M}∑_{j=1}^{N} m_jr_j) = \\frac{1}{M}∑_{j=1}^{N} m_jv_j",
            variables = mapOf(
                "v_{CM}" to "Velocity of the center of mass (m/s)",
                "M" to "Total mass of the system (m)",
                "∑m_jv_j" to "The sum of the product of mass and velocity of each object in the system",
            ),
        ),
        Equation(
            name = "Position of the center of mass of a continuous object",
            formula = "r_{CM} = \\frac{1}{M}∫r dm",
        ),
        Equation(
            name = "Rocket equation",
            formula = "Δv = u ln(\\frac{m_i}{m})",
            variables = mapOf(
                "Δv" to "Change of velocity obtained from loss of mass",
                "u" to "Velocity of the gas being exhausted from the rocket",
                "m_i" to "Initial mass of the rocket (with fuel)",
                "m" to "Mass of the rocket after the fuel has been exhausted",
            ),
            calculation = { v ->
                Calculate.rocketEquation(
                    deltaV = v["Δv"],
                    velExhaust = v["u"],
                    initialMass = v["m_i"],
                    finalMass = v["m"],
                )
            },
        ),
    )

    override val definitions: List<Definition> = listOf(
        Definition("center of mass", "weighted average position of the mass"),
        Definition("closed system", "system for which the mass is constant and the net external force on the system is zero"),
        Definition("elastic", "collision that conserves kinetic energy"),
        Definition("explosion", "single object breaks up into multiple objects; kinetic energy is not conserved in explosions"),
        Definition("external force", "force applied to an extended object that changes the momentum of the extended object as a whole"),
        Definition("impulse", "effect of applying a force on a system for a time interval; this time interval is usually small, but does not have to be"),
        Definition("impulse-momentum theorem", "change of momentum of a system is equal to the impulse applied to the system"),
        Definition("inelastic", "collision that does not conserve kinetic energy"),
        Definition("internal force", "force that the simple particles that make up an extended object exert on each other. Internal forces can be attractive or repulsive"),
        Definition("Law of Conservation of Momentum", "total momentum of a closed system cannot change"),
        Definition("linear mass density", "λ, expressed as the number of kilograms of material per meter"),
        Definition("momentum", "measure of the quantity of motion that an object has; it takes into account both how fast the object is moving, and its mass; specifically, it is the product of mass and velocity; it is a vector quantity"),
        Definition("perfectly inelastic", "collision after which all objects are motionless, the final kinetic energy is zero, and the loss of kinetic energy is a maximum"),
        Definition("rocket equation", "derived by the Soviet physicist Konstantin Tsiolkovsky in 1897, it gives us the change of velocity that the rocket obtains from burning a mass of fuel that decreases the total rocket mass from m_i down to m"),
        Definition("system", "object or collection of objects whose motion is currently under investigation; however, your system is defined at the start of the problem, you must keep that definition for the entire problem"),
    )

    /** Calculation functions for Chapter 9. */
    object Calculate {

        private fun round4(value: Double): Double = roundResult(value)

        /** m₁v₁ + m₂v₂ = Mv (perfectly inelastic collision). Pass null for the unknown. */
        fun inelasticCollisionMomentum(
            mass1: Double? = null,
            mass2: Double? = null,
            velocity1: Double? = null,
            velocity2: Double? = null,
            velocityF: Double? = null,
            massF: Double? = null,
        ): Double {
            if ((mass1 != null && mass1 <= 0.0) ||
                (mass2 != null && mass2 <= 0.0) ||
                (massF != null && massF <= 0.0)
            ) {
                throw IllegalArgumentException(
                    "We are operating with massive objects. Make sure all objects have a mass greater than zero."
                )
            }

            // Solve for m(1)
            if (mass1 == null) {
                if (velocity1 == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                return round4(((massF!! * velocityF!!) - (mass2!! * velocity2!!)) / velocity1!!)
            }

            // Solve for m(2)
            if (mass2 == null) {
                if (velocity2 == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                return round4(((massF!! * velocityF!!) - (mass1 * velocity1!!)) / velocity2!!)
            }

            // Solve for v(1)
            if (velocity1 == null) {
                return round4(((massF!! * velocityF!!) - (mass2 * velocity2!!)) / mass1)
            }

            // Solve for v(2)
            if (velocity2 == null) {
                return round4(((massF!! * velocityF!!) - (mass1 * velocity1)) / mass2)
            }

            // Solve for M
            if (massF == null) {
                if (velocityF == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                return round4(((mass1 * velocity1) + (mass2 * velocity2)) / velocityF!!)
            }

            // Solve for v
            if (velocityF == null) {
                return round4(((mass1 * velocity1) + (mass2 * velocity2)) / massF)
            }

            // All values provided; nothing to solve.
            return 0.0
        }

        /** m₁v(i1) + m₂v(i2) = m₁v(f1) + m₂v(f2) (elastic collision, momentum only). Pass null for the unknown. */
        fun elasticCollisionMomentum(
            mass1: Double? = null,
            mass2: Double? = null,
            velocityI1: Double? = null,
            velocityI2: Double? = null,
            velocityF1: Double? = null,
            velocityF2: Double? = null,
        ): Double {
            if ((mass1 != null && mass1 <= 0.0) || (mass2 != null && mass2 <= 0.0)) {
                // Whitespace run preserved verbatim from the original implementation; tests assert the exact message.
                throw IllegalArgumentException(
                    "We are operating with massive objects.                     Make sure all objects have a mass greater than zero."
                )
            }

            // Solve for m(1)
            if (mass1 == null) {
                if (velocityF1 == velocityI1) throw IllegalArgumentException("Divison by zero is undefined.")
                return round4(
                    ((mass2!! * velocityI2!!) - (mass2 * velocityF2!!)) / (velocityF1!! - velocityI1!!)
                )
            }

            // Solve for m(2)
            // Corrected from upstream: sign error in the numerator.
            if (mass2 == null) {
                if (velocityI2 == velocityF2) throw IllegalArgumentException("Divison by zero is undefined.")
                return round4(
                    ((mass1 * velocityF1!!) - (mass1 * velocityI1!!)) / (velocityI2!! - velocityF2!!)
                )
            }

            // Solve for v(i1)
            if (velocityI1 == null) {
                return round4(
                    ((mass1 * velocityF1!!) + (mass2 * velocityF2!!) - (mass2 * velocityI2!!)) / mass1
                )
            }

            // Solve for v(i2)
            if (velocityI2 == null) {
                return round4(
                    ((mass1 * velocityF1!!) + (mass2 * velocityF2!!) - (mass1 * velocityI1)) / mass2
                )
            }

            // Solve for v(f1)
            if (velocityF1 == null) {
                return round4(
                    ((mass1 * velocityI1) + (mass2 * velocityI2) - (mass2 * velocityF2!!)) / mass1
                )
            }

            // Solve for v(f2)
            if (velocityF2 == null) {
                return round4(
                    ((mass1 * velocityI1) + (mass2 * velocityI2) - (mass1 * velocityF1)) / mass2
                )
            }

            // All values provided; nothing to solve.
            return 0.0
        }

        /** Δv = u·ln(mᵢ/m) (Tsiolkovsky rocket equation). Pass null for the unknown. */
        fun rocketEquation(
            deltaV: Double? = null,
            velExhaust: Double? = null,
            initialMass: Double? = null,
            finalMass: Double? = null,
        ): Double {
            if ((initialMass != null && initialMass <= 0.0) || (finalMass != null && finalMass <= 0.0)) {
                // Whitespace run preserved verbatim from the original implementation; tests assert the exact message.
                throw IllegalArgumentException(
                    "We are operating with massive objects.                     Mass must be greater than zero."
                )
            }

            // Solve for u
            if (velExhaust == null) {
                return round4(deltaV!! / ln(initialMass!! / finalMass!!))
            }

            // Solve for m(i)
            if (initialMass == null) {
                return round4(exp(deltaV!! / velExhaust) * finalMass!!)
            }

            // Solve for m
            if (finalMass == null) {
                return round4(initialMass / exp(deltaV!! / velExhaust))
            }

            // Solve for Δv
            return round4(velExhaust * ln(initialMass / finalMass))
        }
    }
}
