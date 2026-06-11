package io.github.claudiormalvino.physicalc.physics

import kotlin.math.round

/** Chapter 12 — Static equilibrium and the elasticity of materials. */
class Chapter12 : PhysicsChapter(
    title = "Ch.12 - Static Equilibrium and Elasticity",
    description = "Conditions for equilibrium and the elasticity of materials.",
) {

    override val equations: List<Equation> = listOf(
        Equation(
            name = "First Equilibrium Condition",
            formula = "∑F_k = 0",
        ),
        Equation(
            name = "Second Equilibrium Condition",
            formula = "∑τ_k = 0",
        ),
        Equation(
            name = "Linear relation between stress and strain",
            formula = "σ = E·ε",
            variables = mapOf(
                "σ" to "Stress (Pa)",
                "E" to "Elastic modulus (Pa)",
                "ε" to "Strain (dimensionless)",
            ),
        ),
        Equation(
            name = "Young's modulus",
            formula = "Y = (F/A) × (L_0/ΔL)",
            variables = mapOf(
                "Y" to "Elastic modulus for tensile stress (Pa)",
                "F" to "Force (N)",
                "A" to "Cross sectional area (m)",
                "L₀" to "Initial length of the object (m)",
                "ΔL" to "Change of length after deformation (m)",
            ),
            calculation = { v ->
                Calculate.youngModulus(
                    youngMod = v["Y"],
                    force = v["F"],
                    crossSection = v["A"],
                    initLength = v["L₀"],
                    deltaLength = v["ΔL"],
                )
            },
        ),
        Equation(
            name = "Bulk modulus",
            formula = "B = −Δp × (V_0/ΔV)",
            variables = mapOf(
                "B" to "Elastic modulus for bulk stress (Pa)",
                "Δp" to "Change in pressure (Pa)",
                "V₀" to "Initial volume (m³)",
                "ΔV" to "Change in volume (m³)",
            ),
            calculation = { v ->
                Calculate.bulkModulus(
                    bulkMod = v["B"],
                    deltaPressure = v["Δp"],
                    initVolume = v["V₀"],
                    deltaVolume = v["ΔV"],
                )
            },
        ),
        Equation(
            name = "Shear modulus",
            formula = "S = (F/A) × (L_0/Δx)",
            variables = mapOf(
                "S" to "Elastic modulus for shear stress (Pa)",
                "F" to "Force (N)",
                "A" to "Cross sectional area (m)",
                "L₀" to "Initial length of the object (m)",
                "Δx" to "a gradual shift of layers in the direction tangent to the acting forces.",
            ),
            calculation = { v ->
                Calculate.shearModulus(
                    shearMod = v["S"],
                    force = v["F"],
                    crossSection = v["A"],
                    initLength = v["L₀"],
                    deltaLayers = v["Δx"],
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
            "angular momentum is conserved, that is, the initial angular momentum is equal to the final angular momentum when no external torque is applied to the system precession",
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

    /** Calculation functions for Chapter 12. */
    object Calculate {

        private fun round4(value: Double): Double = roundResult(value)

        /** Y = (F/A) × (L₀/ΔL). Pass null for the unknown. */
        fun youngModulus(
            youngMod: Double? = null,
            force: Double? = null,
            crossSection: Double? = null,
            initLength: Double? = null,
            deltaLength: Double? = null,
        ): Double {
            if ((initLength != null && initLength <= 0.0) ||
                (crossSection != null && crossSection < 0.0)
            ) {
                throw IllegalArgumentException(
                    "The initial length and cross section cannot be less than or equal to zero."
                )
            }

            if (deltaLength != null && deltaLength < 0.0) {
                throw IllegalArgumentException("The change in length cannot be less than zero.")
            }

            // Solve for F
            if (force == null) {
                return round4((deltaLength!! * crossSection!! * youngMod!!) / initLength!!)
            }

            // Solve for A
            if (crossSection == null) {
                if (youngMod == 0.0 || deltaLength == 0.0) {
                    throw IllegalArgumentException("Division by zero is undefined.")
                }
                return round4((initLength!! * force) / (youngMod!! * deltaLength!!))
            }

            // Solve for L₀
            if (initLength == null) {
                if (force == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                return round4((youngMod!! * deltaLength!! * crossSection) / force)
            }

            // Solve for ΔL
            if (deltaLength == null) {
                if (youngMod == 0.0) throw IllegalArgumentException("Divison by zero is undefined.")
                return round4((initLength * force) / (youngMod!! * crossSection))
            }

            // Solve for Y
            return round4((force / crossSection) * (initLength / deltaLength))
        }

        /** B = −Δp × (V₀/ΔV). Pass null for the unknown. */
        fun bulkModulus(
            bulkMod: Double? = null,
            deltaPressure: Double? = null,
            initVolume: Double? = null,
            deltaVolume: Double? = null,
        ): Double {
            if (initVolume != null && initVolume <= 0.0) {
                throw IllegalArgumentException("The volume of the object must be greater than zero.")
            }

            // Solve for Δp
            if (deltaPressure == null) {
                return round4(-(bulkMod!! * deltaVolume!!) / initVolume!!)
            }

            // Solve for V₀
            // Corrected from upstream: numerator is −B·ΔV, not −B·Δp.
            if (initVolume == null) {
                if (deltaPressure == 0.0) {
                    throw IllegalArgumentException("Division by zero is undefined.")
                }
                return round4(-(bulkMod!! * deltaVolume!!) / deltaPressure)
            }

            // Solve for ΔV
            if (deltaVolume == null) {
                if (bulkMod == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                return round4((-deltaPressure * initVolume) / bulkMod!!)
            }

            // Solve for B
            if (deltaVolume == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
            return round4((-deltaPressure * initVolume) / deltaVolume)
        }

        /** S = (F/A) × (L₀/Δx). Pass null for the unknown. */
        fun shearModulus(
            shearMod: Double? = null,
            force: Double? = null,
            crossSection: Double? = null,
            initLength: Double? = null,
            deltaLayers: Double? = null,
        ): Double {
            if ((initLength != null && initLength <= 0.0) ||
                (crossSection != null && crossSection < 0.0)
            ) {
                throw IllegalArgumentException(
                    "The initial length and cross section cannot be less than or equal to zero."
                )
            }

            if (deltaLayers != null && deltaLayers < 0.0) {
                throw IllegalArgumentException("The change in length cannot be less than zero.")
            }

            // Solve for F
            if (force == null) {
                return round4((deltaLayers!! * crossSection!! * shearMod!!) / initLength!!)
            }

            // Solve for A
            if (crossSection == null) {
                if (shearMod == 0.0 || deltaLayers == 0.0) {
                    throw IllegalArgumentException("Division by zero is undefined.")
                }
                return round4((initLength!! * force) / (shearMod!! * deltaLayers!!))
            }

            // Solve for L₀
            if (initLength == null) {
                if (force == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                return round4((shearMod!! * deltaLayers!! * crossSection) / force)
            }

            // Solve for Δx
            if (deltaLayers == null) {
                if (shearMod == 0.0) throw IllegalArgumentException("Divison by zero is undefined.")
                return round4((initLength * force) / (shearMod!! * crossSection))
            }

            // Solve for S
            return round4((force / crossSection) * (initLength / deltaLayers))
        }
    }
}
