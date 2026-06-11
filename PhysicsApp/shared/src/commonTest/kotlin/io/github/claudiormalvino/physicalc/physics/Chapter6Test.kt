package io.github.claudiormalvino.physicalc.physics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/*
 * These tests are a direct port of your Python `tests/chapter6_cal_test.py`.
 *
 * Kotlin test notes:
 *  - `assertEquals(expected, actual, tolerance)` is the float-comparison form, the
 *    equivalent of `assertAlmostEqual`. The tolerance below matches the Python `places`:
 *    places=1 -> 0.05, places=2 -> 0.005.
 *  - The `expected` lists mix error-message Strings and Doubles, mirroring the Python
 *    lists that mixed ValueError instances and floats. A String entry means "expect an
 *    IllegalArgumentException with exactly this message".
 *  - Two expectation sets were corrected relative to the Python tests because the
 *    Python implementation had math bugs (see comments at those tests).
 */
class Chapter6Test {

    private val calc = Chapter6.Calculate

    /*
     * Python wrapped the messages below across lines with a backslash continuation
     * inside the string literal, so the trailing space plus the next line's
     * indentation (20 or 24 spaces) are part of the message.
     */
    private val massError =
        "We are operating with massive objects. " +
            "                    Mass must be greater than zero."
    private val massSignError =
        "We are operating with massive objects. " +
            "                    Mass must be greater than zero. Check your signs."
    private val radicandSignError =
        "Negative radicand produces an imaginary number. " +
            "                        Check your signs."
    private val dragCoeffSignError =
        "Drag coefficient is a positive value. " +
            "                        Check your signs."
    private val fluidDensSignError =
        "Fluid density is a positive value. " +
            "                        Check your signs."
    private val areaSignError =
        "Area cannot be negative. " +
            "                        Check your signs."
    private val radiusSignError =
        "The radius cannot be negative. " +
            "                        Check your signs."
    private val viscosityVelocityError =
        "Velocity cannot be less than or euqal to 0. " +
            "                        This makes viscosity a negative value."

    // ---- centripetal_force_tang_vel ----

