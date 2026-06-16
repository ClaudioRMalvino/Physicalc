package io.github.claudiormalvino.physicalc.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.claudiormalvino.physicalc.physics.PhysicsChapter
import io.github.claudiormalvino.physicalc.physics.Root

/*
 * Interactive solver: pick the unknown (chips), enter the knowns, solve.
 * The unknown is passed to the chapter's calculation as `null`.
 */

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CalculatorScreen(
    chapter: PhysicsChapter,
    equationIndex: Int,
    onBack: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val equation = chapter.equations[equationIndex]
    val symbols = equation.variables.keys.toList()
    // Variables the solver can invert for; the rest are input-only.
    val solvableSymbols = symbols.filter { equation.solvableFor?.contains(it) ?: true }

    var target by remember { mutableStateOf(solvableSymbols.last()) }   // variable to solve for
    val inputs = remember { mutableStateMapOf<String, String>() } // text typed per symbol
    var roots by remember { mutableStateOf<List<Root>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }

    fun solve() {
        error = null
        roots = emptyList()
        val values = mutableMapOf<String, Double?>()
        for (symbol in symbols) {
            if (symbol == target) {
                values[symbol] = null // the unknown
                continue
            }
            val text = (inputs[symbol] ?: "").trim()
            if (text.isEmpty()) {
                error = "Enter a value for $symbol"
                return
            }
            val parsed = text.toDoubleOrNull()
            if (parsed == null) {
                error = "“$text” is not a valid number"
                return
            }
            values[symbol] = parsed
        }
        roots = try {
            when {
                equation.multiCalculation != null -> equation.multiCalculation!!.invoke(values)
                equation.calculation != null -> listOf(Root(equation.calculation!!.invoke(values)))
                else -> emptyList()
            }
        } catch (e: IllegalArgumentException) {
            error = e.message ?: "These values have no solution"
            emptyList()
        } catch (e: Exception) {
            error = "Could not solve with these values"
            emptyList()
        }
    }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = { Text(equation.name, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            // Formula banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = colors.surfaceVariant,
            ) {
                FormulaText(
                    formula = equation.formula,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp, horizontal = 16.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.primary,
                    textAlign = TextAlign.Center,
                )
            }

            // Solve-for selector
            Column {
                Text(
                    text = "Solve for",
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    solvableSymbols.forEach { symbol ->
                        FilterChip(
                            selected = symbol == target,
                            onClick = {
                                target = symbol
                                roots = emptyList()
                                error = null
                            },
                            label = {
                                // Same renderer as the formula banner, so chip symbols match it.
                                FormulaText(
                                    formula = symbol,
                                    style = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold),
                                )
                            },
                        )
                    }
                }
            }

            // Inputs for every known variable (the unknown is excluded)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                symbols.filter { it != target }.forEach { symbol ->
                    OutlinedTextField(
                        value = inputs[symbol] ?: "",
                        onValueChange = { newText ->
                            inputs[symbol] = newText
                            roots = emptyList()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { FormulaText(symbol) },
                        supportingText = { Text(equation.variables[symbol] ?: "") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    )
                }
            }

            Button(
                onClick = ::solve,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text("Solve for ", style = MaterialTheme.typography.titleMedium)
                FormulaText(target, style = MaterialTheme.typography.titleMedium)
            }

            // Error banner — slides open when present
            AnimatedVisibility(
                visible = error != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = colors.errorContainer,
                ) {
                    Text(
                        text = error ?: "",
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onErrorContainer,
                    )
                }
            }

            // Result card — pops in with a scale + fade. Shows every root when
            // the answer is multi-valued (e.g. the two x's of a trajectory).
            AnimatedVisibility(
                visible = roots.isNotEmpty(),
                enter = scaleIn(initialScale = 0.9f) + fadeIn(),
                exit = fadeOut(),
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.primaryContainer),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        roots.forEachIndexed { index, root ->
                            if (index > 0) Spacer(Modifier.height(14.dp))
                            FormulaText(
                                formula = "$target = ${formatResult(root.value)}",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = colors.onPrimaryContainer,
                            )
                            if (root.label != null) {
                                Text(
                                    text = root.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = colors.onPrimaryContainer.copy(alpha = 0.7f),
                                )
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = equation.variables[target] ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onPrimaryContainer.copy(alpha = 0.7f),
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

/** "3010.0" reads better as "3010"; keep decimals only when they exist. */
private fun formatResult(value: Double): String =
    if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
