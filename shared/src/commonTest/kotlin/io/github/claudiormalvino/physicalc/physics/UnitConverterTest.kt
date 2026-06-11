package io.github.claudiormalvino.physicalc.physics

import kotlin.test.Test
import kotlin.test.assertEquals

/*
 * Port of your Python `tests/conversion_test.py`, plus a couple of checks
 * covering the data fixes made during the port (day length, megawatt-hour).
 */
class UnitConverterTest {

    private val length = UnitConverters.length

    @Test
    fun meterToKilometer() {
        val scalars = listOf(1.0, 30.0, 1000.0, 1500.0)
        val expected = listOf(0.001, 0.03, 1.0, 1.5)
        for (i in expected.indices) {
            assertEquals(expected[i], length.convert(scalars[i], "meter", "kilometer"), 1e-9)
        }
    }

    @Test
    fun kilometerToMeter() {
        val scalars = listOf(1.0, 30.0, 0.5, 0.1)
        val expected = listOf(1000.0, 30000.0, 500.0, 100.0)
        for (i in expected.indices) {
            assertEquals(expected[i], length.convert(scalars[i], "kilometer", "meter"), 1e-9)
        }
    }

    @Test
    fun nanometerToMillimeter() {
        val scalars = listOf(1.0, 30.0, 0.01, 250.0)
        val expected = listOf(1.0E-6, 3.0E-5, 1.0E-8, 2.5E-4)
        for (i in expected.indices) {
            assertEquals(expected[i], length.convert(scalars[i], "nanometer", "millimeter"), 5e-7)
        }
    }

    @Test
    fun imperialToMetric() {
        val scalars = listOf(1.0, 1.0, 2.0, 30.0)
        val fromUnits = listOf("inch", "foot", "yard", "mile")
        val toUnits = listOf("centimeter", "meter", "meter", "kilometer")
        val expected = listOf(2.54, 0.3048, 1.8288, 48.2803)
        for (i in expected.indices) {
            assertEquals(expected[i], length.convert(scalars[i], fromUnits[i], toUnits[i]), 0.005)
        }
    }

    @Test
    fun metricToImperial() {
        val scalars = listOf(1.0, 1.0, 2.0, 30.0)
        val fromUnits = listOf("centimeter", "meter", "meter", "kilometer")
        val toUnits = listOf("inch", "foot", "yard", "mile")
        val expected = listOf(0.393701, 3.2808, 2.187, 18.64)
        for (i in expected.indices) {
            assertEquals(expected[i], length.convert(scalars[i], fromUnits[i], toUnits[i]), 0.005)
        }
    }

    // --- Checks for the data fixes made during the port ---

    @Test
    fun dayIs24Hours() {
        assertEquals(24.0, UnitConverters.time.convert(1.0, "day", "hour"), 1e-9)
        assertEquals(7.0, UnitConverters.time.convert(1.0, "week", "day"), 1e-9)
    }

    @Test
    fun megawattHourIsThousandKilowattHours() {
        assertEquals(1000.0, UnitConverters.energy.convert(1.0, "megawatt-hour", "kilowatt-hour"), 1e-9)
    }
}
