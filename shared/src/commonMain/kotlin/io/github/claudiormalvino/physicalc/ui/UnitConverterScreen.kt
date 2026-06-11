package io.github.claudiormalvino.physicalc.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.claudiormalvino.physicalc.physics.UnitConverters
import kotlin.math.abs
import kotlin.math.round

/*
 * Unit converter. The result is derived state — recomputed from
 * (value, fromUnit, toUnit) on every recomposition — so it updates live as you type.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun UnitConverterScreen(onBack: () -> Unit) {
    val colors = MaterialTheme.colorScheme

    var quantity by remember { mutableStateOf(UnitConverters.all.first()) }
    var valueText by remember { mutableStateOf("") }
    var fromUnit by remember { mutableStateOf(quantity.baseUnit) }
    var toUnit by remember { mutableStateOf(quantity.units.keys.first { it != quantity.baseUnit }) }

    val parsed = valueText.trim().toDoubleOrNull()
    val result = parsed?.let { quantity.convert(it, fromUnit, toUnit) }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = { Text("Unit Converter", style = MaterialTheme.typography.titleLarge) },
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

            // Quantity selector
            Column {
                Text(
                    text = "Quantity",
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    UnitConverters.all.forEach { q ->
                        FilterChip(
                            selected = q.name == quantity.name,
                            onClick = {
                                quantity = q
                                // Reset units to sensible defaults for the new quantity
                                fromUnit = q.baseUnit
                                toUnit = q.units.keys.first { it != q.baseUnit }
                            },
                            label = { Text(q.name) },
                        )
                    }
                }
            }

            // Value input
            OutlinedTextField(
                value = valueText,
                onValueChange = { valueText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Value") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = valueText.isNotBlank() && parsed == null,
                supportingText = {
                    if (valueText.isNotBlank() && parsed == null) Text("Not a valid number")
                },
            )

            // From / To unit pickers with a swap button between them
            UnitDropdown(
                label = "From",
                selected = fromUnit,
                options = quantity.units.keys.toList(),
                onSelect = { fromUnit = it },
            )

            OutlinedButton(
                onClick = {
                    val tmp = fromUnit
                    fromUnit = toUnit
                    toUnit = tmp
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(50),
            ) {
                Text("⇅  Swap")
            }

            UnitDropdown(
                label = "To",
                selected = toUnit,
                options = quantity.units.keys.toList(),
                onSelect = { toUnit = it },
            )

            // Live result card
            AnimatedVisibility(
                visible = result != null,
                enter = scaleIn(initialScale = 0.95f) + fadeIn(),
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
                        Text(
                            text = "${valueText.trim()} $fromUnit =",
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.onPrimaryContainer.copy(alpha = 0.7f),
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${formatNumber(result ?: 0.0)} $toUnit",
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = colors.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

/** Read-only text field that opens a dropdown of unit options. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitDropdown(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

/** Compact display: trim float noise, fall back to scientific notation at the extremes. */
private fun formatNumber(value: Double): String {
    if (value == 0.0) return "0"
    val magnitude = abs(value)
    if (magnitude >= 1e9 || magnitude < 1e-6) return value.toString() // scientific notation
    val rounded = (round(value * 1e8) / 1e8).toString()
    return if ('E' in rounded || 'e' in rounded) rounded else rounded.trimEnd('0').trimEnd('.')
}