    @Test
    fun tangVelSolvingForCentripetalF() {
        val mass = listOf(-10.0, 10.0, 25.0, 25.0)
        val radius = listOf(2.0, -2.0, 0.0, 2.0)
        val velocity = listOf(15.0, 15.0, 15.0, 15.0)

        val expected = listOf<Any>(
            massError,
            "Radius must be greater than zero.",
            "Radius must be greater than zero.",
            2812.5,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.centripetalForceTangVel(mass = mass[i], radius = radius[i], velocity = velocity[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.centripetalForceTangVel(mass = mass[i], radius = radius[i], velocity = velocity[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    @Test
    fun tangVelSolvingForMass() {
        val centripetalF = listOf(-10.0, 10.0, 200.0, 200.0)
        val radius = listOf(2.0, -2.0, 3.0, 3.0)
        val velocity = listOf(15.0, 15.0, 0.0, 25.0)

        val expected = listOf<Any>(
            massSignError,
            "Radius must be greater than zero.",
            "Divison by zero is undefined.",
            0.96,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.centripetalForceTangVel(centripetalF = centripetalF[i], radius = radius[i], velocity = velocity[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.centripetalForceTangVel(centripetalF = centripetalF[i], radius = radius[i], velocity = velocity[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    @Test
    fun tangVelSolvingForVelocity() {
        val centripetalF = listOf(-10.0, 10.0, 200.0)
        val radius = listOf(2.0, 2.0, 3.0)
        val mass = listOf(10.0, 10.0, 25.0)

        val expected = listOf<Any>(
            radicandSignError,
            1.41,
            4.89,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.centripetalForceTangVel(centripetalF = centripetalF[i], radius = radius[i], mass = mass[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.centripetalForceTangVel(centripetalF = centripetalF[i], radius = radius[i], mass = mass[i])
                assertEquals(e as Double, result, 0.05)
            }
        }
    }

    @Test
    fun tangVelSolvingForRadius() {
        val centripetalF = listOf(0.0, -10.0, 200.0)
        val velocity = listOf(25.0, 25.0, 25.0)
        val mass = listOf(10.0, 10.0, 10.0)

        val expected = listOf<Any>(
            "Divison by zero is undefined.",
            "Radius cannot be negative. Check your signs.",
            31.25,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.centripetalForceTangVel(centripetalF = centripetalF[i], velocity = velocity[i], mass = mass[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.centripetalForceTangVel(centripetalF = centripetalF[i], velocity = velocity[i], mass = mass[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    // ---- centripetal_force_ang_vel ----

    @Test
    fun angVelSolvingForCentripetalF() {
        val mass = listOf(-10.0, 10.0, 5.0, 5.0)
        val angularVel = listOf(25.0, 0.0, 40.0, 40.0)
        val radius = listOf(5.0, 5.0, 0.0, 5.0)

        val expected = listOf<Any>(
            massError,
            0.0,
            "Radius must be greater than zero.",
            40000.0,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.centripetalForceAngVel(radius = radius[i], angularVel = angularVel[i], mass = mass[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.centripetalForceAngVel(radius = radius[i], angularVel = angularVel[i], mass = mass[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    @Test
    fun angVelSolvingForMass() {
        val centripetalF = listOf(-10.0, 10.0, 5.0, 500.0)
        val angularVel = listOf(25.0, 0.0, 40.0, 15.0)
        val radius = listOf(5.0, 5.0, 0.0, 5.0)

        val expected = listOf<Any>(
            "Mass cannot be negative. Check your signs.",
            "Division by zero is undefined.",
            "Radius must be greater than zero.",
            0.44,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.centripetalForceAngVel(radius = radius[i], angularVel = angularVel[i], centripetalF = centripetalF[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.centripetalForceAngVel(radius = radius[i], angularVel = angularVel[i], centripetalF = centripetalF[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    @Test
    fun angVelSolvingForAngularVel() {
        val centripetalF = listOf(-10.0, 200.0, 100.0)
        val mass = listOf(25.0, 5.0, 200.0)
        val radius = listOf(5.0, 1.0, 2.0)

        val expected = listOf<Any>(
            "Negative radicand produces an imaginary number.",
            6.32,
            0.5,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.centripetalForceAngVel(radius = radius[i], mass = mass[i], centripetalF = centripetalF[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.centripetalForceAngVel(radius = radius[i], mass = mass[i], centripetalF = centripetalF[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    @Test
    fun angVelSolvingForRadius() {
        val centripetalF = listOf(10.0, -200.0, 3000.0)
        val mass = listOf(25.0, 5.0, 20.0)
        val angularVel = listOf(0.0, 25.0, 8.0)

        val expected = listOf<Any>(
            "Divison by zero is undefined.",
            "Radius cannot be negative. Check your signs.",
            2.34,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.centripetalForceAngVel(angularVel = angularVel[i], mass = mass[i], centripetalF = centripetalF[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.centripetalForceAngVel(angularVel = angularVel[i], mass = mass[i], centripetalF = centripetalF[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    // ---- ideal_ang_banked_curve ----

    @Test
    fun bankedCurveSolvingForTheta() {
        val velocity = listOf(40.0, 11.0, 30.0)
        val radius = listOf(-20.0, 20.0, 10.0)

        val expected = listOf<Any>(
            "Radius cannot be less than or equal to zero.",
            31.64,
            83.77,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.idealAngBankedCurve(velocity = velocity[i], radius = radius[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.idealAngBankedCurve(velocity = velocity[i], radius = radius[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    @Test
    fun bankedCurveSolvingForVelocity() {
        val theta = listOf(31.64, -83.77, 31.64, 90.0)
        val radius = listOf(-20.0, 20.0, 20.0, 10.0)

        val expected = listOf<Any>(
            "Radius cannot be less than or equal to zero.",
            "Reconsider if theta can physically be a negative value.",
            11.0,
            "Tangent function is undefined at 90.0 and 270.0 degrees.",
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.idealAngBankedCurve(theta = theta[i], radius = radius[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.idealAngBankedCurve(theta = theta[i], radius = radius[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    @Test
    fun bankedCurveSolvingForRadius() {
        val theta = listOf(31.64, -83.77, 31.64, 15.0)
        val velocity = listOf(0.0, 11.0, 11.0, 22.0)

        // The Python tests expected 7.59 and 13.206, which came from the buggy
        // inversion ((v²)/g)·tan θ. The correct r = v²/(g·tan θ) gives the values
        // below (note the third row round-trips with bankedCurveSolvingForTheta:
        // v=11, r=20 -> θ=31.64).
        val expected = listOf<Any>(
            "Division by zero is undefined.",
            "Reconsider if theta can physically be a negative value.",
            19.9975,
            183.9422,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.idealAngBankedCurve(theta = theta[i], velocity = velocity[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.idealAngBankedCurve(theta = theta[i], velocity = velocity[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    // ---- drag_force ----

    @Test
    fun dragForceSolvingForDragF() {
        val dragCoeff = listOf(-0.5, 0.5, 0.5, 0.5)
        val fluidDens = listOf(1.225, 1.225, -1.225, 1.225)
        val area = listOf(0.5, -0.5, 0.5, 0.5)
        val velocity = listOf(16.0, 16.0, 16.0, 16.0)

        val expected = listOf<Any>(
            "The drag coefficient cannot be less than or equalt to zero.",
            "Area cannot be less than zero or equal to zero.",
            "Fluid density cannot be less than or equal to zero.",
            -39.2,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.dragForce(dragCoeff = dragCoeff[i], fluidDens = fluidDens[i], area = area[i], velocity = velocity[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.dragForce(dragCoeff = dragCoeff[i], fluidDens = fluidDens[i], area = area[i], velocity = velocity[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    @Test
    fun dragForceSolvingForDragCoeff() {
        val dragF = listOf(39.2, 39.2, 39.2)
        val fluidDens = listOf(1.225, 1.225, 1.225)
        val area = listOf(0.5, 0.5, 0.5)
        val velocity = listOf(0.0, -16.0, 16.0)

        val expected = listOf<Any>(
            "Divison by zero is undefined.",
            dragCoeffSignError,
            0.5,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.dragForce(dragF = dragF[i], fluidDens = fluidDens[i], area = area[i], velocity = velocity[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.dragForce(dragF = dragF[i], fluidDens = fluidDens[i], area = area[i], velocity = velocity[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    @Test
    fun dragForceSolvingForFluidDens() {
        val dragF = listOf(39.2, 39.2, 39.2)
        val dragCoeff = listOf(0.5, 0.5, 0.5)
        val area = listOf(0.5, 0.5, 0.5)
        val velocity = listOf(0.0, -16.0, 16.0)

        val expected = listOf<Any>(
            "Divison by zero is undefined.",
            fluidDensSignError,
            1.225,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.dragForce(dragF = dragF[i], dragCoeff = dragCoeff[i], area = area[i], velocity = velocity[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.dragForce(dragF = dragF[i], dragCoeff = dragCoeff[i], area = area[i], velocity = velocity[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    @Test
    fun dragForceSolvingForArea() {
        val dragF = listOf(39.2, 39.2, 39.2)
        val dragCoeff = listOf(0.5, 0.5, 0.5)
        val fluidDens = listOf(1.225, 1.225, 1.225)
        val velocity = listOf(0.0, -16.0, 16.0)

        val expected = listOf<Any>(
            "Divison by zero is undefined.",
            areaSignError,
            0.5,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.dragForce(dragF = dragF[i], dragCoeff = dragCoeff[i], fluidDens = fluidDens[i], velocity = velocity[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.dragForce(dragF = dragF[i], dragCoeff = dragCoeff[i], fluidDens = fluidDens[i], velocity = velocity[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    @Test
    fun dragForceSolvingForVelocity() {
        val dragF = listOf(39.2, 25.0, 2000.0)
        val dragCoeff = listOf(0.5, 0.75, 0.25)
        val fluidDens = listOf(1.225, 1000.0, 680.0)
        val area = listOf(0.5, 2.0, 5.0)

        val expected = listOf(16.0, 0.182, 2.169)

        for (i in expected.indices) {
            val result = calc.dragForce(dragF = dragF[i], dragCoeff = dragCoeff[i], fluidDens = fluidDens[i], area = area[i])
            assertEquals(expected[i], result, 0.005)
        }
    }

    // ---- stokes_law ----

    @Test
    fun stokesLawSolvingForDragFs() {
        val radius = listOf(-1.0, 0.25, 0.25)
        val viscosity = listOf(0.001, -0.001, 0.001)
        val velocity = listOf(5.0, 5.0, 5.0)

        val expected = listOf<Any>(
            "Radius cannot be less than zero or equal to zero.",
            "Viscocity cannot be a negative value.",
            -0.0235,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.stokesLaw(radius = radius[i], viscosity = viscosity[i], velocity = velocity[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.stokesLaw(radius = radius[i], viscosity = viscosity[i], velocity = velocity[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    @Test
    fun stokesLawSolvingForRadius() {
        val dragFs = listOf(0.0235, 0.0235, 0.0235, 0.0235)
        val viscosity = listOf(0.001, 0.0, 0.001, 0.001)
        val velocity = listOf(0.0, 5.0, 5.0, -5.0)

        val expected = listOf<Any>(
            "Divison by zero is undefined.",
            "Divison by zero is undefined.",
            0.25,
            radiusSignError,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.stokesLaw(dragFs = dragFs[i], viscosity = viscosity[i], velocity = velocity[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.stokesLaw(dragFs = dragFs[i], viscosity = viscosity[i], velocity = velocity[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    @Test
    fun stokesLawSolvingForViscosity() {
        val dragFs = listOf(0.0235, 0.0235, 0.580)
        val velocity = listOf(-1.0, 5.0, 5.0)
        val radius = listOf(1.0, 0.25, 0.10)

        val expected = listOf<Any>(
            viscosityVelocityError,
            0.000997,
            0.0615,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.stokesLaw(dragFs = dragFs[i], radius = radius[i], velocity = velocity[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.stokesLaw(dragFs = dragFs[i], radius = radius[i], velocity = velocity[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    @Test
    fun stokesLawSolvingForVelocity() {
        val dragFs = listOf(10.0, 0.0235, 100.0)
        val viscosity = listOf(0.580, 0.028, 10.0)
        val radius = listOf(0.25, 0.25, 0.25)

        val expected = listOf(3.658, 0.178, 2.122)

        for (i in expected.indices) {
            val result = calc.stokesLaw(dragFs = dragFs[i], radius = radius[i], viscosity = viscosity[i])
            assertEquals(expected[i], result, 0.005)
        }
    }

    // ---- terminal_velocity ----

    @Test
    fun terminalVelocitySolvingForTerminalVel() {
        val mass = listOf(-65.7, 65.7, 65.7, 65.7, 65.7, 0.861)
        val dragCoeff = listOf(1.3, -1.3, 1.3, 1.3, 1.3, 1.3)
        val area = listOf(2.5, 2.5, -2.5, 2.5, 2.5, 2.5)
        val fluidDens = listOf(1.225, 1.225, 1.225, -1.225, 1.225, 1.225)

        val expected = listOf<Any>(
            massError,
            "The drag coefficient cannot be a negative value.",
            "Area cannot be less than zero or equal to zero.",
            "Fluid density cannot be less than or equal to zero.",
            18.0,
            2.06,
        )

        for (i in expected.indices) {
            val e = expected[i]
            if (e is String) {
                val ex = assertFailsWith<IllegalArgumentException> {
                    calc.terminalVelocity(mass = mass[i], dragCoeff = dragCoeff[i], area = area[i], fluidDens = fluidDens[i])
                }
                assertEquals(e, ex.message)
            } else {
                val result = calc.terminalVelocity(mass = mass[i], dragCoeff = dragCoeff[i], area = area[i], fluidDens = fluidDens[i])
                assertEquals(e as Double, result, 0.005)
            }
        }
    }

    @Test
    fun terminalVelocitySolvingForMass() {
        val terminalVel = listOf(18.0, 36.0, 18.0)
        val dragCoeff = listOf(1.3, 1.3, 1.3)
        val area = listOf(2.5, 2.5, 2.5)
        val fluidDens = listOf(1.225, 1.225, 70.0)

        // The Python tests expected [0.861, 1.216, 49.14], which came from the buggy
        // m = √(vₜ)·ρCA/(2g). The correct m = vₜ²·ρCA/(2g) gives the values below
        // (note the first row round-trips with terminalVelocitySolvingForTerminalVel:
        // m=65.7 -> vₜ=18.0).
        val expected = listOf(65.6785, 262.7138, 3753.055)

        for (i in expected.indices) {
            val result = calc.terminalVelocity(terminalVel = terminalVel[i], dragCoeff = dragCoeff[i], area = area[i], fluidDens = fluidDens[i])
            assertEquals(expected[i], result, 0.005)
        }
    }
}
