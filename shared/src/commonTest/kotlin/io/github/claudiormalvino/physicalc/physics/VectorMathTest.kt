package io.github.claudiormalvino.physicalc.physics

import kotlin.test.Test
import kotlin.test.assertEquals

/*
 * Tests for Vec2 vector math.
 */
class VectorMathTest {

    private val tolerance = 1e-9

    @Test
    fun magnitudeOfClassicTriples() {
        assertEquals(5.0, Vec2(3.0, 4.0).magnitude, tolerance)
        assertEquals(13.0, Vec2(-5.0, 12.0).magnitude, tolerance)
        assertEquals(0.0, Vec2.ZERO.magnitude, tolerance)
    }

    @Test
    fun angleInEachQuadrant() {
        assertEquals(0.0, Vec2(1.0, 0.0).angleDegrees, tolerance)
        assertEquals(45.0, Vec2(1.0, 1.0).angleDegrees, tolerance)
        assertEquals(90.0, Vec2(0.0, 2.0).angleDegrees, tolerance)
        assertEquals(135.0, Vec2(-1.0, 1.0).angleDegrees, tolerance)
        assertEquals(180.0, Vec2(-3.0, 0.0).angleDegrees, tolerance)
        assertEquals(-135.0, Vec2(-1.0, -1.0).angleDegrees, tolerance)
        assertEquals(-45.0, Vec2(1.0, -1.0).angleDegrees, tolerance)
        assertEquals(0.0, Vec2.ZERO.angleDegrees, tolerance)
    }

    @Test
    fun polarRoundTrip() {
        val v = Vec2.fromPolar(10.0, 30.0)
        assertEquals(8.660254037844387, v.x, tolerance)
        assertEquals(5.0, v.y, tolerance)
        assertEquals(10.0, v.magnitude, tolerance)
        assertEquals(30.0, v.angleDegrees, tolerance)
    }

    @Test
    fun polarWithNegativeAngle() {
        val v = Vec2.fromPolar(5.0, -90.0)
        assertEquals(0.0, v.x, tolerance)
        assertEquals(-5.0, v.y, tolerance)
    }

    @Test
    fun additionAndSubtraction() {
        val a = Vec2(3.0, 4.0)
        val b = Vec2(-1.0, 2.0)
        assertEquals(Vec2(2.0, 6.0), a + b)
        assertEquals(Vec2(4.0, 2.0), a - b)
        assertEquals(a, a + Vec2.ZERO)
    }

    @Test
    fun scalarMultiplication() {
        assertEquals(Vec2(6.0, -8.0), Vec2(3.0, -4.0) * 2.0)
        // Compare componentwise: -4.0 * 0.0 is IEEE -0.0, which != 0.0 under equals().
        val zeroed = Vec2(3.0, -4.0) * 0.0
        assertEquals(0.0, zeroed.x, tolerance)
        assertEquals(0.0, zeroed.y, tolerance)
    }

    @Test
    fun dotProduct() {
        assertEquals(11.0, Vec2(3.0, 4.0) dot Vec2(1.0, 2.0), tolerance)
        // Perpendicular vectors have zero dot product.
        assertEquals(0.0, Vec2(1.0, 0.0) dot Vec2(0.0, 5.0), tolerance)
        // Antiparallel vectors give -|a||b|.
        assertEquals(-6.0, Vec2(2.0, 0.0) dot Vec2(-3.0, 0.0), tolerance)
    }

    @Test
    fun crossProductZComponent() {
        // +x cross +y points out of the page.
        assertEquals(1.0, Vec2(1.0, 0.0) cross Vec2(0.0, 1.0), tolerance)
        assertEquals(-1.0, Vec2(0.0, 1.0) cross Vec2(1.0, 0.0), tolerance)
        // Parallel vectors have zero cross product.
        assertEquals(0.0, Vec2(2.0, 3.0) cross Vec2(4.0, 6.0), tolerance)
        assertEquals(2.0, Vec2(3.0, 4.0) cross Vec2(1.0, 2.0), tolerance)
    }

