package com.veritm.music.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VeritmColorSchemeTest {
    @Test
    fun defaultAccentIsVeritmBlue() {
        assertEquals(VeritmBlue, DefaultThemeColor)
    }

    @Test
    fun darkSchemeUsesIdentityPalette() {
        val s = VeritmDarkColorScheme
        assertEquals(VeritmBlue, s.primary)
        assertEquals(VeritmBackground, s.background)
        assertEquals(VeritmSurface, s.surface)
        assertEquals(VeritmTextPrimary, s.onSurface)
        assertEquals(VeritmTextSecondary, s.onSurfaceVariant)
    }

    @Test
    fun withAccentOverridesPrimaryOnly() {
        val accented = VeritmDarkColorScheme.withAccent(Color(0xFFD81B60), dark = true)
        assertEquals(Color(0xFFD81B60), accented.primary)
        assertEquals(VeritmDarkColorScheme.surface, accented.surface)
        assertEquals(VeritmDarkColorScheme.background, accented.background)
    }

    @Test
    fun pureBlackClearsBackgroundAndSurface() {
        val b = VeritmDarkColorScheme.pureBlack(true)
        assertEquals(Color.Black, b.background)
        assertEquals(Color.Black, b.surface)
    }

    @Test
    fun accentOnColorHasContrast() {
        val light = VeritmDarkColorScheme.withAccent(VeritmBlue, dark = true)
        val dark = VeritmDarkColorScheme.withAccent(Color(0xFF1A237E), dark = true)
        assertTrue(light.onPrimary.luminance() < 0.5f)
        assertTrue(dark.onPrimary.luminance() > 0.5f)
    }
}
