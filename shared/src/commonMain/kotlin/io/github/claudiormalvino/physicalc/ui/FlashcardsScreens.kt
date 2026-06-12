package io.github.claudiormalvino.physicalc.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.claudiormalvino.physicalc.physics.ChapterRegistry
import io.github.claudiormalvino.physicalc.physics.PhysicsChapter
import kotlin.math.roundToInt

/*
 * Flashcards tool: choose Equations or Definitions, choose a chapter, then
 * study a shuffled deck one card at a time — tap to flip, self-grade with ✗ / ✓.
 * Every grade feeds an SM-2 schedule (see ReviewStore); due counts appear on
 * the mode and chapter pickers, and a "due only" toggle deals just those cards.
 */

enum class FlashcardMode(val label: String) {
    Equations("Equations"),
    Definitions("Definitions"),
}

private data class Flashcard(
    val front: String,
    val back: String,
    val backIsFormula: Boolean,
)

private fun cardsFor(mode: FlashcardMode, chapter: PhysicsChapter): List<Flashcard> = when (mode) {
    FlashcardMode.Equations -> chapter.equations.map { Flashcard(it.name, it.formula, backIsFormula = true) }
    FlashcardMode.Definitions -> chapter.definitions.map { Flashcard(it.term, it.meaning, backIsFormula = false) }
}

// ---- 1. Mode selection -------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsModeScreen(
    onSelect: (FlashcardMode) -> Unit,
    onSettingsClick: () -> Unit,
    onBack: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = { Text("Flashcards", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Recomputed when a session records grades (revision bump).
            val revision = ReviewStore.revision
            val dueByMode = remember(revision) {
                val today = ReviewStore.todayEpochDay()
                FlashcardMode.entries.associateWith { mode ->
                    ChapterRegistry.all.sumOf { ReviewStore.dueCount(mode, it, today) }
                }
            }

            Text(
                text = "What do you want to study?",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
            )
            ModeCard(
                title = "Equations",
                subtitle = "See the equation's name, recall its formula",
                due = dueByMode.getValue(FlashcardMode.Equations),
                onClick = { onSelect(FlashcardMode.Equations) },
            )
            ModeCard(
                title = "Definitions",
                subtitle = "See the term, recall its meaning",
                due = dueByMode.getValue(FlashcardMode.Definitions),
                onClick = { onSelect(FlashcardMode.Definitions) },
            )
        }
    }
}

@Composable
private fun ModeCard(title: String, subtitle: String, due: Int, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.outlineVariant),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = colors.primary)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
            if (due > 0) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "$due due for review today",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.tertiary,
                )
            }
        }
    }
}

// ---- 2. Chapter picker --------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsChaptersScreen(
    mode: FlashcardMode,
    onChapterClick: (Int, Boolean) -> Unit,
    onBack: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    var dueOnly by remember { mutableStateOf(false) }
    val revision = ReviewStore.revision
    val dueCounts = remember(revision) {
        val today = ReviewStore.todayEpochDay()
        ChapterRegistry.all.map { ReviewStore.dueCount(mode, it, today) }
    }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = { Text("Study ${mode.label.lowercase()}", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                FilterChip(
                    selected = dueOnly,
                    onClick = { dueOnly = !dueOnly },
                    label = { Text("Due cards only") },
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }
            itemsIndexed(ChapterRegistry.all) { index, chapter ->
                ChapterCard(
                    chapter = chapter,
                    onClick = { onChapterClick(index, dueOnly) },
                    modifier = Modifier.padding(horizontal = 20.dp),
                    dueCount = dueCounts[index],
                )
            }
        }
    }
}

