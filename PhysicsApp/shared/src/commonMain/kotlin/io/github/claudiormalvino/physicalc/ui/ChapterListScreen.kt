package io.github.claudiormalvino.physicalc.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.claudiormalvino.physicalc.physics.ChapterRegistry
import io.github.claudiormalvino.physicalc.physics.PhysicsChapter

/*
 * Home screen: hero header + the list of chapters.
 *
 * Compose learning notes:
 *  - A @Composable function *describes* UI; Compose re-runs it when state changes.
 *  - LazyColumn is a RecyclerView/virtualized list — only visible items are composed.
 *  - State you want to survive recomposition lives in `remember { ... }`.
 */

@Composable
fun ChapterListScreen(
    onChapterClick: (Int) -> Unit,
    onConverterClick: () -> Unit,
) {
    val chapters = ChapterRegistry.all

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { HeroHeader() }

        item { SectionLabel("Tools") }

        item {
            ToolCard(
                title = "Unit Converter",
                subtitle = "Length, time, mass, force, energy, pressure, speed",
                glyph = "⇄",
                onClick = onConverterClick,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }

        item { SectionLabel("Chapters") }

        itemsIndexed(chapters) { index, chapter ->
            ChapterCard(
                chapter = chapter,
                onClick = { onChapterClick(index) },
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }
    }
}

/** Small uppercase section heading between groups of cards. */
@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        modifier = Modifier.padding(start = 24.dp, top = 8.dp),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

/** A tappable tool entry styled like the chapter cards, with a glyph badge. */
@Composable
private fun ToolCard(
    title: String,
    subtitle: String,
    glyph: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
    )

    Card(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.outlineVariant),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(colors.secondaryContainer, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = glyph,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSecondaryContainer,
                )
            }

            Spacer(Modifier.size(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = colors.onSurfaceVariant,
            )
        }
    }
}

/** Big gradient title block at the top of the home screen. */
@Composable
private fun HeroHeader() {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                // Vertical fade from a deep blue into the app background —
                // gives the header depth without any image assets.
                Brush.verticalGradient(
                    colors = listOf(colors.primaryContainer.copy(alpha = 0.55f), colors.background),
                )
            )
    ) {
        Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 56.dp, bottom = 28.dp)) {
            Text(
                text = "Physicalc",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Interactive reference & equation solvers",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onSurfaceVariant,
            )
        }
    }
}

/** One tappable chapter row: number badge, title, description, equation/definition counts. */
@Composable
private fun ChapterCard(
    chapter: PhysicsChapter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme

    // "Ch.3 - Motion Along a Straight Line"  ->  number "3", name "Motion Along a Straight Line"
    val number = chapter.title.removePrefix("Ch.").substringBefore(" ").trim('-', ' ')
    val name = chapter.title.substringAfter("- ")

    // Press feedback: watch the card's interaction state and ease its scale down
    // a touch while pressed. `animateFloatAsState` springs between values for us.
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
    )
    val borderGlow by animateColorAsState(
        targetValue = if (pressed) colors.primary.copy(alpha = 0.6f) else colors.outlineVariant,
    )

    Card(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderGlow),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Circular badge with a glyph capturing the chapter's topic
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(colors.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                ChapterIcon(
                    chapterNumber = number.toIntOrNull() ?: 0,
                    tint = colors.onPrimaryContainer,
                    modifier = Modifier.size(28.dp),
                )
            }

            Spacer(Modifier.size(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface,
                )
                // Defensive: never render an empty gap if a chapter lacks a description.
                if (chapter.description.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = chapter.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "${chapter.equations.size} equations  •  ${chapter.definitions.size} definitions",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.tertiary,
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = colors.onSurfaceVariant,
            )
        }
    }
}
