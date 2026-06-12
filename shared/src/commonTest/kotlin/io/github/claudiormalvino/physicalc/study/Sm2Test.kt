package io.github.claudiormalvino.physicalc.study

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/*
 * Tests for the SM-2 scheduling algorithm.
 */
class Sm2Test {

    private val today = 20_000L // arbitrary epoch day

    @Test
    fun firstSuccessSchedulesTomorrow() {
        val s = Sm2.review(CardSchedule(), remembered = true, todayEpochDay = today)
        assertEquals(1, s.repetitions)
        assertEquals(1, s.intervalDays)
        assertEquals(today + 1, s.dueEpochDay)
        assertEquals(2.5, s.easiness, 1e-9)
    }

    @Test
    fun secondSuccessSchedulesSixDaysOut() {
        var s = Sm2.review(CardSchedule(), remembered = true, todayEpochDay = today)
        s = Sm2.review(s, remembered = true, todayEpochDay = today + 1)
        assertEquals(2, s.repetitions)
        assertEquals(6, s.intervalDays)
        assertEquals(today + 7, s.dueEpochDay)
    }

    @Test
    fun thirdSuccessMultipliesByEasiness() {
        var s = Sm2.review(CardSchedule(), remembered = true, todayEpochDay = today)
        s = Sm2.review(s, remembered = true, todayEpochDay = today + 1)
        s = Sm2.review(s, remembered = true, todayEpochDay = today + 7)
        // ceil(6 × 2.5) = 15
        assertEquals(3, s.repetitions)
        assertEquals(15, s.intervalDays)
        assertEquals(today + 7 + 15, s.dueEpochDay)
    }

    @Test
    fun missResetsToOneDayAndLowersEasiness() {
        var s = Sm2.review(CardSchedule(), remembered = true, todayEpochDay = today)
        s = Sm2.review(s, remembered = true, todayEpochDay = today + 1)
        s = Sm2.review(s, remembered = false, todayEpochDay = today + 7)
        assertEquals(0, s.repetitions)
        assertEquals(1, s.intervalDays)
        assertEquals(today + 8, s.dueEpochDay)
        // Quality 2: easiness drops by 0.32
        assertEquals(2.18, s.easiness, 1e-9)
    }

    @Test
    fun easinessNeverDropsBelowFloor() {
        var s = CardSchedule()
        repeat(20) { s = Sm2.review(s, remembered = false, todayEpochDay = today + it) }
        assertEquals(1.3, s.easiness, 1e-9)
    }

    @Test
    fun recoveryAfterMissRestartsLadder() {
        var s = Sm2.review(CardSchedule(), remembered = false, todayEpochDay = today)
        s = Sm2.review(s, remembered = true, todayEpochDay = today + 1)
        assertEquals(1, s.repetitions)
        assertEquals(1, s.intervalDays)
        s = Sm2.review(s, remembered = true, todayEpochDay = today + 2)
        assertEquals(6, s.intervalDays)
        // Easiness stays reduced from the earlier miss.
        assertEquals(2.18, s.easiness, 1e-9)
    }

    @Test
    fun intervalsGrowMonotonicallyWithSuccesses() {
        var s = CardSchedule()
        var previous = 0
        var day = today
        repeat(6) {
            s = Sm2.review(s, remembered = true, todayEpochDay = day)
            assertTrue(s.intervalDays >= previous)
            previous = s.intervalDays
            day = s.dueEpochDay
        }
        // Six straight successes should reach a multi-week interval.
        assertTrue(s.intervalDays >= 30)
    }
}
