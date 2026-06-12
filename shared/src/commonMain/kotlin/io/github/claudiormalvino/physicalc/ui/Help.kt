package io.github.claudiormalvino.physicalc.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.russhwolf.settings.Settings
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

/*
 * First-run guides. Each surface's guide pops up automatically the first time
 * the surface is opened, then lives behind the ⋮ menu's Help entry. Seen flags
 * persist via multiplatform-settings; nothing is ever pushed at the user again.
 */

/** Surfaces that have a guide. */
enum class HelpTopic(val storageKey: String) {
    Welcome("welcome"),
    Tools("tools"),
    Chapters("chapters"),
    Flashcards("flashcards"),
    Vectors("vectors"),
}

/** Persisted has-the-user-seen-this flags. */
object HelpStore {
    private val store = Settings()

    fun seen(topic: HelpTopic): Boolean = store.getBoolean("help_seen_${topic.storageKey}", false)

    fun markSeen(topic: HelpTopic) = store.putBoolean("help_seen_${topic.storageKey}", true)
}

private data class HelpSection(val title: String, val body: String)

private data class HelpGuide(val title: String, val intro: String?, val sections: List<HelpSection>)

private fun guideFor(topic: HelpTopic): HelpGuide = when (topic) {
    HelpTopic.Welcome -> HelpGuide(
        title = "Welcome to Physicalc",
        intro = "Your companion for first-year physics — look up equations, solve problems, " +
            "convert units, and drill concepts until they stick.",
        sections = listOf(
            HelpSection(
                "Home",
                "The solar system with every planet at its real position for today. " +
                    "Settings and help live behind the ⋮ button.",
            ),
            HelpSection(
                "Tools",
                "A unit converter, a 2D & 3D vector calculator, and flashcards with spaced repetition.",
            ),
            HelpSection(
                "Chapters",
                "Twelve chapters of equations and definitions, each with interactive solvers " +
                    "for working problems.",
            ),
            HelpSection(
                "Getting around",
                "Use the bottom bar or swipe left and right to move between tabs.",
            ),
        ),
    )

    HelpTopic.Tools -> HelpGuide(
        title = "The Tools tab",
        intro = "Three study utilities, none tied to a specific chapter.",
        sections = listOf(
            HelpSection(
                "Unit Converter",
                "Pick a quantity, type a value, and the conversion updates live as you type.",
            ),
            HelpSection(
                "Vectors",
                "Add or subtract two vectors in 2D or 3D and watch the tip-to-tail construction " +
                    "drawn live, with dot product, cross product, and the angle between them.",
            ),
            HelpSection(
                "Flashcards",
                "Flip-to-reveal cards built from every chapter's equations and definitions. " +
                    "Your ✓/✗ self-grades schedule each card's next review.",
            ),
        ),
    )

    HelpTopic.Chapters -> HelpGuide(
        title = "Chapters & solvers",
        intro = "Each chapter collects its key equations — every variable defined, with units — " +
            "plus a glossary of the chapter's terms.",
        sections = listOf(
            HelpSection(
                "Equations",
                "Tap an equation to see what each symbol means and the units it carries.",
            ),
            HelpSection(
                "Solvers",
                "Open an equation's solver, select the variable you want to solve for, then " +
                    "enter the known values. The solver computes the unknown for you.",
            ),
            HelpSection(
                "No nonsense answers",
                "If your inputs have no physical solution — a negative time, no real roots — " +
                    "the solver tells you instead of inventing a number.",
            ),
        ),
    )

    HelpTopic.Flashcards -> HelpGuide(
        title = "Flashcards",
        intro = "Active recall plus spaced repetition — the two most evidence-backed " +
            "study techniques, combined.",
        sections = listOf(
            HelpSection(
                "Studying",
                "Choose equations or definitions, pick a chapter, then tap a card to flip it. " +
                    "Grade yourself honestly: ✓ if you knew it, ✗ if you didn't.",
            ),
            HelpSection(
                "Spaced repetition",
                "Grades schedule each card's next review. Misses come back tomorrow; cards you " +
                    "keep getting right wait a day, then six, then weeks. Due counts appear on " +
                    "the pickers when cards are ready.",
            ),
            HelpSection(
                "Due cards only",
                "Toggle the chip on the chapter list to deal just the cards due for review.",
            ),
            HelpSection(
                "No nagging",
                "The app never sends notifications. Check in when you want to study — " +
                    "the due counts will be waiting.",
            ),
        ),
    )

    HelpTopic.Vectors -> HelpGuide(
        title = "The vector calculator",
        intro = null,
        sections = listOf(
            HelpSection(
                "Entering vectors",
                "Type components, or switch to magnitude & angle in 2D. The 3D toggle adds " +
                    "a z component to each vector.",
            ),
            HelpSection(
                "The drawing",
                "A and B are drawn from the origin; dashed ghosts show the tip-to-tail " +
                    "construction with the resultant as the diagonal.",
            ),
            HelpSection(
                "3D view",
                "Drag to orbit, pinch to zoom, double-tap to reset. Dashed drop lines anchor " +
                    "each tip to the xy-plane so you can read depth at a glance.",
            ),
            HelpSection(
                "The numbers",
                "Below the canvas: components, magnitude, dot product, the full cross-product " +
                    "vector, and the angle between A and B.",
            ),
        ),
    )
}

/**
 * A guide as a dismissable frosted-glass overlay, matching the bottom bar's
 * material. Drawn in the app window (not a Dialog) because Haze can only
 * blur content living in the same window — the caller supplies the same
 * [HazeState] that feeds the bar and renders this above its content.
 */
@OptIn(ExperimentalHazeMaterialsApi::class, ExperimentalComposeUiApi::class)
@Composable
fun HelpOverlay(topic: HelpTopic, hazeState: HazeState, onDismiss: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val guide = guideFor(topic)
    val shape = RoundedCornerShape(28.dp)

    BackHandler(enabled = true, onBack = onDismiss)

    // Scrim: tap anywhere outside the card to dismiss.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.scrim.copy(alpha = 0.45f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .padding(24.dp)
                .widthIn(max = 420.dp)
                .fillMaxWidth()
                .heightIn(max = 620.dp)
                .clip(shape)
                .hazeEffect(state = hazeState, style = HazeMaterials.thin(colors.surface))
                .border(width = 1.dp, color = colors.onSurface.copy(alpha = 0.10f), shape = shape)
                // Swallow taps on the card itself so they don't reach the scrim.
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                ),
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = guide.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface,
                )

                Spacer(Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                ) {
                    if (guide.intro != null) {
                        Text(
                            text = guide.intro,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                    guide.sections.forEach { section ->
                        Text(
                            text = section.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.primary,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = section.body,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(14.dp))
                    }
                }

                Spacer(Modifier.height(4.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                ) {
                    Text("Got it")
                }
            }
        }
    }
}