// ---- 3. Study session ----------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsSessionScreen(
    mode: FlashcardMode,
    chapter: PhysicsChapter,
    dueOnly: Boolean,
    onExit: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    // sessionKey re-seeds everything for "Study again".
    var sessionKey by remember { mutableIntStateOf(0) }
    val deck = remember(sessionKey) {
        val all = cardsFor(mode, chapter)
        val today = ReviewStore.todayEpochDay()
        val dealt = if (dueOnly) {
            all.filter { ReviewStore.isDue(mode, chapter.title, it.front, today) }
        } else all
        dealt.shuffled()
    }
    var index by remember(sessionKey) { mutableIntStateOf(0) }
    var correct by remember(sessionKey) { mutableIntStateOf(0) }
    var flipped by remember(sessionKey) { mutableStateOf(false) }

    val finished = index >= deck.size

    fun judge(remembered: Boolean) {
        ReviewStore.record(mode, chapter.title, deck[index].front, remembered)
        if (remembered) correct++
        index++
        flipped = false
    }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (finished) "Results" else "Card ${index + 1} of ${deck.size}",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                actions = {
                    IconButton(onClick = onExit) {
                        Icon(Icons.Default.Close, contentDescription = "Quit")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (deck.isEmpty()) {
                // Only reachable with the due-only filter: nothing is scheduled today.
                Column(
                    modifier = Modifier.fillMaxSize().padding(top = 64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "All caught up!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.primary,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "No cards in this chapter are due for review.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(36.dp))
                    OutlinedButton(
                        onClick = onExit,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                    ) {
                        Text("Done", style = MaterialTheme.typography.titleMedium)
                    }
                }
            } else if (finished) {
                ScoreView(
                    correct = correct,
                    total = deck.size,
                    onRetry = { sessionKey++ },
                    onDone = onExit,
                )
            } else {
                LinearProgressIndicator(
                    progress = { index / deck.size.toFloat() },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(24.dp))

                // Keying on index fixes an answer leak: the outgoing card stays
                // frozen on its revealed back and slides away while the incoming
                // card composes fresh on its front, instead of swapping content mid-flip.
                AnimatedContent(
                    targetState = index,
                    transitionSpec = {
                        (slideInHorizontally { it / 2 } + fadeIn())
                            .togetherWith(slideOutHorizontally { -it / 2 } + fadeOut())
                    },
                ) { cardIndex ->
                    FlipCard(
                        card = deck[cardIndex],
                        mode = mode,
                        // Exiting instances stay pinned on their back; only the live card follows the flip state.
                        flipped = if (cardIndex == index) flipped else true,
                        onFlip = { if (cardIndex == index) flipped = !flipped },
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Judging is only offered once the answer has been revealed.
                AnimatedVisibility(visible = flipped, enter = fadeIn(), exit = fadeOut()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        FilledTonalButton(
                            onClick = { judge(false) },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = colors.errorContainer,
                                contentColor = colors.onErrorContainer,
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f).height(52.dp),
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null)
                            Spacer(Modifier.height(0.dp))
                            Text("  Missed it")
                        }
                        FilledTonalButton(
                            onClick = { judge(true) },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = colors.primaryContainer,
                                contentColor = colors.onPrimaryContainer,
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f).height(52.dp),
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Text("  Got it")
                        }
                    }
                }
                AnimatedVisibility(visible = !flipped, enter = fadeIn(), exit = fadeOut()) {
                    Text(
                        text = "Tap the card to reveal",
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

/** The flippable card: front = prompt, back = answer, 3-D rotation on tap. */
@Composable
private fun FlipCard(
    card: Flashcard,
    mode: FlashcardMode,
    flipped: Boolean,
    onFlip: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(durationMillis = 450),
    )

    Card(
        onClick = onFlip,
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density // perspective depth for the flip
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.outlineVariant),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (rotation <= 90f) {
                // Front: the prompt.
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = if (mode == FlashcardMode.Equations) "EQUATION" else "TERM",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = card.front,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        color = colors.onSurface,
                    )
                }
            } else {
                // Back: the answer — counter-rotated so it isn't mirrored.
                Column(
                    modifier = Modifier
                        .graphicsLayer { rotationY = 180f }
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = card.front,
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(20.dp))
                    if (card.backIsFormula) {
                        FormulaText(
                            formula = card.back,
                            style = MaterialTheme.typography.headlineSmall,
                            color = colors.primary,
                            textAlign = TextAlign.Center,
                        )
                    } else {
                        Text(
                            text = card.back,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = colors.onSurface,
                        )
                    }
                }
            }
        }
    }
}

/** End-of-deck score: ratio of self-declared correct answers. */
@Composable
private fun ScoreView(
    correct: Int,
    total: Int,
    onRetry: () -> Unit,
    onDone: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val percent = if (total == 0) 0 else (100f * correct / total).roundToInt()
    val verdict = when {
        percent >= 90 -> "Excellent recall!"
        percent >= 70 -> "Solid work."
        percent >= 50 -> "Getting there."
        else -> "Keep practicing — repetition is the trick."
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "$percent%",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = if (percent >= 70) colors.primary else colors.onSurface,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "You remembered $correct of $total",
            style = MaterialTheme.typography.titleMedium,
            color = colors.onSurfaceVariant,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = verdict,
            style = MaterialTheme.typography.bodyLarge,
            color = colors.onSurfaceVariant,
        )
        Spacer(Modifier.height(36.dp))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp),
        ) {
            Text("Study again", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = onDone,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp),
        ) {
            Text("Done", style = MaterialTheme.typography.titleMedium)
        }
    }
}
