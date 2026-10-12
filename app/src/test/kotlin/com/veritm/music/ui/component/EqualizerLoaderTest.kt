package com.veritm.music.ui.component

import org.junit.Assert.assertTrue
import org.junit.Test

class EqualizerLoaderTest {
    @Test
    fun fractionStaysInRangeForAllPhases() {
        for (i in 0 until 4) {
            var p = 0f
            while (p < 1f) {
                val f = barFraction(i, 4, p)
                assertTrue("fraction $f out of range at $p", f in 0.2f..1.0f)
                p += 0.05f
            }
        }
    }
}