    @Test
    fun angleBetween() {
        assertEquals(90.0, Vec2(1.0, 0.0).angleBetweenDegrees(Vec2(0.0, 3.0)), tolerance)
        assertEquals(180.0, Vec2(1.0, 0.0).angleBetweenDegrees(Vec2(-2.0, 0.0)), tolerance)
        assertEquals(0.0, Vec2(1.0, 1.0).angleBetweenDegrees(Vec2(3.0, 3.0)), tolerance)
        assertEquals(45.0, Vec2(1.0, 0.0).angleBetweenDegrees(Vec2(1.0, 1.0)), tolerance)
        // Order doesn't matter: always the smaller angle.
        assertEquals(45.0, Vec2(1.0, 1.0).angleBetweenDegrees(Vec2(1.0, 0.0)), tolerance)
        assertEquals(0.0, Vec2.ZERO.angleBetweenDegrees(Vec2(1.0, 0.0)), tolerance)
    }

    @Test
    fun vec3Magnitude() {
        assertEquals(3.0, Vec3(1.0, 2.0, 2.0).magnitude, tolerance)
        assertEquals(13.0, Vec3(3.0, 4.0, 12.0).magnitude, tolerance)
        assertEquals(0.0, Vec3.ZERO.magnitude, tolerance)
    }

    @Test
    fun vec3AdditionAndSubtraction() {
        val a = Vec3(1.0, 2.0, 3.0)
        val b = Vec3(4.0, -5.0, 6.0)
        assertEquals(Vec3(5.0, -3.0, 9.0), a + b)
        assertEquals(Vec3(-3.0, 7.0, -3.0), a - b)
        assertEquals(a, a + Vec3.ZERO)
    }

    @Test
    fun vec3DotProduct() {
        assertEquals(12.0, Vec3(1.0, 2.0, 3.0) dot Vec3(4.0, -5.0, 6.0), tolerance)
        // Orthogonal basis vectors
        assertEquals(0.0, Vec3(1.0, 0.0, 0.0) dot Vec3(0.0, 1.0, 0.0), tolerance)
    }

    @Test
    fun vec3CrossProductRightHandRule() {
        val xHat = Vec3(1.0, 0.0, 0.0)
        val yHat = Vec3(0.0, 1.0, 0.0)
        val zHat = Vec3(0.0, 0.0, 1.0)
        // Cyclic: x̂ × ŷ = ẑ, ŷ × ẑ = x̂, ẑ × x̂ = ŷ
        assertEquals(zHat, xHat cross yHat)
        assertEquals(xHat, yHat cross zHat)
        assertEquals(yHat, zHat cross xHat)
        // Anticommutative
        assertEquals(Vec3(0.0, 0.0, -1.0), yHat cross xHat)
    }

    @Test
    fun vec3CrossProductIsOrthogonalToInputs() {
        val a = Vec3(1.0, 2.0, 3.0)
        val b = Vec3(4.0, -5.0, 6.0)
        val c = a cross b
        assertEquals(Vec3(27.0, 6.0, -13.0), c)
        assertEquals(0.0, c dot a, tolerance)
        assertEquals(0.0, c dot b, tolerance)
    }

    @Test
    fun vec3CrossReducesToVec2CrossInPlane() {
        // For vectors in the xy-plane, the 3D cross product is (0, 0, vec2 cross).
        val a2 = Vec2(10.0, 5.0)
        val b2 = Vec2(30.0, 10.0)
        val cross3 = Vec3(a2.x, a2.y, 0.0) cross Vec3(b2.x, b2.y, 0.0)
        assertEquals(0.0, cross3.x, tolerance)
        assertEquals(0.0, cross3.y, tolerance)
        assertEquals(a2 cross b2, cross3.z, tolerance)
    }

    @Test
    fun vec3AngleBetween() {
        assertEquals(90.0, Vec3(1.0, 0.0, 0.0).angleBetweenDegrees(Vec3(0.0, 0.0, 4.0)), tolerance)
        assertEquals(180.0, Vec3(1.0, 1.0, 1.0).angleBetweenDegrees(Vec3(-2.0, -2.0, -2.0)), tolerance)
        assertEquals(0.0, Vec3(1.0, 2.0, 3.0).angleBetweenDegrees(Vec3(2.0, 4.0, 6.0)), tolerance)
        assertEquals(0.0, Vec3.ZERO.angleBetweenDegrees(Vec3(1.0, 0.0, 0.0)), tolerance)
    }
}
