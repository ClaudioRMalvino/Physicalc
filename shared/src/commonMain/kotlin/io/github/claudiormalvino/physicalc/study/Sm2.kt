package io.github.claudiormalvino.physicalc.study

import kotlin.math.ceil
import kotlin.math.max

/**
 * Scheduling state for one flashcard. A card with no stored schedule is "new" —
 * it has never been graded and is not counted as due.
 */
data class CardSchedule(
    val easiness: Double = 2.5,
    val repetitions: Int = 0,
    val intervalDays: Int = 0,
    val dueEpochDay: Long = 0L,
)

/**
 * SuperMemo-2 spaced repetition with the app's binary ✓/✗ grade mapped onto
 * SM-2's 0–5 quality scale (✓ = 4, ✗ = 2). Successive successes go 1 day,
 * 6 days, then interval × easiness; a miss resets the card to 1 day and
 * lowers its easiness, floored at 1.3.
 */
object Sm2 {
    private const val MIN_EASINESS = 1.3

    fun review(schedule: CardSchedule, remembered: Boolean, todayEpochDay: Long): CardSchedule {
        val quality = if (remembered) 4 else 2
        val easiness = max(
            MIN_EASINESS,
            schedule.easiness + 0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02),
        )
        if (!remembered) {
            return CardSchedule(
                easiness = easiness,
                repetitions = 0,
                intervalDays = 1,
                dueEpochDay = todayEpochDay + 1,
            )
        }
        val repetitions = schedule.repetitions + 1
        val intervalDays = when (repetitions) {
            1 -> 1
            2 -> 6
            else -> ceil(schedule.intervalDays * easiness).toInt()
        }
        return CardSchedule(
            easiness = easiness,
            repetitions = repetitions,
            intervalDays = intervalDays,
            dueEpochDay = todayEpochDay + intervalDays,
        )
    }
}
