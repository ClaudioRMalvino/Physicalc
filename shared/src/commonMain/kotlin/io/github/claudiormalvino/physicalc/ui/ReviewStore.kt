package io.github.claudiormalvino.physicalc.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.Settings
import io.github.claudiormalvino.physicalc.physics.PhysicsChapter
import io.github.claudiormalvino.physicalc.study.CardSchedule
import io.github.claudiormalvino.physicalc.study.Sm2
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Per-card SM-2 schedules, persisted via multiplatform-settings like
 * [AppSettings]. Cards are keyed by chapter title and prompt text (equation
 * name / definition term) so schedules survive content reordering. Review
 * is entirely pull-based: due counts surface inside the app, never as
 * notifications.
 */
@OptIn(ExperimentalTime::class)
object ReviewStore {
    private val store = Settings()

    /** Bumped on every write so composables remembering due counts recompute. */
    var revision by mutableIntStateOf(0)
        private set

    /** Calendar day (UTC) used for all scheduling, matching the home screen's date. */
    fun todayEpochDay(): Long = Clock.System.now().toEpochMilliseconds() / 86_400_000L

    fun scheduleFor(mode: FlashcardMode, chapterTitle: String, cardKey: String): CardSchedule? =
        store.getStringOrNull(key(mode, chapterTitle, cardKey))?.let(::decode)

    /** Apply a ✓/✗ grade to a card and persist its next schedule. */
    fun record(mode: FlashcardMode, chapterTitle: String, cardKey: String, remembered: Boolean) {
        val current = scheduleFor(mode, chapterTitle, cardKey) ?: CardSchedule()
        val next = Sm2.review(current, remembered, todayEpochDay())
        store.putString(key(mode, chapterTitle, cardKey), encode(next))
        revision++
    }

    fun isDue(mode: FlashcardMode, chapterTitle: String, cardKey: String, today: Long): Boolean =
        scheduleFor(mode, chapterTitle, cardKey)?.let { it.dueEpochDay <= today } == true

    /** How many of this chapter's cards are due. New (never-graded) cards don't count. */
    fun dueCount(mode: FlashcardMode, chapter: PhysicsChapter, today: Long = todayEpochDay()): Int =
        cardKeys(mode, chapter).count { isDue(mode, chapter.title, it, today) }

    fun cardKeys(mode: FlashcardMode, chapter: PhysicsChapter): List<String> = when (mode) {
        FlashcardMode.Equations -> chapter.equations.map { it.name }
        FlashcardMode.Definitions -> chapter.definitions.map { it.term }
    }

    private fun key(mode: FlashcardMode, chapterTitle: String, cardKey: String) =
        "srs|${mode.name}|$chapterTitle|$cardKey"

    private fun encode(s: CardSchedule) =
        "${s.easiness}|${s.repetitions}|${s.intervalDays}|${s.dueEpochDay}"

    private fun decode(raw: String): CardSchedule? {
        val parts = raw.split('|')
        if (parts.size != 4) return null
        return CardSchedule(
            easiness = parts[0].toDoubleOrNull() ?: return null,
            repetitions = parts[1].toIntOrNull() ?: return null,
            intervalDays = parts[2].toIntOrNull() ?: return null,
            dueEpochDay = parts[3].toLongOrNull() ?: return null,
        )
    }
}
