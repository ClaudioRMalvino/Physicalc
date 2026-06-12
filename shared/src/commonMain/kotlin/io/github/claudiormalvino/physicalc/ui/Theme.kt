package io.github.claudiormalvino.physicalc.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Stroke colors for the vector canvas. The Material accent roles are too close
 * in hue and darkness to tell three arrows apart on the light theme, so each
 * theme gets a dedicated hue-separated trio. The light set is the high-contrast
 * Okabe–Ito subset (deep blue / vermilion / bluish green) — colorblind-safe and
 * the classic navy / terracotta / forest pairing for a cream background.
 */
data class VectorAccents(val a: Color, val b: Color, val resultant: Color)

private val VectorAccentsDark = VectorAccents(
    a = Color(0xFF8AB4FF),         // periwinkle, matches dark primary
    b = Color(0xFFBCA6FF),         // violet, matches dark secondary
    resultant = Color(0xFF77D9C9), // teal, matches dark tertiary
)

private val VectorAccentsLight = VectorAccents(
    a = Color(0xFF0072B2),         // deep blue
    b = Color(0xFFD55E00),         // vermilion
    resultant = Color(0xFF009E73), // bluish green
)

@Composable
fun vectorAccents(): VectorAccents =
    if (AppSettings.darkTheme) VectorAccentsDark else VectorAccentsLight

/* App-wide Material 3 theme. */

// Deep-space palette: dark blue-black background, periwinkle primary, violet accents.
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

// Light counterpart: same blue/violet brand on warm paper-cream neutrals, not clinical white.
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

    background = Color(0xFFF2F0E3),       // paper cream
    onBackground = Color(0xFF21211C),      // warm near-black

    surface = Color(0xFFFAF9F0),           // cards: a lighter cream, lifted off the page
    onSurface = Color(0xFF21211C),
    surfaceVariant = Color(0xFFE7E4D3),     // warm raised surfaces (banners, chips)
    onSurfaceVariant = Color(0xFF4C4A40),   // warm secondary text

    outline = Color(0xFF7A776A),
    outlineVariant = Color(0xFFD2CFBE),

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)
