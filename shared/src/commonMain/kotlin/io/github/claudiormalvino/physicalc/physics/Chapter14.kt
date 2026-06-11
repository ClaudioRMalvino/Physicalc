package io.github.claudiormalvino.physicalc.physics

import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

/**
 * Chapter on Fluid Dynamics — the Kotlin twin of your Python `chapter14.py`.
 */
class Chapter14 : PhysicsChapter(
    title = "Ch.14 - Fluid Dynamics",
    description = "Fluids at rest and in motion: pressure, buoyancy, and flow.",
) {

    override val equations: List<Equation> = listOf(
        Equation(
            name = "Density of a sample at constant density",
            formula = "ρ = m/V",
            variables = mapOf(
                "ρ" to "Density (kg/m³)",
                "m" to "mass (kg)",
                "V" to "Volume (m³)",
            ),
        ),
        Equation(
            name = "Pressure",
            formula = "p = F/A",
            variables = mapOf(
                "p" to "Pressure (N/m²)",
                "F" to "Force (N)",
                "A" to "Area (m²)",
            ),
        ),
        Equation(
            name = "Hydrostatic pressure",
            formula = "p = p_0 + ρgh",
            variables = mapOf(
                "p" to "Pressure at depth (Pa)",
                "p₀" to "Pressure at atmosphere (Pa)",
                "ρ" to "Density of the fluid (kg/m³)",
                "h" to "Depth (m)",
            ),
            calculation = { v ->
                Calculate.hydrostaticPressure(
                    pressure = v["p"],
                    pressureAtm = v["p₀"],
                    density = v["ρ"],
                    depth = v["h"],
                )
            },
        ),
        Equation(
            name = "Pressure gradiant in a fluid of constant density",
            formula = "dp/dy = -ρg",
            variables = mapOf(
                "dp/dy" to "Rate of change of pressure with respect to height",
                "ρ" to "Density of the fluid (kg/m³)",
            ),
        ),
        Equation(
            name = "Absolute pressure",
            formula = "p_{abs} = p_{g} + p_{atm}",
            variables = mapOf(
                "p_{abs}" to "Absolute pressure (Pa)",
                "p_g" to "Gauge pressure (Pa)",
                "p_{atm}" to "Atmospheric pressure (Pa)",
            ),
        ),
        Equation(
            name = "Pascal's principle",
            formula = "F_1/A_1 = F_2/A_2",
            variables = mapOf(
                "F_1" to "Force applied by piston 1 (N)",
                "A_1" to "Area of piston 1 (m²)",
                "F_2" to "Force applied by piston 2 (N)",
                "A_2" to "Area of piston 2 (m²)",
            ),
            calculation = { v ->
                Calculate.pascalsPrinciple(
                    force1 = v["F_1"],
                    area1 = v["A_1"],
                    force2 = v["F_2"],
                    area2 = v["A_2"],
                )
            },
        ),
        Equation(
            name = "Volume flow rate",
            formula = "Q = dV/dt",
            variables = mapOf(
                "Q" to "Flow rate",
                "dV/dt" to "Rate of change of the volume with respect to time",
            ),
        ),
        Equation(
            name = "Continuity equation (constant density)",
            formula = "A_1v_1 = A_2v_2",
            variables = mapOf(
                "A_1" to "Area of nozzle 1 (m²)",
                "v_1" to "Velocity of fluid in nozzle 1 (m/s)",
                "A_2" to "Area of nozzle 2 (m²)",
                "v_2" to "Velocity of fluid in nozzle 2 (m/s)",
            ),
            calculation = { v ->
                Calculate.continuityConstDensity(
                    area1 = v["A_1"],
                    velocity1 = v["v_1"],
                    area2 = v["A_2"],
                    velocity2 = v["v_2"],
                )
            },
        ),
        Equation(
            name = "Continuity equation (general form)",
            formula = "ρ_1A_1v_1 = ρ_2A_2v_2",
            variables = mapOf(
                "ρ_1" to "Density of the fluid in nozzle 1 (kg/m³)",
                "A_1" to "Area of nozzle 1 (m²)",
                "v_1" to "Velocity of fluid in nozzle 1 (m/s)",
                "ρ_2" to "Density of the fluid in nozzle 2 (kg/m³)",
                "A_2" to "Area of nozzle 2 (m²)",
                "v_2" to "Velocity of fluid in nozzle 2 (m/s)",
            ),
            calculation = { v ->
                Calculate.continuityConstGeneral(
                    density1 = v["ρ_1"],
                    area1 = v["A_1"],
                    velocity1 = v["v_1"],
                    density2 = v["ρ_2"],
                    area2 = v["A_2"],
                    velocity2 = v["v_2"],
                )
            },
        ),
        Equation(
            name = "Bernoulli's equation",
            formula = "p_1 + ½ρv_1^2 + ρgy_1 = p_2 + ½ρv_2^2 + ρgy_2",
            variables = mapOf(
                "ρ" to "Density of the fluid (kg/m³)",
                "p_1" to "Pressure of the fluid in moment 1 (Pa)",
                "v_1" to "Velocity of the fluid in moment 1 (m/s)",
                "y_1" to "Height of the fluid in moment 1 (m)",
                "p_2" to "Pressure of the fluid in moment 1 (Pa)",
                "v_2" to "Velocity of the fluid in moment 1 (m/s)",
                "y_2" to "Height of the fluid in moment 1 (m)",
            ),
            calculation = { v ->
                Calculate.bernoullisEquation(
                    density = v["ρ"],
                    pressure1 = v["p_1"],
                    velocity1 = v["v_1"],
                    height1 = v["y_1"],
                    pressure2 = v["p_2"],
                    velocity2 = v["v_2"],
                    height2 = v["y_2"],
                )
            },
        ),
        Equation(
            name = "Viscocity",
            formula = "η = FL/(vA)",
            variables = mapOf(
                "η" to "Viscocity (Pa⋅s)",
                "F" to "Force (N)",
                "L" to "Distance between plates (m)",
                "A" to "Area of the plates (m²)",
                "v" to "Velocity of the fluid (m/s)",
            ),
            calculation = { v ->
                Calculate.viscocity(
                    viscocity = v["η"],
                    force = v["F"],
                    distance = v["L"],
                    area = v["A"],
                    velocity = v["v"],
                )
            },
        ),
        Equation(
            name = "Poiseuille’s law for resistance",
            formula = "R = 8ηl/(πr^4)",
            variables = mapOf(
                "R" to "Resistance to laminar flow",
                "η" to "Viscocity (Pa⋅s)",
                "l" to "Length of the tube (m)",
                "r" to "Radius of the tube (m)",
            ),
            calculation = { v ->
                Calculate.poiseuillesLawResistance(
                    resistance = v["R"],
                    viscocity = v["η"],
                    length = v["l"],
                    radius = v["r"],
                )
            },
        ),
        Equation(
            name = "Poiseuille’s law",
            formula = "Q = (p_1 - p_2)πr^4/(8ηl)",
            variables = mapOf(
                "Q" to "Flow rate",
                "η" to "Viscocity (Pa⋅s)",
                "l" to "Length of the tube (m)",
                "r" to "Radius of the tube (m)",
                "p_1" to "Pressure at point 1 (Pa)",
                "p_2" to "Pressure at point 2 (Pa)",
            ),
            calculation = { v ->
                Calculate.poiseuillesLaw(
                    flow = v["Q"],
                    viscocity = v["η"],
                    length = v["l"],
                    radius = v["r"],
                    pressure1 = v["p_1"],
                    pressure2 = v["p_2"],
                )
            },
        ),
    )

    override val definitions: List<Definition> = listOf(
        Definition("absolute pressure", "sum of gauge pressure and atmospheric pressure"),
        Definition("Archimedes' principle", "buoyant force on an object equals the weight of the fluid it displaces"),
        Definition("Bernoulli's equation", "equation resulting from applying conservation of energy to an incompressible frictionless fluid: p + ½ρv² + ρgh = constant, throughout the fluid"),
        Definition("Bernoulli's principle", "Bernoulli's equation applied at constant depth: p₁ + ½ρv₁² = p₂ + ½ρv₂²"),
        Definition("buoyant force", "net upward force on any object in any fluid due to the pressure difference at different depths"),
        Definition("density", "mass per unit volume of a substance or object"),
        Definition("flow rate", "abbreviated Q, it is the volume V that flows past a particular point during a time t, or Q = dV/dt"),
        Definition("fluids", "liquids and gases; a fluid is a state of matter that yields to shearing forces"),
        Definition("gauge pressure", "pressure relative to atmospheric pressure"),
        Definition("hydraulic jack", "simple machine that uses cylinders of different diameters to distribute force"),
        Definition("hydrostatic equilibrium", "state at which water is not flowing, or is static"),
        Definition("ideal fluid", "fluid with negligible viscosity"),
        Definition("laminar flow", "type of fluid flow in which layers do not mix"),
        Definition("Pascal's principle", "change in pressure applied to an enclosed fluid is transmitted undiminished to all portions of the fluid walls of its container"),
        Definition("Poiseuille's law", "rate of laminar flow of an incompressible fluid in a tube: Q = (p₁-p₂)πr⁴/8ηl"),
        Definition("Poiseuille's law for resistance", "resistance to laminar flow of an incompressible fluid in a tube: R = 8ηl/πr⁴"),
        Definition("pressure", "force per unit area exerted perpendicular to the area over which the force acts"),
        Definition("Reynolds number", "dimensionless parameter that can reveal whether a particular flow is laminar or turbulent"),
        Definition("specific gravity", "ratio of the density of an object to a fluid (usually water)"),
        Definition("turbulence", "fluid flow in which layers mix together via eddies and swirls"),
        Definition("turbulent flow", "type of fluid flow in which layers mix together via eddies and swirls"),
        Definition("viscosity", "measure of the internal friction in a fluid"),
    )

    /**
     * Holds the calculation functions for Chapter 14 — the twin of your nested
     * Python `class Calculate`.
     */
    object Calculate {

        /** Acceleration due to gravity on Earth [m/s²] (positive magnitude, as in the Python source). */
        const val G: Double = 9.82

        /** Rounds to 4 decimal places, matching Python's `round(x, 4)`. */
        private fun round4(value: Double): Double = round(value * 10000.0) / 10000.0

        /**
         * p = p₀ + ρgh. Pass every value except the one you want solved for;
         * leave that one as `null`.
         */
        fun hydrostaticPressure(
            pressure: Double? = null,
            pressureAtm: Double? = null,
            density: Double? = null,
            depth: Double? = null,
        ): Double {
            if ((pressure != null && pressure < 0.0) || (pressureAtm != null && pressureAtm < 0.0)) {
                throw IllegalArgumentException("Hydrostatic or atmospheric pressure cannot be a negative value.")
            }

            // Solve for p₀ (atmospheric pressure)
            if (pressureAtm == null) {
                return round4(pressure!! - (density!! * G * depth!!))
            }

            // Solve for ρ (fluid density)
            if (density == null) {
                if (depth == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                return round4((pressure!! - pressureAtm) / (G * depth!!))
            }

            // Solve for h (depth)
            if (depth == null) {
                if (density == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                return round4((pressure!! - pressureAtm) / (G * density))
            }

            // Solve for p (pressure at depth) — the default case
            return round4(pressureAtm + (density * G * depth))
        }

        /**
         * F₁/A₁ = F₂/A₂ (Pascal's principle). Pass every value except the unknown;
         * leave that one `null`.
         */
        fun pascalsPrinciple(
            force1: Double? = null,
            area1: Double? = null,
            force2: Double? = null,
            area2: Double? = null,
        ): Double {
            // Solve for F₁ (force on piston 1)
            if (force1 == null) {
                if (area2 == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                return round4((force2!! / area2!!) * area1!!)
            }

            // Solve for A₁ (area of piston 1)
            if (area1 == null) {
                if (force2 == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                return round4((force1 * area2!!) / force2!!)
            }

            // Solve for F₂ (force on piston 2)
            if (force2 == null) {
                if (area1 == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                return round4((force1 / area1) * area2!!)
            }

            // Solve for A₂ (area of piston 2)
            if (area2 == null) {
                if (force1 == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                return round4((force2 * area1) / force1)
            }

            return 0.0
        }

        /**
         * A₁v₁ = A₂v₂ (continuity, constant density). Pass every value except the
         * unknown; leave that one `null`.
         */
        fun continuityConstDensity(
            area1: Double? = null,
            velocity1: Double? = null,
            area2: Double? = null,
            velocity2: Double? = null,
        ): Double {
            if ((area1 != null && area1 <= 0.0) || (area2 != null && area2 <= 0.0)) {
                throw IllegalArgumentException("Area cannot be less than or equal to zero.")
            }

            // Solve for A₁
            if (area1 == null) {
                if (velocity1 == 0.0) throw IllegalArgumentException("Divison by zero is undefined.")
                return round4((area2!! * velocity2!!) / velocity1!!)
            }

            // Solve for v₁
            if (velocity1 == null) {
                return round4((area2!! * velocity2!!) / area1)
            }

            // Solve for A₂
            if (area2 == null) {
                if (velocity2 == 0.0) throw IllegalArgumentException("Divison by zero is undefined.")
                return round4((area1 * velocity1) / velocity2!!)
            }

            // Solve for v₂
            if (velocity2 == null) {
                return round4((area1 * velocity1) / area2)
            }

            return 0.0
        }

        /**
         * ρ₁A₁v₁ = ρ₂A₂v₂ (continuity, general form). Pass every value except the
         * unknown; leave that one `null`.
         */
        fun continuityConstGeneral(
            density1: Double? = null,
            area1: Double? = null,
            velocity1: Double? = null,
            density2: Double? = null,
            area2: Double? = null,
            velocity2: Double? = null,
        ): Double {
            if ((area1 != null && area1 <= 0.0) || (area2 != null && area2 <= 0.0)) {
                throw IllegalArgumentException("Area cannot be less than or equal to zero.")
            }

            if ((density1 != null && density1 <= 0.0) || (density2 != null && density2 <= 0.0)) {
                throw IllegalArgumentException("Density of a fluid cannot be less than or equal to zero.")
            }

            // Solve for ρ₁
            if (density1 == null) {
                if (velocity1 == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                val numerator: Double = density2!! * area2!! * velocity2!!
                val denominator: Double = area1!! * velocity1!!
                return round4(numerator / denominator)
            }

            // Solve for A₁
            if (area1 == null) {
                if (velocity1 == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                val numerator: Double = density2!! * area2!! * velocity2!!
                val denominator: Double = density1 * velocity1!!
                return round4(numerator / denominator)
            }

            // Solve for v₁
            if (velocity1 == null) {
                val numerator: Double = density2!! * area2!! * velocity2!!
                val denominator: Double = density1 * area1
                return round4(numerator / denominator)
            }

            // Solve for ρ₂
            if (density2 == null) {
                if (velocity2 == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                val numerator: Double = density1 * area1 * velocity1
                val denominator: Double = area2!! * velocity2!!
                return round4(numerator / denominator)
            }

            // Solve for A₂
            // (The Python source checked velocity_1 here; the denominator is ρ₂v₂, so v₂ is the
            //  correct value to guard against zero.)
            if (area2 == null) {
                if (velocity2 == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                val numerator: Double = density1 * area1 * velocity1
                val denominator: Double = density2 * velocity2!!
                return round4(numerator / denominator)
            }

            // Solve for v₂
            if (velocity2 == null) {
                val numerator: Double = density1 * area1 * velocity1
                val denominator: Double = density2 * area2
                return round4(numerator / denominator)
            }

            return 0.0
        }

        /**
         * p₁ + ½ρv₁² + ρgy₁ = p₂ + ½ρv₂² + ρgy₂ (Bernoulli's equation).
         * Pass every value except the unknown; leave that one `null`.
         *
         * Note: the Python source omitted the density factor on the ½v² and gy terms in
         * several branches (and never actually assigned `term3`); the correct physics is
         * implemented here, with each term carrying its ρ factor.
         */
        fun bernoullisEquation(
            density: Double? = null,
            pressure1: Double? = null,
            velocity1: Double? = null,
            height1: Double? = null,
            pressure2: Double? = null,
            velocity2: Double? = null,
            height2: Double? = null,
        ): Double {
            if (density != null && density <= 0.0) {
                throw IllegalArgumentException("Density of a fluid cannot be less than or equal to zero.")
            }

            // Solve for ρ (density): ρ = (p₂ - p₁) / (½v₁² + gy₁ - ½v₂² - gy₂)
            if (density == null) {
                val term1: Double = 0.5 * (velocity1!! * velocity1)
                val term2: Double = G * height1!!
                val term3: Double = 0.5 * (velocity2!! * velocity2)
                val term4: Double = G * height2!!
                val numerator: Double = pressure2!! - pressure1!!
                val denominator: Double = term1 + term2 - term3 - term4

                if (denominator == 0.0) throw IllegalArgumentException("Divison by zero is undefined.")

                return round4(numerator / denominator)
            }

            // Solve for p₁: p₁ = p₂ + ½ρv₂² + ρgy₂ - ½ρv₁² - ρgy₁
            if (pressure1 == null) {
                val term1: Double = 0.5 * density * (velocity1!! * velocity1)
                val term2: Double = density * G * height1!!
                val term3: Double = 0.5 * density * (velocity2!! * velocity2)
                val term4: Double = density * G * height2!!

                return round4(pressure2!! + term3 + term4 - term1 - term2)
            }

            // Solve for v₁: v₁ = √[(p₂ - p₁ + ½ρv₂² + ρgy₂ - ρgy₁) / (½ρ)]
            if (velocity1 == null) {
                val term2: Double = density * G * height1!!
                val term3: Double = 0.5 * density * (velocity2!! * velocity2)
                val term4: Double = density * G * height2!!
                val coeff: Double = 0.5 * density
                val radicand: Double = pressure2!! - pressure1 + term3 + term4 - term2

                if (radicand < 0.0) throw IllegalArgumentException("Negative radicand yields an imaginary number")

                return round4(sqrt(radicand / coeff))
            }

            // Solve for y₁: y₁ = (p₂ - p₁ + ½ρv₂² + ρgy₂ - ½ρv₁²) / (ρg)
            if (height1 == null) {
                val term1: Double = 0.5 * density * (velocity1 * velocity1)
                val term3: Double = 0.5 * density * (velocity2!! * velocity2)
                val term4: Double = density * G * height2!!

                return round4((pressure2!! - pressure1 + term3 + term4 - term1) / (density * G))
            }

            // Solve for p₂: p₂ = p₁ + ½ρv₁² + ρgy₁ - ½ρv₂² - ρgy₂
            if (pressure2 == null) {
                val term1: Double = 0.5 * density * (velocity1 * velocity1)
                val term2: Double = density * G * height1
                val term3: Double = 0.5 * density * (velocity2!! * velocity2)
                val term4: Double = density * G * height2!!

                return round4(pressure1 - term3 - term4 + term1 + term2)
            }

            // Solve for v₂: v₂ = √[(p₁ - p₂ + ½ρv₁² + ρgy₁ - ρgy₂) / (½ρ)]
            if (velocity2 == null) {
                val term1: Double = 0.5 * density * (velocity1 * velocity1)
                val term2: Double = density * G * height1
                val term4: Double = density * G * height2!!
                val coeff: Double = 0.5 * density
                val radicand: Double = -pressure2 + pressure1 - term4 + term2 + term1

                if (radicand < 0.0) throw IllegalArgumentException("Negative radicand yields an imaginary number")

                return round4(sqrt(radicand / coeff))
            }

            // Solve for y₂: y₂ = (p₁ - p₂ + ½ρv₁² + ρgy₁ - ½ρv₂²) / (ρg)
            if (height2 == null) {
                val term1: Double = 0.5 * density * (velocity1 * velocity1)
                val term2: Double = density * G * height1
                val term3: Double = 0.5 * density * (velocity2 * velocity2)

                return round4((-pressure2 + pressure1 - term3 + term2 + term1) / (density * G))
            }

            return 0.0
        }

        /**
         * η = FL/(vA) (viscosity between parallel plates). Pass every value except
         * the unknown; leave that one `null`.
         */
        fun viscocity(
            viscocity: Double? = null,
            force: Double? = null,
            distance: Double? = null,
            area: Double? = null,
            velocity: Double? = null,
        ): Double {
            if ((distance != null && distance <= 0.0) || (area != null && area <= 0.0)) {
                throw IllegalArgumentException("Dimensions of length and area cannot be less than or equal to zero.")
            }

            // Solve for F: F = ηvA/L
            if (force == null) {
                return round4(viscocity!! * ((velocity!! * area!!) / distance!!))
            }

            // Solve for L: L = ηvA/F
            if (distance == null) {
                return round4(viscocity!! * ((velocity!! * area!!) / force))
            }

            // Solve for A: A = FL/(ηv)
            if (area == null) {
                if (velocity == 0.0 || viscocity == 0.0) {
                    throw IllegalArgumentException("Division by zero is undefined.")
                }
                return round4((force * distance) / (viscocity!! * velocity!!))
            }

            // Solve for v: v = FL/(ηA)
            if (velocity == null) {
                if (viscocity == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                return round4((force * distance) / (viscocity!! * area))
            }

            // Solve for η (viscosity) — the default case
            // (The Python source checked the unknown `viscocity` here; the denominator is vA,
            //  so velocity is the correct value to guard against zero.)
            if (velocity == 0.0) throw IllegalArgumentException("Division by zero is undefined.")

            return round4((force * distance) / (velocity * area))
        }

        /**
         * R = 8ηl/(πr⁴) (Poiseuille's law for resistance). Pass every value except
         * the unknown; leave that one `null`.
         */
        fun poiseuillesLawResistance(
            resistance: Double? = null,
            viscocity: Double? = null,
            length: Double? = null,
            radius: Double? = null,
        ): Double {
            if ((length != null && length <= 0.0) || (radius != null && radius <= 0.0)) {
                throw IllegalArgumentException("Dimensions of length and radius cannot be less than or equal to zero.")
            }

            // Solve for η: η = Rπr⁴/(8l)
            if (viscocity == null) {
                return round4((resistance!! * PI * radius!!.pow(4)) / (8.0 * length!!))
            }

            // Solve for l: l = Rπr⁴/(8η)
            if (length == null) {
                return round4((resistance!! * PI * radius!!.pow(4)) / (8.0 * viscocity))
            }

            // Solve for r: r = [8ηl/(πR)]^(1/4)
            if (radius == null) {
                if (resistance == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                val radicand: Double = (8.0 * viscocity * length) / (PI * resistance!!)
                return round4(radicand.pow(0.25))
            }

            // Solve for R — the default case
            return round4((8.0 * viscocity * length) / (PI * radius.pow(4)))
        }

        /**
         * Q = (p₁ - p₂)πr⁴/(8ηl) (Poiseuille's law). Pass every value except the
         * unknown; leave that one `null`.
         *
         * Note: the Python source used (p₂ - p₁) in several branches, contradicting both
         * the displayed formula and the standard form of Poiseuille's law; the correct
         * (p₁ - p₂) convention is used consistently here.
         */
        fun poiseuillesLaw(
            flow: Double? = null,
            viscocity: Double? = null,
            length: Double? = null,
            radius: Double? = null,
            pressure1: Double? = null,
            pressure2: Double? = null,
        ): Double {
            if ((length != null && length <= 0.0) || (radius != null && radius <= 0.0)) {
                throw IllegalArgumentException("Dimensions of length and radius cannot be less than or equal to zero.")
            }

            // Solve for η: η = (p₁ - p₂)πr⁴/(8lQ)
            if (viscocity == null) {
                if (flow == 0.0) throw IllegalArgumentException("Division by zero is undefined.")
                val numerator: Double = (pressure1!! - pressure2!!) * (PI * radius!!.pow(4))
                val denominator: Double = 8.0 * length!! * flow!!
                return round4(numerator / denominator)
            }

            // Solve for l: l = (p₁ - p₂)πr⁴/(8ηQ)
            if (length == null) {
                if (viscocity == 0.0 || flow == 0.0) {
                    throw IllegalArgumentException("Division by zero is undefined.")
                }
                val numerator: Double = (pressure1!! - pressure2!!) * (PI * radius!!.pow(4))
                val denominator: Double = 8.0 * viscocity * flow!!
                return round4(numerator / denominator)
            }

            // Solve for r: r = [8ηlQ/((p₁ - p₂)π)]^(1/4)
            if (radius == null) {
                val numerator: Double = flow!! * 8.0 * viscocity * length
                val denominator: Double = (pressure1!! - pressure2!!) * PI
                val radicand: Double = numerator / denominator
                return round4(radicand.pow(0.25))
            }

            // Solve for p₁: p₁ = 8ηlQ/(πr⁴) + p₂
            if (pressure1 == null) {
                val numerator: Double = flow!! * 8.0 * viscocity * length
                val denominator: Double = PI * radius.pow(4)
                return round4((numerator / denominator) + pressure2!!)
            }

            // Solve for p₂: p₂ = p₁ - 8ηlQ/(πr⁴)
            if (pressure2 == null) {
                val numerator: Double = -flow!! * 8.0 * viscocity * length
                val denominator: Double = PI * radius.pow(4)
                return round4((numerator / denominator) + pressure1)
            }

            // Solve for Q — the default case
            val numerator: Double = (pressure1 - pressure2) * PI * radius.pow(4)
            val denominator: Double = 8.0 * viscocity * length
            return round4(numerator / denominator)
        }
    }
}
