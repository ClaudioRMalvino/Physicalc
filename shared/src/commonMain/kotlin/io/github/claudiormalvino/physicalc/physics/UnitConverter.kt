package io.github.claudiormalvino.physicalc.physics

/**
 * Unit conversion. Every unit maps to a factor relative to a base unit;
 * converting is `value * factor[from] / factor[to]`.
 */
data class Quantity(
    val name: String,
    val baseUnit: String,
    val units: Map<String, Double>, // unit name -> factor relative to baseUnit
) {
    fun convert(value: Double, fromUnit: String, toUnit: String): Double {
        val from = units[fromUnit] ?: throw IllegalArgumentException("Unknown unit: $fromUnit")
        val to = units[toUnit] ?: throw IllegalArgumentException("Unknown unit: $toUnit")
        return value * from / to
    }
}

/** All available quantities. */
object UnitConverters {

    val length = Quantity(
        name = "Length",
        baseUnit = "meter",
        units = mapOf(
            "angstrom" to 1.0E-10,
            "nanometer" to 1.0E-9,
            "micrometer" to 1.0E-6,
            "millimeter" to 1.0E-3,
            "centimeter" to 1.0E-2,
            "decimeter" to 1.0E-1,
            "meter" to 1.0,
            "kilometer" to 1.0E+3,
            "inch" to 0.0254,
            "foot" to 0.3048,
            "yard" to 0.9144,
            "mile" to 1609.34,
        ),
    )

    val time = Quantity(
        name = "Time",
        baseUnit = "second",
        units = mapOf(
            "nanosecond" to 1.0E-9,
            "microsecond" to 1.0E-6,
            "millisecond" to 1.0E-3,
            "second" to 1.0,
            "minute" to 60.0,
            "hour" to 3600.0,
            "day" to 8.64E+4,      // Corrected from upstream: was 8.64E+5
            "week" to 6.048E+5,
            "month" to 2.628E+6,
            "year" to 3.15576E+7,
        ),
    )

    val mass = Quantity(
        name = "Mass",
        baseUnit = "gram",
        units = mapOf(
            "atomic mass unit" to 1.66E-24,
            "nanogram" to 1.0E-9,
            "microgram" to 1.0E-6,
            "milligram" to 1.0E-3,
            "centigram" to 1.0E-2,
            "decigram" to 1.0E-1,  // Corrected from upstream typo "decimgram"
            "gram" to 1.0,
            "kilogram" to 1.0E+3,
            "ounce" to 28.349,
            "pound" to 453.59,
            "ton" to 907_184.74,
        ),
    )

    val force = Quantity(
        name = "Force",
        baseUnit = "newton",
        units = mapOf(
            "nanonewton" to 1.0E-9,
            "micronewton" to 1.0E-6,
            "millinewton" to 1.0E-3,
            "centinewton" to 1.0E-2,
            "decinewton" to 1.0E-1,
            "newton" to 1.0,
            "kilonewton" to 1.0E+3,
            "meganewton" to 1.0E+6,
            "pound-force" to 4.44822,
            "ounce-force" to 0.27801, // Corrected from upstream typo "ounce-fource"
        ),
    )

    val energy = Quantity(
        name = "Energy",
        baseUnit = "joule",
        units = mapOf(
            "nanojoule" to 1.0E-9,
            "microjoule" to 1.0E-6,
            "millijoule" to 1.0E-3,
            "centijoule" to 1.0E-2,
            "decijoule" to 1.0E-1,
            "joule" to 1.0,
            "kilojoule" to 1.0E+3,
            "megajoule" to 1.0E+6,
            "watt-hour" to 3.6E+3,
            "kilowatt-hour" to 3.6E+6,
            "megawatt-hour" to 3.6E+9, // Corrected from upstream: was 3.6E+6 (same as kWh)
            "calorie" to 4.1868,       // Corrected from upstream: 4186.8 is a kilocalorie
            "kilocalorie" to 4186.8,
            "electron-volt" to 1.60217E-19,
            "kiloelectron-volt" to 1.60217E-16,
            "megaelectron-volt" to 1.60217E-13,
            "erg" to 1.0E-7,
        ),
    )

    val pressure = Quantity(
        name = "Pressure",
        baseUnit = "pascal",
        units = mapOf(
            "nanopascal" to 1.0E-9,
            "micropascal" to 1.0E-6,
            "millipascal" to 1.0E-3,
            "centipascal" to 1.0E-2,
            "decipascal" to 1.0E-1,
            "pascal" to 1.0,
            "kilopascal" to 1.0E+3,
            "bar" to 1.0E+5,
            "psi" to 6894.7572,
            "ksi" to 6894757.2932,
            "atm" to 101325.0,
        ),
    )

    val speed = Quantity(
        name = "Speed",
        baseUnit = "m/s",
        units = mapOf(
            "mm/s" to 1.0E-3,
            "mm/min" to 1.66666E-5,
            "mm/h" to 2.77777E-7,
            "cm/s" to 1.0E-2,
            "cm/min" to 1.66666E-4,
            "cm/h" to 2.7778E-6,
            "m/s" to 1.0,
            "m/min" to 1.66666E-2,
            "m/h" to 2.7777E-4,
            "km/s" to 1.0E+3,
            "km/min" to 16.66666,
            "km/h" to 0.277777,
            "ft/s" to 0.3048,
            "ft/min" to 5.08E-3,
            "ft/h" to 8.46667E-5,
            "yd/s" to 0.9144,
            "yd/min" to 1.524E-2,
            "yd/h" to 2.54E-4,
            "mi/s" to 1609.344,
            "mi/min" to 26.8224,
            "mi/h" to 0.44704,
        ),
    )

    /** Display order. */
    val all: List<Quantity> = listOf(length, time, mass, force, energy, pressure, speed)
}
