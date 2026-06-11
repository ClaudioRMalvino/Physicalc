package io.github.claudiormalvino.physicalc.physics

import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.round

/**
 * Chapter on Newton's Laws of Motion — the Kotlin twin of your Python `chapter5.py`.
 */
class Chapter5 : PhysicsChapter(
    title = "Ch.5 - Newton's Laws of Motion",
    description = "Study of forces and Newton's three laws of motion.",
) {

    override val equations: List<Equation> = listOf(
        Equation(
            name = "Net external force",
            formula = "F_{net} = ∑F",
            variables = mapOf(
                "F" to "Force vector, (N)",
                "F_{net}" to "Sum of all force vectors (N)",
            ),
        ),
        Equation(
            name = "Newton's first law",
            formula = "v = constant ⟺ F_{net} = 0",
            variables = mapOf(
                "v" to "Velocity (m/s)",
                "F_{net}" to "Sum of all force vectors (N)",
            ),
        ),
        Equation(
            name = "Newton's second law",
            formula = "F_{net} = ∑ma",
            variables = mapOf(
                "F_{net}" to "Sum of all force vectors",
                "m" to "Mass of the object (kg)",
                "a" to "Acceleration vector (m/s²)",
            ),
        ),
        Equation(
            name = "Newton's second law, component form",
            formula = "∑F_x = ma_x, ∑F_y = ma_y, and ∑F_z = ma_z",
            variables = mapOf(
                "m" to "Mass of the object (kg)",
                "aₓ" to "X component of acceleration (m/s²)",
                "aᵧ" to "y component of acceleration (m/s²)",
                "az" to "z component of acceleration (m/s²)",
            ),
        ),
        Equation(
            name = "Newton's second law, momentum form",
            formula = "F_{net} = dp/dt = d(mv)/dt",
            variables = mapOf(
                "F_{net}" to "Sum of all force vectors (N)",
                "p" to "Time dependent momentum vector (kg⋅m/s)",
                "d/dt" to "First order derivative with respect to time",
            ),
        ),
        Equation(
            name = "Weight",
            formula = "w = mg",
            variables = mapOf(
                "w" to "Weight (N)",
                "m" to "Mass (kg)",
            ),
        ),
        Equation(
            name = "Newton's third law",
            formula = "F_{AB} = -F_{BA}",
            variables = mapOf(
                "F_{AB}" to "Force acted upon B by A (N)",
                "F_{BA}" to "Force acted upon A by B (N)",
            ),
        ),
        Equation(
            name = "Normal force (resting)",
            formula = "N = mgcosθ",
            variables = mapOf(
                "N" to "Normal force (N)",
                "m" to "Mass of the object (kg)",
                "θ" to "Angle between the normal vector and gravitational vector [radians]",
            ),
            calculation = { v ->
                Calculate.normalForce(
                    normalF = v["N"],
                    mass = v["m"],
                    theta = v["θ"],
                )
            },
        ),
        Equation(
            name = "Hooke's Law",
            formula = "F = -kx",
            variables = mapOf(
                "F" to "Restorative force (N)",
                "k" to "Spring constant (kg/s²)",
                "x" to "Distance from point of equilibrium",
            ),
            calculation = { v ->
                Calculate.hookesLaw(
                    force = v["F"],
                    springConst = v["k"],
                    displacement = v["x"],
                )
            },
        ),
    )

    override val definitions: List<Definition> = listOf(
        Definition("dynamics", "study of how forces affect the motion of objects and systems"),
        Definition("external force", "force acting on an object or system that originates outside of the object or system"),
        Definition("force", "push or pull on an object with a specific magnitude and direction; can be represented by vectors or expressed as a multiple of a standard force"),
        Definition("free fall", "situation in which the only force acting on an object is gravity"),
        Definition("free-body diagram", "sketch showing all external forces acting on an object or system; the system is represented by a single isolated point, and the forces are represented by vectors extending outward from that point"),
        Definition("Hooke's law", "in a spring, a restoring force proportional to and in the opposite direction of the imposed displacement"),
        Definition("inertia", "ability of an object to resist changes in its motion"),
        Definition("inertial reference frame", "reference frame moving at constant velocity relative to an inertial frame is also inertial; a reference frame accelerating relative to an inertial frame is not inertial"),
        Definition("law of inertia", "see Newton's first law of motion"),
        Definition("net external force", "vector sum of all external forces acting on an object or system; causes a mass to accelerate"),
        Definition("newton", "SI unit of force; 1 N is the force needed to accelerate an object with a mass of 1 kg at a rate of 1m/s²"),
        Definition("Newton's first law of motion", "body at rest remains at rest or, if in motion, remains in motion at constant velocity unless acted on by a net external force; also known as the law of inertia"),
        Definition("Newton's second law of motion", "acceleration of a system is directly proportional to and in the same direction as the net external force acting on the system and is inversely proportional to its mass"),
        Definition("Newton's third law of motion", "whenever one body exerts a force on a second body, the first body experiences a force that is equal in magnitude and opposite in direction to the force that it exerts"),
        Definition("normal force", "force supporting the weight of an object, or a load, that is perpendicular to the surface of contact between the load and its support; the surface applies this force to an object to support the weight of the object"),
        Definition("tension", "pulling force that acts along a stretched flexible connector, such as a rope or cable"),
        Definition("thrust", "reaction force that pushes a body forward in response to a backward force"),
        Definition("weight", "force due to gravity acting on an object of mass m"),
    )

    /**
     * Holds the calculation functions for Chapter 5 — the twin of the nested
     * Python `class Calculate`.
     */
    object Calculate {

        /** Gravitational acceleration on Earth [m/s²]. */
        const val G: Double = 9.82

        /** Rounds to 4 decimal places, matching Python's `round(x, 4)`. */
        private fun round4(value: Double): Double = round(value * 10000.0) / 10000.0

        /**
         * N = mg·cosθ. Pass every value except the one you want solved for;
         * leave that one as `null`. `theta` is given in degrees.
         */
        fun normalForce(
            normalF: Double? = null,
            mass: Double? = null,
            theta: Double? = null,
        ): Double {
            if (mass != null && mass < 0) throw IllegalArgumentException("Mass cannot be negative.")

            // Solve for θ (angle), returned in degrees
            if (theta == null) {
                val arg: Double = normalF!! / (mass!! * G)
                return round4(acos(arg) * (180.0 / PI))
            }

            // Converts degrees to radians
            val thetaRadians: Double = theta * (PI / 180.0)

            // Solve for m (mass)
            if (mass == null) {
                if (theta == 90.0 || theta == 270.0) {
                    throw IllegalArgumentException("Division by zero is undefined.")
                }
                val result = normalF!! / (G * cos(thetaRadians))
                if (result < 0) throw IllegalArgumentException("Mass cannot be negative.")
                return round4(result)
            }

            // Solve for N (normal force) — the default case
            return round4(mass * G * cos(thetaRadians))
        }

        /**
         * F = -kx (Hooke's law). Pass every value except the unknown; leave that one `null`.
         */
        fun hookesLaw(
            force: Double? = null,
            springConst: Double? = null,
            displacement: Double? = null,
        ): Double {
            if (springConst != null && springConst < 0) {
                throw IllegalArgumentException("Spring constant (k) cannot be a negative value.")
            }

            // Solve for k (spring constant)
            if (springConst == null) {
                if (displacement == 0.0) throw IllegalArgumentException("Divison by zero is undefined.")

                val result = -force!! / displacement!!
                if (result < 0) {
                    // Message spacing matches the Python line-continuation string verbatim.
                    throw IllegalArgumentException(
                        "Spring constant cannot be negative. " +
                            "                        Consider the relation between the direction " +
                            "                        of displacment and the restorative force."
                    )
                }
                return round4(result)
            }

            // Solve for x (displacement)
            if (displacement == null) {
                if (springConst == 0.0) throw IllegalArgumentException("Divison by zero is undefined.")
                return round4(-force!! / springConst)
            }

            // Solve for F (restorative force) — the default case
            return round4(-springConst * displacement)
        }
    }
}
