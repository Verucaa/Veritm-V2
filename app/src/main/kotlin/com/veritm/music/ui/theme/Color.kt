/**
 * VERITM Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.veritm.music.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance

// Core identity palette - neutral near-black minimalist, light-blue accent.
val VeritmBlue = Color(0xFF38BDF8)
val VeritmCyan = Color(0xFF0EA5E9)
val VeritmBackground = Color(0xFF0A0A0A)
val VeritmSurface = Color(0xFF121212)
val VeritmElevated = Color(0xFF1A1A1A)
val VeritmTextPrimary = Color(0xFFFFFFFF)
val VeritmTextSecondary = Color(0xFF9AA3AF)
val VeritmDivider = Color(0xFF262626)

val VeritmDarkColorScheme =
    darkColorScheme(
        primary = VeritmBlue,
        onPrimary = Color(0xFF05202E),
        primaryContainer = Color(0xFF0B3A52),
        onPrimaryContainer = Color(0xFFBAE6FD),
        secondary = VeritmCyan,
        onSecondary = Color(0xFF05202E),
        secondaryContainer = Color(0xFF0C3A50),
        onSecondaryContainer = Color(0xFFA5E4FF),
        tertiary = Color(0xFF7DD3FC),
        onTertiary = Color(0xFF05202E),
        tertiaryContainer = Color(0xFF0B3A52),
        onTertiaryContainer = Color(0xFFDBF0FF),
        background = VeritmBackground,
        onBackground = VeritmTextPrimary,
        surface = VeritmSurface,
        onSurface = VeritmTextPrimary,
        surfaceVariant = VeritmElevated,
        onSurfaceVariant = VeritmTextSecondary,
        surfaceContainerLowest = VeritmBackground,
        surfaceContainerLow = VeritmSurface,
        surfaceContainer = Color(0xFF181818),
        surfaceContainerHigh = Color(0xFF222222),
        surfaceContainerHighest = Color(0xFF2A2A2A),
        surfaceTint = VeritmBlue,
        inverseSurface = Color(0xFFE2E8F0),
        inverseOnSurface = VeritmSurface,
        inversePrimary = Color(0xFF0284C7),
        outline = VeritmDivider,
        outlineVariant = Color(0xFF242424),
        error = Color(0xFFF87171),
        onError = Color(0xFF2A0A0A),
        errorContainer = Color(0xFF601410),
        onErrorContainer = Color(0xFFFFDAD6),
        scrim = Color.Black,
    )

val VeritmLightColorScheme =
    lightColorScheme(
        primary = Color(0xFF0284C7),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFBAE6FD),
        onPrimaryContainer = Color(0xFF053A52),
        secondary = VeritmCyan,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFDBF0FF),
        onSecondaryContainer = Color(0xFF05202E),
        tertiary = Color(0xFF0369A1),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFCFE8FF),
        onTertiaryContainer = Color(0xFF04293F),
        background = Color(0xFFF8FAFC),
        onBackground = Color(0xFF0B1220),
        surface = Color.White,
        onSurface = Color(0xFF0B1220),
        surfaceVariant = Color(0xFFE2E8F0),
        onSurfaceVariant = Color(0xFF475569),
        surfaceContainerLowest = Color.White,
        surfaceContainerLow = Color(0xFFF8FAFC),
        surfaceContainer = Color(0xFFF1F5F9),
        surfaceContainerHigh = Color(0xFFE2E8F0),
        surfaceContainerHighest = Color(0xFFCBD5E1),
        surfaceTint = Color(0xFF0284C7),
        inverseSurface = VeritmSurface,
        inverseOnSurface = VeritmTextPrimary,
        inversePrimary = VeritmBlue,
        outline = Color(0xFF94A3B8),
        outlineVariant = Color(0xFFCBD5E1),
        error = Color(0xFFDC2626),
        onError = Color.White,
        errorContainer = Color(0xFFFEE2E2),
        onErrorContainer = Color(0xFF7F1D1D),
        scrim = Color.Black,
    )

// Override the primary accent family while keeping the VERITM surfaces intact.
internal fun ColorScheme.withAccent(
    accent: Color,
    dark: Boolean,
): ColorScheme {
    val onAccent = if (accent.luminance() > 0.5f) Color(0xFF05202E) else Color.White
    val containerMix = if (dark) 0.35f else 0.18f
    val container = lerp(surface, accent, containerMix)
    val onContainer = if (container.luminance() > 0.5f) Color(0xFF05202E) else Color(0xFFBAE6FD)
    return copy(
        primary = accent,
        onPrimary = onAccent,
        primaryContainer = container,
        onPrimaryContainer = onContainer,
        surfaceTint = accent,
        inversePrimary = accent,
    )
}
