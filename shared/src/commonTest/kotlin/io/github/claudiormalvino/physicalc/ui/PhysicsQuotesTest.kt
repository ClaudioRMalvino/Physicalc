package io.github.claudiormalvino.physicalc.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PhysicsQuotesTest {

    private val dayMs = 86_400_000L

    @Test
    fun stableWithinADay() {
        val morning = 20_615L * dayMs + 6 * 3_600_000L
        val evening = 20_615L * dayMs + 23 * 3_600_000L
        assertEquals(PhysicsQuotes.forDay(morning), PhysicsQuotes.forDay(evening))
    }

    @Test
    fun changesAcrossDays() {
        val today = PhysicsQuotes.forDay(20_615L * dayMs)
        val tomorrow = PhysicsQuotes.forDay(20_616L * dayMs)
        assertNotEquals(today, tomorrow)
    }

    @Test
    fun cyclesThroughTheWholeList() {
        val seen = (0 until PhysicsQuotes.all.size)
            .map { PhysicsQuotes.forDay(it * dayMs) }
            .toSet()
        assertEquals(PhysicsQuotes.all.size, seen.size, "every quote should appear in a full cycle")
    }

    @Test
    fun allQuotesAreWellFormed() {
        for (q in PhysicsQuotes.all) {
            assertTrue(q.text.isNotBlank() && q.author.isNotBlank())
            assertTrue(q.text.length <= 160, "quote too long for the home screen: ${q.author}")
        }
    }
}
