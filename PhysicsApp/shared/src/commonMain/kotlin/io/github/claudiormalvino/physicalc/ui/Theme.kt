package io.github.claudiormalvino.physicalc.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/*
 * App-wide Material 3 theme.
 *
 * Material 3 works from a "color scheme" — a set of named roles (primary, surface,
 * background, ...) that every component reads from. Change a color here and the
 * entire app updates. This replaces your appearance.tcss from the TUI.
 */

// A deep-space palette: dark blue-black background, periwinkle primary, violet accents.
private val SpaceDark = darkColorScheme(
    primary = Color(0xFF8AB4FF),            // interactive elements, formulas
    onPrimary = Color(0xFF062B52),
    primaryContainer = Color(0xFF274468),   // chips, badges
    onPrimaryContainer = Color(0xFFD7E3FF),

    secondary = Color(0xFFBCA6FF),          // secondary accents
    onSecondary = Color(0xFF2A1A55),
    secondaryContainer = Color(0xFF3B2D63),
    onSecondaryContainer = Color(0xFFE9DDFF),

    tertiary = Color(0xFF77D9C9),           // small highlights (counts, success)
    onTertiary = Color(0xFF003730),

    background = Color(0xFF0B0F1A),         // app background
    onBackground = Color(0xFFE4E7F0),

    surface = Color(0xFF111726),            // cards, sheets
    onSurface = Color(0xFFE4E7F0),
    surfaceVariant = Color(0xFF1A2336),     // slightly raised surfaces
    onSurfaceVariant = Color(0xFFA9B3C8),   // secondary text

    outline = Color(0xFF45516B),
    outlineVariant = Color(0xFF273249),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
)

@Composable
fun PhysicsTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) SpaceDark else SpaceLight,
        content = content,
    )
}

// Light counterpart: same blue/violet brand, paper-white surfaces.
private val SpaceLight = lightColorScheme(
    primary = Color(0xFF2F5DA8),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD7E3FF),
    onPrimaryContainer = Color(0xFF0A2D5C),

    secondary = Color(0xFF5F4BA8),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE9DDFF),
    onSecondaryContainer = Color(0xFF211352),

    tertiary = Color(0xFF006A60),
    onTertiary = Color(0xFFFFFFFF),

    background = Color(0xFFF8F9FE),
    onBackground = Color(0xFF1A1C22),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1C22),
    surfaceVariant = Color(0xFFE6EAF4),
    onSurfaceVariant = Color(0xFF44495A),

    outline = Color(0xFF74798C),
    outlineVariant = Color(0xFFC4C9D8),

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)
