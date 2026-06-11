package io.github.claudiormalvino.physicalc.physics

import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cbrt
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sqrt

/**
 * Chapter on gravitation — the Kotlin twin of your Python `chapter13.py`.
 *
 * Several calculation branches in the Python source contained bugs (undefined
 * variables, `sqrt` where a square was needed, operator-precedence slips,
 * `cbrt` where `sqrt` was needed, and a duplicated unreachable branch). The
 * CORRECT physics is implemented here; the original error-message strings are
 * preserved verbatim — including the literal whitespace runs that Python's
 * backslash line-continuations embedded in them.
 */
class Chapter13 : PhysicsChapter(
    title = "Ch.13 - Gravitation",
    description = "Newtonian gravitation, orbits, and Kepler's laws.",
) {

    override val equations: List<Equation> = listOf(
        Equation(
            name = "Newton's law of gravitation",
            formula = "F_{12} = G(m_1m_2/r^2)\\hat{r}_{12}",
            variables = mapOf(
                "F_{12}" to "Gravitational force between from object 1 on object 2 (N)",
                "G" to "Newton's gravitational constant:  6.674 × 10⁻¹¹ m³ kg⁻¹ s⁻² (constant)",
                "m_1" to "Mass of object 1 (kg)",
                "m_2" to "Mass of object 2 (kg)",
                "r" to "Distance between the centers of mass of m(1) to m(2) (m)",
            ),
            calculation = { v ->
                Calculate.lawOfGravitation(
                    force12 = v["F_{12}"],
                    mass1 = v["m_1"],
                    mass2 = v["m_2"],
                    distance = v["r"],
                )
            },
        ),
        Equation(
            name = "Acceleration due to gravity at the surface of a stellar body",
            formula = "g = GM/r^2",
            variables = mapOf(
                "g" to "Acceleration due to gravity (m/s²)",
                "G" to "Newton's gravitational constant:  6.674 × 10⁻¹¹ m³ kg⁻¹ s⁻² (constant)",
                "M" to "Mass of the stellar body (kg)",
                "r" to "Radius of the stellar body (m)",
            ),
            calculation = { v ->
                Calculate.gravitationalAcceleration(
                    g = v["g"],
                    massBody = v["M"],
                    distance = v["r"],
                )
            },
        ),
        Equation(
            name = "Gravitational potential energy beyond a stellar body",
            formula = "U = -GMm/r",
            variables = mapOf(
                "U" to "Gravitational potential energy (J)",
                "G" to "Newton's gravitational constant:  6.674 × 10⁻¹¹ m³ kg⁻¹ s⁻² (constant)",
                "M" to "Mass of the stellar body (kg)",
                "m" to "Mass of the object (kg)",
                "r" to "Distance from the center of mass of M to  m (m)",
            ),
            calculation = { v ->
                Calculate.gravitationalPotential(
                    potentialEnergy = v["U"],
                    massBody = v["M"],
                    massObject = v["m"],
                    distance = v["r"],
                )
            },
        ),
        Equation(
            name = "Conservation of energy",
            formula = "½mv_1^2 - GMm/r_1 = ½mv_2^2 - GMm/r_2",
            variables = mapOf(
                "G" to "Newton's gravitational constant:  6.674 × 10⁻¹¹ m³ kg⁻¹ s⁻² (constant)",
                "m" to "Mass of the object (kg)",
                "M" to "Mass of the stellar body (kg)",
                "v_1" to "Velocity of object 1 (m/s)",
                "v_2" to "Velocity of object 2 (m/s)",
                "r_1" to "Distance from the center of mass M to m (m)",
                "r_2" to "Distance from the center of mass M to m (m)",
            ),
            calculation = { v ->
                Calculate.conservationOfGravEnergy(
                    massObject = v["m"],
                    massBody = v["M"],
                    velocity1 = v["v_1"],
                    velocity2 = v["v_2"],
                    distance1 = v["r_1"],
                    distance2 = v["r_2"],
                )
            },
        ),
        Equation(
            name = "Escape velocity",
            formula = "v_{esc} = √(2GM/R)",
            variables = mapOf(
                "v_{esc}" to "Escape velocity (m/s)",
                "G" to "Newton's gravitational constant:  6.674 × 10⁻¹¹ m³ kg⁻¹ s⁻² (constant)",
                "M" to "Mass of the stellar body (kg)",
                "R" to "Radius of the stellar body (m)",
            ),
            calculation = { v ->
                Calculate.escapeVelocity(
                    escapeVel = v["v_{esc}"],
                    massBody = v["M"],
                    radius = v["R"],
                )
            },
        ),
        Equation(
            name = "Orbital speed",
            formula = "V_{orbit} = √(GM/r)",
            variables = mapOf(
                "v_{orb}" to "Escape velocity (m/s)",
                "G" to "Newton's gravitational constant:  6.674 × 10⁻¹¹ m³ kg⁻¹ s⁻² (constant)",
                "M" to "Mass of the stellar body (kg)",
                "r" to "Altitude of the orbiting body (m)",
            ),
            calculation = { v ->
                Calculate.orbitalVelocity(
                    orbitalVel = v["v_{orb}"],
                    massBody = v["M"],
                    distance = v["r"],
                )
            },
        ),
        Equation(
            name = "Orbital period",
            formula = "T = 2π√(r^3/GM)",
            variables = mapOf(
                "T" to "Orbital period (s)",
                "r" to "Altitude of orbiting body (m)",
                "G" to "Newton's gravitational constant:  6.674 × 10⁻¹¹ m³ kg⁻¹ s⁻² (constant)",
                "M" to "Mass of the stellar body (kg)",
            ),
            calculation = { v ->
                Calculate.orbitalPeriod(
                    period = v["T"],
                    distance = v["r"],
                    massBody = v["M"],
                )
            },
        ),
        Equation(
            name = "Energy in circular orbit",
            formula = "E = K + U = -GmM/2r",
            variables = mapOf(
                "E" to "Total energy of the systen (J)",
                "r" to "Distance between the center of mass of object M and m (m)",
                "G" to "Newton's gravitational constant:  6.674 × 10⁻¹¹ m³ kg⁻¹ s⁻² (constant)",
                "M" to "Mass of the stellar body (kg)",
                "m" to "Mass of the orbiting body (kg)",
            ),
        ),
        Equation(
            name = "Orbital equation",
            formula = "α/r = 1 + ecosθ",
            variables = mapOf(
                "α" to "Semi-latus rectum of the orbit",
                "e" to "Eccentricity of the orbit",
                "r" to "Distance from focus to object (m)",
                "θ" to "Angle from periapsis (rads)",
            ),
            calculation = { v ->
                Calculate.orbitalEquation(
                    semiLatusRectum = v["α"],
                    eccentricity = v["e"],
                    distance = v["r"],
                    theta = v["θ"],
                )
            },
        ),
        Equation(
            name = "Kepler's third law",
            formula = "T^2 = (4π^2/GM)a^3",
            variables = mapOf(
                "T" to "The orbital period of the stellar body (s)",
                "a" to "Semi-major axis of the orbit",
                "G" to "Newton's gravitational constant:  6.674 × 10⁻¹¹ m³ kg⁻¹ s⁻² (constant)",
                "M" to "Mass of the stellar body (kg)",
            ),
            calculation = { v ->
                Calculate.keplersThirdLaw(
                    period = v["T"],
                    semiMajorAxis = v["a"],
                    massBody = v["M"],
                )
            },
        ),
        Equation(
            name = "Schwarzschild radius",
            formula = "R_S = 2GM/c^2",
            variables = mapOf(
                "R_S" to "Schwarzschild radius (m)",
                "G" to "Newton's gravitational constant:  6.674 × 10⁻¹¹ m³ kg⁻¹ s⁻² (constant)",
                "M" to "Mass of the stellar body (kg)",
                "c" to "Speed of light: 2.998 × 10⁸ m/s (constant) ",
            ),
            calculation = { v ->
                Calculate.schwarzschildRadius(
                    schwarzRadius = v["R_S"],
                    massBody = v["M"],
                )
            },
        ),
    )

    override val definitions: List<Definition> = listOf(
        Definition("action-at-a-distance force", "type of force exerted without physical contact"),
        Definition("aphelion", "farthest point from the Sun of an orbiting body; the corresponding term for the Moon's farthest point from Earth is the apogee"),
        Definition("apparent weight", "reading of the weight of an object on a scale that does not account for acceleration"),
        Definition("black hole", "mass that becomes so dense, that it collapses in on itself, creating a singularity at the center surrounded by an event horizon"),
        Definition("escape velocity", "initial velocity an object needs to escape the gravitational pull of another; it is more accurately defined as the velocity of an object with zero total mechanical energy"),
        Definition("event horizon", "location of the Schwarzschild radius and is the location near a black hole from within which no object, even light, can escape"),
        Definition("gravitational field", "vector field that surrounds the mass creating the field; the field is represented by field lines, in which the direction of the field is tangent to the lines, and the magnitude (or field strength) is inversely proportional to the spacing of the lines; other masses respond to this field"),
        Definition("gravitationally bound", "two objects are gravitationally bound if their orbits are closed; gravitationally bound systems have a negative total mechanical energy"),
        Definition("Kepler's first law", "law stating that every planet moves along an ellipse, with the Sun located at a focus of the ellipse"),
        Definition("Kepler's second law", "law stating that a planet sweeps out equal areas in equal times, meaning it has a constant areal velocity"),
        Definition("Kepler's third law", "law stating that the square of the period is proportional to the cube of the semi-major axis of the orbit"),
        Definition("neap tide", "low tide created when the Moon and the Sun form a right triangle with Earth"),
        Definition("neutron star", "most compact object known—outside of a black hole itself"),
        Definition("Newton's law of gravitation", "every mass attracts every other mass with a force proportional to the product of their masses, inversely proportional to the square of the distance between them, and with direction along the line connecting the center of mass of each"),
        Definition("non-Euclidean geometry", "geometry of curved space, describing the relationships among angles and lines on the surface of a sphere, hyperboloid, etc."),
        Definition("orbital period", "time required for a satellite to complete one orbit"),
        Definition("orbital speed", "speed of a satellite in a circular orbit; it can also be used for the instantaneous speed for noncircular orbits in which the speed is not constant"),
        Definition("perihelion", "point of closest approach to the Sun of an orbiting body; the corresponding term for the Moon's closest approach to Earth is the perigee"),
        Definition("principle of equivalence", "part of the general theory of relativity, it states that there is no difference between free fall and being weightless, or a uniform gravitational field and uniform acceleration"),
        Definition("Schwarzschild radius", "critical radius (RS) such that if a mass were compressed to the extent that its radius becomes less than the Schwarzschild radius, then the mass will collapse to a singularity, and anything that passes inside that radius cannot escape"),
        Definition("space-time", "concept of space-time is that time is essentially another coordinate that is treated the same way as any individual spatial coordinate; in the equations that represent both special and general relativity, time appears in the same context as do the spatial coordinates"),
        Definition("spring tide", "high tide created when the Moon, the Sun, and Earth are along one line"),
        Definition("theory of general relativity", "Einstein's theory for gravitation and accelerated reference frames; in this theory, gravitation is the result of mass and energy distorting the space-time around it; it is also often referred to as Einstein's theory of gravity"),
        Definition("tidal force", "difference between the gravitational force at the center of a body and that at any other location on the body; the tidal force stretches the body"),
        Definition("universal gravitational constant", "constant representing the strength of the gravitational force, that is believed to be the same throughout the universe"),
    )

    /**
     * Holds the calculation functions for Chapter 13 — the twin of the nested
     * Python `class Calculate`.
     */
    object Calculate {

        /** Newton's gravitational constant [m³ kg⁻¹ s⁻²]. */
        const val G: Double = 6.674e-11

        /** Speed of light in a vacuum [m/s]. */
        const val C: Double = 2.998e8

        /*
         * Error messages reproduced VERBATIM from the Python source. The Python
         * strings were written with backslash line-continuations, which embed the
         * next line's leading indentation into the string — hence the explicit
         * `" ".repeat(n)` whitespace runs (which differ per call site).
         */
        private val MSG_MASSIVE_OBJECTS_21 =
            "We are operating with massive objects." + " ".repeat(21) +
                "Make sure all objects have a mass greater than zero."
        private val MSG_MASSIVE_OBJECTS_17 =
            "We are operating with massive objects." + " ".repeat(17) +
                "Make sure all objects have a mass greater than zero."
        private const val MSG_DISTANCE_NEGATIVE = "Distance cannot be a negative value."
        private val MSG_DISTANCE_LE_ZERO =
            "Distance cannot be a value that is" + " ".repeat(21) + "less than or equal to zero."
        private val MSG_DISTANCE_TWO_BODIES =
            "Distance between the two bodies must be" + " ".repeat(17) + "greater than zero."
        private val MSG_MASS_CANCELS =
            "Mass of orbiting body cancels out and" + " ".repeat(21) + "cannot be determined."
        private const val MSG_RADIUS_GT_ZERO = "Radius of stellar body must be greater than zero."
        private val MSG_NEG_RADICAND_24 =
            "Negative radicand yields a imaginary number." + " ".repeat(24) + "Check your values."
        private val MSG_NEG_RADICAND_25 =
            "Negative radicand yields a imaginary number." + " ".repeat(25) + "Check your values."
        private val MSG_NEG_SQRT =
            "Negative square root yields a imaginary number." + " ".repeat(24) + "Check your values."
        private const val MSG_MASS_NEGATIVE = "Mass cannot be negative. Check your values."
        private val MSG_DISTANCE_NEG_CHECK =
            "Distance cannot be negative." + " ".repeat(25) + "Check your values."
        private const val MSG_DIV_ZERO = "Division by zero is undefined."
        private const val MSG_DIV_ZERO_TYPO = "Divison by zero is undefined." // typo kept from Python
        private const val MSG_DISTANCE_LE_ZERO_SHORT = "Distance cannot be less than or equal to zero."

        /** Rounds to 4 decimal places, matching Chapter3's helper. */
        private fun round4(value: Double): Double = roundResult(value)

        /**
         * F = G·m₁·m₂/r². Pass every value except the unknown; leave it `null`.
         */
        fun lawOfGravitation(
            force12: Double? = null,
            mass1: Double? = null,
            mass2: Double? = null,
            distance: Double? = null,
        ): Double {
            if ((mass1 != null && mass1 <= 0.0) || (mass2 != null && mass2 <= 0.0)) {
                throw IllegalArgumentException(MSG_MASSIVE_OBJECTS_21)
            }

            if (distance != null && distance < 0.0) {
                throw IllegalArgumentException(MSG_DISTANCE_NEGATIVE)
            }

            // Solve for m₁ (mass of object 1)
            if (mass1 == null) {
                return round4((force12!! * (distance!! * distance)) / (G * mass2!!))
            }

            // Solve for m₂ (mass of object 2)
            if (mass2 == null) {
                return round4((force12!! * (distance!! * distance)) / (G * mass1))
            }

            // Solve for r (distance between the two bodies)
            if (distance == null) {
                val radicand: Double = (G * mass1 * mass2) / force12!!
                if (radicand < 0) throw IllegalArgumentException(MSG_NEG_RADICAND_24)
                return round4(sqrt(radicand))
            }

            // Solve for F (gravitational force) — the default case
            return round4((G * mass1 * mass2) / (distance * distance))
        }

        /**
         * g = GM/r². Pass every value except the unknown; leave it `null`.
         *
         * Python bug fixed: the original referenced an undefined `radius`
         * variable in two branches; the parameter is `distance`.
         */
        fun gravitationalAcceleration(
            g: Double? = null,
            massBody: Double? = null,
            distance: Double? = null,
        ): Double {
            if (distance != null && distance <= 0.0) {
                throw IllegalArgumentException(MSG_DISTANCE_LE_ZERO)
            }

            // Solve for M (mass of the stellar body)
            if (massBody == null) {
                return round4((g!! * (distance!! * distance)) / G)
            }

            // Solve for r (radius of the stellar body)
            if (distance == null) {
                val radicand: Double = (G * massBody) / g!!
                if (radicand < 0.0) throw IllegalArgumentException(MSG_DISTANCE_NEGATIVE)
                return round4(sqrt(radicand))
            }

            // Solve for g (acceleration due to gravity) — the default case
            return round4((G * massBody) / (distance * distance))
        }

        /**
         * U = -GMm/r. Pass every value except the unknown; leave it `null`.
         */
        fun gravitationalPotential(
            potentialEnergy: Double? = null,
            massBody: Double? = null,
            massObject: Double? = null,
            distance: Double? = null,
        ): Double {
            if (distance != null && distance <= 0.0) {
                throw IllegalArgumentException(MSG_DISTANCE_TWO_BODIES)
            }

            if ((massBody != null && massBody <= 0.0) || (massObject != null && massObject <= 0.0)) {
                throw IllegalArgumentException(MSG_MASSIVE_OBJECTS_17)
            }

            // Solve for M (mass of the stellar body)
            if (massBody == null) {
                val result: Double = -(potentialEnergy!! * distance!!) / (G * massObject!!)
                if (result < 0.0) throw IllegalArgumentException(MSG_MASS_NEGATIVE)
                return round4(result)
            }

            // Solve for m (mass of the orbiting object)
            if (massObject == null) {
                val result: Double = -(potentialEnergy!! * distance!!) / (G * massBody)
                if (result < 0.0) throw IllegalArgumentException(MSG_MASS_NEGATIVE)
                return round4(result)
            }

            // Solve for r (distance between the two bodies)
            if (distance == null) {
                val result: Double = -(G * massBody * massObject) / potentialEnergy!!
                if (result < 0.0) throw IllegalArgumentException(MSG_DISTANCE_NEG_CHECK)
                return round4(result)
            }

            // Solve for U (potential energy) — the default case
            return round4(-(G * massBody * massObject) / distance)
        }

        /**
         * ½mv₁² - GMm/r₁ = ½mv₂² - GMm/r₂. Pass every value except the unknown;
         * leave it `null`.
         *
         * Python bugs fixed: the r₁/r₂ branches computed dimensionally wrong
         * expressions; the v₂ branch was guarded by a duplicated (unreachable)
         * `velocity_1 == None` check. Correct algebra is used here.
         */
        fun conservationOfGravEnergy(
            massObject: Double? = null,
            massBody: Double? = null,
            velocity1: Double? = null,
            velocity2: Double? = null,
            distance1: Double? = null,
            distance2: Double? = null,
        ): Double {
            if ((distance1 != null && distance1 <= 0.0) || (distance2 != null && distance2 <= 0.0)) {
                throw IllegalArgumentException(MSG_DISTANCE_NEGATIVE)
            }

            if ((massObject != null && massObject <= 0.0) || (massBody != null && massBody <= 0.0)) {
                throw IllegalArgumentException(MSG_MASSIVE_OBJECTS_21)
            }

            // Solve for M (mass of the stellar body)
            if (massBody == null) {
                val numerator: Double = 0.5 * ((velocity1!! * velocity1) - (velocity2!! * velocity2))
                val denominator: Double = G * ((-1.0 / distance2!!) + (1.0 / distance1!!))
                if (denominator == 0.0) throw IllegalArgumentException(MSG_DIV_ZERO_TYPO)
                return round4(numerator / denominator)
            }

            // m (mass of the orbiting body) cancels out of the equation
            if (massObject == null) {
                throw IllegalArgumentException(MSG_MASS_CANCELS)
            }

            // Solve for r₂: r₂ = GM / (½v₂² - ½v₁² + GM/r₁)
            if (distance2 == null) {
                if (velocity1 == 0.0 || velocity2 == 0.0) {
                    throw IllegalArgumentException(MSG_DIV_ZERO)
                }
                val denominator: Double =
                    (0.5 * velocity2!! * velocity2) - (0.5 * velocity1!! * velocity1) + ((G * massBody) / distance1!!)
                return round4((G * massBody) / denominator)
            }

            // Solve for r₁: r₁ = GM / (½v₁² - ½v₂² + GM/r₂)
            if (distance1 == null) {
                if (velocity1 == 0.0 || velocity2 == 0.0) {
                    throw IllegalArgumentException(MSG_DIV_ZERO)
                }
                val denominator: Double =
                    (0.5 * velocity1!! * velocity1) - (0.5 * velocity2!! * velocity2) + ((G * massBody) / distance2)
                return round4((G * massBody) / denominator)
            }

            // Solve for v₁: v₁ = √(v₂² + 2GM/r₁ - 2GM/r₂)
            if (velocity1 == null) {
                val term1: Double = 0.5 * massObject * (velocity2!! * velocity2)
                val term2: Double = (G * massBody * massObject) / distance1
                val term3: Double = (G * massBody * massObject) / distance2
                val coeff: Double = 2.0 / massObject

                val radicand: Double = coeff * (term1 + term2 - term3)
                if (radicand < 0.0) throw IllegalArgumentException(MSG_NEG_RADICAND_25)
                return round4(sqrt(radicand))
            }

            // Solve for v₂: v₂ = √(v₁² - 2GM/r₁ + 2GM/r₂)
            if (velocity2 == null) {
                val term1: Double = 0.5 * massObject * (velocity1 * velocity1)
                val term2: Double = (G * massBody * massObject) / distance1
                val term3: Double = (G * massBody * massObject) / distance2
                val coeff: Double = 2.0 / massObject

                val radicand: Double = coeff * (term1 - term2 + term3)
                if (radicand < 0.0) throw IllegalArgumentException(MSG_NEG_RADICAND_25)
                return round4(sqrt(radicand))
            }

            // All values supplied — nothing to solve for (matches the Python fallthrough).
            return 0.0
        }

        /**
         * v(esc) = √(2GM/R). Pass every value except the unknown; leave it `null`.
         *
         * Python bugs fixed: the M and R branches used `sqrt(escape_vel)` where
         * the algebra requires `escape_vel²`.
         */
        fun escapeVelocity(
            escapeVel: Double? = null,
            massBody: Double? = null,
            radius: Double? = null,
        ): Double {
            if (massBody != null && massBody <= 0) {
                throw IllegalArgumentException(MSG_MASSIVE_OBJECTS_17)
            }

            if (radius != null && radius <= 0) {
                throw IllegalArgumentException(MSG_RADIUS_GT_ZERO)
            }

            // Solve for M (mass of the stellar body): M = v²R/(2G)
            if (massBody == null) {
                if (escapeVel!! < 0.0) throw IllegalArgumentException(MSG_NEG_SQRT)
                return round4(((escapeVel * escapeVel) * radius!!) / (2.0 * G))
            }

            // Solve for R (radius of the stellar body): R = 2GM/v²
            if (radius == null) {
                if (escapeVel!! < 0.0) throw IllegalArgumentException(MSG_NEG_SQRT)
                if (escapeVel == 0.0) throw IllegalArgumentException(MSG_DIV_ZERO_TYPO)
                return round4((2.0 * G * massBody) / (escapeVel * escapeVel))
            }

            // Solve for v(esc) — the default case
            val radicand: Double = (2.0 * G * massBody) / radius
            if (radicand < 0.0) throw IllegalArgumentException(MSG_NEG_SQRT)
            return round4(sqrt(radicand))
        }

        /**
         * v(orb) = √(GM/r). Pass every value except the unknown; leave it `null`.
         *
         * Python bugs fixed: the distance guard referenced an undefined `radius`
         * variable, and the M and r branches used `sqrt(orbital_vel)` where the
         * algebra requires `orbital_vel²`.
         */
        fun orbitalVelocity(
            orbitalVel: Double? = null,
            massBody: Double? = null,
            distance: Double? = null,
        ): Double {
            if (massBody != null && massBody <= 0) {
                throw IllegalArgumentException(MSG_MASSIVE_OBJECTS_17)
            }

            if (distance != null && distance <= 0) {
                throw IllegalArgumentException(MSG_RADIUS_GT_ZERO)
            }

            // Solve for M (mass of the stellar body): M = v²r/G
            if (massBody == null) {
                if (orbitalVel!! < 0.0) throw IllegalArgumentException(MSG_NEG_SQRT)
                return round4(((orbitalVel * orbitalVel) * distance!!) / G)
            }

            // Solve for r (orbital distance): r = GM/v²
            if (distance == null) {
                if (orbitalVel!! < 0.0) throw IllegalArgumentException(MSG_NEG_SQRT)
                if (orbitalVel == 0.0) throw IllegalArgumentException(MSG_DIV_ZERO_TYPO)
                return round4((G * massBody) / (orbitalVel * orbitalVel))
            }

            // Solve for v(orb) — the default case
            val radicand: Double = (G * massBody) / distance
            if (radicand < 0.0) throw IllegalArgumentException(MSG_NEG_SQRT)
            return round4(sqrt(radicand))
        }

        /**
         * T = 2π√(r³/GM). Pass every value except the unknown; leave it `null`.
         *
         * Python bugs fixed: `pi` was never imported (NameError on every call),
         * and the r branch wrote `period / 2 * pi` (precedence) instead of
         * `period / (2π)`.
         */
        fun orbitalPeriod(
            period: Double? = null,
            distance: Double? = null,
            massBody: Double? = null,
        ): Double {
            if (massBody != null && massBody <= 0.0) {
                throw IllegalArgumentException(MSG_MASSIVE_OBJECTS_17)
            }

            // Solve for M (mass of the stellar body): M = (2π/T)²·r³/G
            if (massBody == null) {
                if (period == 0.0) throw IllegalArgumentException(MSG_DIV_ZERO)
                val factor: Double = (2.0 * PI) / period!!
                return round4((factor * factor) * (distance!! * distance * distance) / G)
            }

            // Solve for r (orbital distance): r = ∛((T/2π)²·GM)
            if (distance == null) {
                val factor: Double = period!! / (2.0 * PI)
                val radicand: Double = (factor * factor) * (G * massBody)
                return round4(cbrt(radicand))
            }

            // Solve for T (orbital period) — the default case
            val radicand: Double = (distance * distance * distance) / (G * massBody)
            return round4(2.0 * PI * sqrt(radicand))
        }

        /**
         * α/r = 1 + e·cosθ. Pass every value except the unknown; leave it `null`.
         *
         * Note: as in the Python source, θ is accepted and returned in DEGREES
         * (it is converted to radians internally), even though the equation's
         * variable description says "(rads)".
         */
        fun orbitalEquation(
            semiLatusRectum: Double? = null,
            eccentricity: Double? = null,
            distance: Double? = null,
            theta: Double? = null,
        ): Double {
            val thetaRadians: Double? = theta?.times(PI / 180.0)

            if (distance != null && distance <= 0.0) {
                throw IllegalArgumentException(MSG_DISTANCE_LE_ZERO_SHORT)
            }

            // Solve for α (semi-latus rectum)
            if (semiLatusRectum == null) {
                return round4(distance!! * (1 + eccentricity!! * cos(thetaRadians!!)))
            }

            // Solve for e (eccentricity)
            if (eccentricity == null) {
                if (cos(thetaRadians!!) == 0.0) throw IllegalArgumentException(MSG_DIV_ZERO)
                return round4(((semiLatusRectum / distance!!) - 1) / cos(thetaRadians))
            }

            // Solve for r (distance from focus)
            if (distance == null) {
                val denominator: Double = 1 + eccentricity * cos(thetaRadians!!)
                if (denominator == 0.0) throw IllegalArgumentException(MSG_DIV_ZERO_TYPO)
                return round4(semiLatusRectum / denominator)
            }

            // Solve for θ (angle from periapsis, in degrees)
            if (theta == null) {
                if (eccentricity == 0.0) throw IllegalArgumentException(MSG_DIV_ZERO_TYPO)
                val argument: Double = ((semiLatusRectum / distance) - 1) / eccentricity
                return round4(acos(argument) * 180.0 / PI)
            }

            // All values supplied — nothing to solve for (matches the Python fallthrough).
            return 0.0
        }

        /**
         * T² = (4π²/GM)a³. Pass every value except the unknown; leave it `null`.
         *
         * Python bugs fixed: `pi` was never imported, `period_sq` was computed
         * from an undefined `orbital_period` variable, and the T branch used
         * `cbrt` where the algebra requires `sqrt`.
         */
        fun keplersThirdLaw(
            period: Double? = null,
            semiMajorAxis: Double? = null,
            massBody: Double? = null,
        ): Double {
            val fourPiSquared: Double = 4.0 * (PI * PI)

            val periodSq: Double? = period?.times(period)

            if (massBody != null && massBody <= 0.0) {
                throw IllegalArgumentException(MSG_MASSIVE_OBJECTS_17)
            }

            // Solve for a (semi-major axis): a = ∛(T²·GM/4π²)
            if (semiMajorAxis == null) {
                val argument: Double = periodSq!! * (G * massBody!!) / fourPiSquared
                return round4(cbrt(argument))
            }

            // Solve for M (mass of the stellar body): M = 4π²a³/(G·T²)
            if (massBody == null) {
                if (periodSq == 0.0) throw IllegalArgumentException(MSG_DIV_ZERO_TYPO)
                return round4((fourPiSquared * semiMajorAxis * semiMajorAxis * semiMajorAxis) / (G * periodSq!!))
            }

            // Solve for T (orbital period): T = √(a³·4π²/(GM)) — the default case
            val argument: Double =
                (semiMajorAxis * semiMajorAxis * semiMajorAxis) * (fourPiSquared / (G * massBody))
            return round4(sqrt(argument))
        }

        /**
         * R(S) = 2GM/c². Pass every value except the unknown; leave it `null`.
         */
        fun schwarzschildRadius(
            schwarzRadius: Double? = null,
            massBody: Double? = null,
        ): Double {
            if (massBody != null && massBody <= 0.0) {
                throw IllegalArgumentException(MSG_MASSIVE_OBJECTS_17)
            }

            // Solve for M (mass of the stellar body): M = R(S)·c²/(2G)
            if (massBody == null) {
                return round4(schwarzRadius!! * ((C * C) / (2.0 * G)))
            }

            // Solve for R(S) — the default case
            return round4((2.0 * G * massBody) / (C * C))
        }
    }
}
