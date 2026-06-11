package io.github.claudiormalvino.physicalc.physics

/**
 * Chapter 8: potential energy and conservation of energy.
 *
 * Reference-only: no equation here has a single-unknown solver, so none carry
 * a calculation.
 */
class Chapter8 : PhysicsChapter(
    title = "Ch.8 - Potential Energy & Conservation of Energy",
    description = "Potential energy, conservative forces, and conservation of energy.",
) {

    override val equations: List<Equation> = listOf(
        Equation(
            name = "Difference in gravitational potential energy",
            formula = "ΔU_{αβ} = U_β - U_α = -W_{αβ}",
            variables = mapOf(
                "ΔUαβ" to "Change in gravitational potential energy (J)",
                "Uβ" to "Gravitational potential energy at final position (J)",
                "Uα" to "Gravitational potential energy at initial position (J)",
                "Wαβ" to "Work done from point α to point β (J)",
            ),
        ),
        Equation(
            name = "Conservation of Energy",
            formula = "W_{nc,αβ} = Δ(K + U)_{αβ} = ΔE_{αβ}",
            variables = mapOf(
                "Wₙc,αβ" to "Non-conservative work (J)",
                "Δ(K+U)_{αβ}" to "The sum of the changes in kinetic energy and potential energy from α to β (J)",
                "ΔEαβ" to "The change in energy of the system (J)",
            ),
        ),
        Equation(
            name = "Work done by a conservative force along a closed path",
            formula = "W(closed path) = ∫ \\vec{F}_{cons} · d\\vec{r} = 0",
            variables = mapOf(
                "F_{cons}" to "Conservative force (N)",
                "dr" to "Infinitesmal distance (m)",
            ),
        ),
        Equation(
            name = "Condition for conservative forces in two dimensions",
            formula = "(∂F(y)/∂x) = (∂F(x)/∂y)",
            variables = mapOf(
                "∂F(y)/∂x" to "Partial derivative of F(y) with respect to x",
                "∂F(x)/∂y" to "Partial derivative of F(x) with respect to y",
            ),
        ),
        Equation(
            name = "Conservative force is the negative derivative of the potential energy",
            formula = "F(l) = -∂U/∂x(l)",
            variables = mapOf(
                "F(l)" to "Conservative force (N)",
                "dU/dx(l)" to "Derivative of U with respect to x(l)",
            ),
        ),
    )

    override val definitions: List<Definition> = listOf(
        Definition(
            term = "conservative force",
            meaning = "force that does work independent of path",
        ),
        Definition(
            term = "conserved quantity",
            meaning = "one that cannot be created or destroyed, but may be transformed between different forms of itself",
        ),
        Definition(
            term = "energy conservation",
            meaning = "total energy of an isolated system is constant",
        ),
        Definition(
            term = "equilibrium point",
            meaning = "position where the assumed conservative, net force on a particle, given by the slope of its potential energy curve, is zero",
        ),
        Definition(
            term = "exact differential",
            meaning = "is the total differential of a function and requires the use of partial derivatives if the function involves more than one dimension",
        ),
        Definition(
            term = "mechanical energy",
            meaning = "sum of the kinetic and potential energies",
        ),
        Definition(
            term = "non-conservative force",
            meaning = "force that does work that depends on path",
        ),
        Definition(
            term = "non-renewable",
            meaning = "energy source that is not renewable, but is depleted by human consumption",
        ),
        Definition(
            term = "potential energy",
            meaning = "function of position, energy possessed by an object relative to the system considered",
        ),
        Definition(
            term = "potential energy diagram",
            meaning = "graph of a particle's potential energy as a function of position",
        ),
        Definition(
            term = "potential energy difference",
            meaning = "negative of the work done acting between two points in space",
        ),
        Definition(
            term = "renewable",
            meaning = "energy source that is replenished by natural processes, over human time scales",
        ),
        Definition(
            term = "turning point",
            meaning = "position where the velocity of a particle, in one-dimensional motion, changes sign",
        ),
    )

    /** Empty; kept for structural parity with the other chapters. */
    object Calculate
}
