package io.github.claudiormalvino.physicalc.physics

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 2D vector math for the vector tool. Angles are in degrees, measured
 * counterclockwise from the +x axis, as in introductory physics texts.
 */
data class Vec2(val x: Double, val y: Double) {

    val magnitude: Double get() = sqrt(x * x + y * y)

    /** Direction in degrees, normalized to (-180, 180]. Zero vector reports 0. */
    val angleDegrees: Double get() = if (x == 0.0 && y == 0.0) 0.0 else atan2(y, x) * 180.0 / PI

    operator fun plus(other: Vec2): Vec2 = Vec2(x + other.x, y + other.y)

    operator fun minus(other: Vec2): Vec2 = Vec2(x - other.x, y - other.y)

    operator fun times(scalar: Double): Vec2 = Vec2(x * scalar, y * scalar)

    infix fun dot(other: Vec2): Double = x * other.x + y * other.y

    /** z-component of the 3D cross product; positive when `other` is counterclockwise of this. */
    infix fun cross(other: Vec2): Double = x * other.y - y * other.x

    /**
     * Smaller angle between the two vectors in degrees, in [0, 180].
     * Undefined for a zero vector; returns 0 in that case.
     */
    fun angleBetweenDegrees(other: Vec2): Double {
        if ((x == 0.0 && y == 0.0) || (other.x == 0.0 && other.y == 0.0)) return 0.0
        // atan2(|cross|, dot) is stable near 0° and 180°, unlike acos of the
        // normalized dot product which loses precision there.
        return atan2(kotlin.math.abs(this cross other), this dot other) * 180.0 / PI
    }

    companion object {
        val ZERO = Vec2(0.0, 0.0)

        fun fromPolar(magnitude: Double, angleDegrees: Double): Vec2 {
            val radians = angleDegrees * PI / 180.0
            return Vec2(magnitude * cos(radians), magnitude * sin(radians))
        }
    }
}

/** 3D vector math. Same conventions as [Vec2]; the cross product is a true vector here. */
data class Vec3(val x: Double, val y: Double, val z: Double) {

    val magnitude: Double get() = sqrt(x * x + y * y + z * z)

    operator fun plus(other: Vec3): Vec3 = Vec3(x + other.x, y + other.y, z + other.z)

    operator fun minus(other: Vec3): Vec3 = Vec3(x - other.x, y - other.y, z - other.z)

    operator fun times(scalar: Double): Vec3 = Vec3(x * scalar, y * scalar, z * scalar)

    infix fun dot(other: Vec3): Double = x * other.x + y * other.y + z * other.z

    infix fun cross(other: Vec3): Vec3 = Vec3(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x,
    )

    /**
     * Smaller angle between the two vectors in degrees, in [0, 180].
     * Undefined for a zero vector; returns 0 in that case.
     */
    fun angleBetweenDegrees(other: Vec3): Double {
        if (magnitude == 0.0 || other.magnitude == 0.0) return 0.0
        return atan2((this cross other).magnitude, this dot other) * 180.0 / PI
    }

    companion object {
        val ZERO = Vec3(0.0, 0.0, 0.0)
    }
}
