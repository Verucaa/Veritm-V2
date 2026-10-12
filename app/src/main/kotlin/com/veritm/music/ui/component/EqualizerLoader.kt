/**
 * Veritm Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.veritm.music.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

fun barFraction(index: Int, barCount: Int, phase: Float): Float {
    val wave = (sin(2.0 * PI * (phase - index / barCount.toFloat())) + 1.0) / 2.0
    return 0.2f + 0.8f * wave.toFloat()
}

@Composable
fun EqualizerLoader(
    modifier: Modifier = Modifier.size(40.dp),
    barCount: Int = 4,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    val transition = rememberInfiniteTransition(label = "equalizer")
    val phase by
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = 1200, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "phase",
        )

    Canvas(modifier = modifier) {
        val barWidth = size.width / (barCount * 2 - 1)
        val radius = CornerRadius(barWidth / 2f, barWidth / 2f)
        for (i in 0 until barCount) {
            val barHeight = size.height * barFraction(i, barCount, phase)
            val x = i * barWidth * 2f
            val y = (size.height - barHeight) / 2f
            drawRoundRect(
                color = color,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = radius,
            )
        }
    }
}
