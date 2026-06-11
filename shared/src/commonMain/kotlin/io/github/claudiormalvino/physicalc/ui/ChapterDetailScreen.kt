package io.github.claudiormalvino.physicalc.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.claudiormalvino.physicalc.physics.Equation
import io.github.claudiormalvino.physicalc.physics.PhysicsChapter

/*
 * Chapter detail: Equations / Definitions tabs. Equation cards expand to
 * reveal their variables; calculable ones offer an "Open solver" button.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterDetailScreen(
    chapter: PhysicsChapter,
    onOpenCalculator: (equationIndex: Int) -> Unit,
    onBack: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    var tab by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = chapter.title.substringAfter("- "),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            TabRow(
                selectedTabIndex = tab,
                containerColor = Color.Transparent,
            ) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Equations") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Definitions") })
            }

            AnimatedContent(
                targetState = tab,
                transitionSpec = {
                    val towardLeft = targetState > initialState
                    val enter = slideInHorizontally { full -> if (towardLeft) full / 3 else -full / 3 } + fadeIn()
                    val exit = slideOutHorizontally { full -> if (towardLeft) -full / 3 else full / 3 } + fadeOut()
                    enter togetherWith exit
                },
            ) { currentTab ->
                if (currentTab == 0) {
                    EquationsTab(chapter, onOpenCalculator)
                } else {
                    DefinitionsTab(chapter)
                }
            }
        }
    }
}

@Composable
private fun EquationsTab(
    chapter: PhysicsChapter,
    onOpenCalculator: (Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        itemsIndexed(chapter.equations) { index, equation ->
            EquationCard(
                equation = equation,
                onOpenCalculator = if (equation.calculation != null) {
                    { onOpenCalculator(index) }
                } else null,
            )
        }
    }
}

/**
 * One equation card. Expandable — with chevron — only when there is something
 * to reveal; reference-only formulas render as plain, non-clickable cards.
 */
@Composable
private fun EquationCard(
    equation: Equation,
    onOpenCalculator: (() -> Unit)?,
) {
    val colors = MaterialTheme.colorScheme
    val hasDetails = equation.variables.isNotEmpty() || onOpenCalculator != null

    var expanded by remember { mutableStateOf(false) }
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
    )

    Card(
        onClick = { if (hasDetails) expanded = !expanded },
        enabled = hasDetails, // no ripple/click feedback on cards with nothing to show
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface,
            // Keep the disabled (reference-only) cards looking identical to the rest.
            disabledContainerColor = colors.surface,
            disabledContentColor = colors.onSurface,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow))
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = equation.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(4.dp))
                    FormulaText(
                        formula = equation.formula,
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.primary,
                    )
                }

                if (onOpenCalculator != null) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = colors.primaryContainer,
                    ) {
                        Text(
                            text = "solver",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.onPrimaryContainer,
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                }

                if (hasDetails) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        modifier = Modifier.rotate(chevronRotation),
                        tint = colors.onSurfaceVariant,
                    )
                }
            }

            if (expanded) {
                if (equation.variables.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = colors.outlineVariant)
                    Spacer(Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        equation.variables.forEach { (symbol, description) ->
                            Row {
                                // Same renderer as the formula, so symbols match exactly.
                                FormulaText(
                                    formula = symbol,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = colors.secondary,
                                    modifier = Modifier.width(64.dp),
                                )
                                Text(
                                    text = description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                if (onOpenCalculator != null) {
                    Spacer(Modifier.height(14.dp))
                    FilledTonalButton(
                        onClick = onOpenCalculator,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Open solver")
                    }
                }
            }
        }
    }
}

@Composable
private fun DefinitionsTab(chapter: PhysicsChapter) {
    val colors = MaterialTheme.colorScheme
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(chapter.definitions) { definition ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = definition.term,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.secondary,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = definition.meaning,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
