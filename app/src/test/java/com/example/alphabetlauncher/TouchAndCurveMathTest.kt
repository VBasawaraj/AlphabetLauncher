package com.example.alphabetlauncher

import com.example.alphabetlauncher.utils.ALPHABET
import com.example.alphabetlauncher.utils.DOT_CHAR
import com.example.alphabetlauncher.utils.STAR_CHAR
import com.example.alphabetlauncher.utils.calculateCharForTouch
import com.example.alphabetlauncher.utils.calculateCurveDeflection
import com.example.alphabetlauncher.utils.calculateCurveScale
import com.example.alphabetlauncher.utils.calculateItemCenterY
import com.example.alphabetlauncher.utils.calculateItemIndex
import com.example.alphabetlauncher.utils.getItemChar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TouchAndCurveMathTest {

    private val totalHeight = 1000f
    private val topPadding = 100f
    private val bottomPadding = 100f
    private val usableHeight = 800f
    private val itemHeight = usableHeight / 28f // ~28.57f

    @Test
    fun calculateItemIndex_mapsTouchCorrectly() {
        // Touch exactly at top padding should select item 0 (Star)
        val indexTop = calculateItemIndex(topPadding + 1f, totalHeight, topPadding, bottomPadding)
        assertEquals(0, indexTop)

        // Touch near center
        val centerTouch = topPadding + usableHeight / 2f
        val centerIndex = calculateItemIndex(centerTouch, totalHeight, topPadding, bottomPadding)
        assertEquals(14, centerIndex)

        // Touch below bottom should clamp to 27 (Dot)
        val bottomTouch = totalHeight + 100f
        val bottomIndex = calculateItemIndex(bottomTouch, totalHeight, topPadding, bottomPadding)
        assertEquals(27, bottomIndex)

        // Touch above top should clamp to 0
        val aboveTopTouch = 10f
        val aboveTopIndex = calculateItemIndex(aboveTopTouch, totalHeight, topPadding, bottomPadding)
        assertEquals(0, aboveTopIndex)
    }

    @Test
    fun getItemChar_returnsCorrectSymbols() {
        assertEquals(STAR_CHAR, getItemChar(0))
        assertEquals('A', getItemChar(1))
        assertEquals('B', getItemChar(2))
        assertEquals('Z', getItemChar(26))
        assertEquals(DOT_CHAR, getItemChar(27))
    }

    @Test
    fun calculateCharForTouch_mapsToAlphabet() {
        // Star
        val starTouch = topPadding + itemHeight * 0.5f
        assertEquals(STAR_CHAR, calculateCharForTouch(starTouch, totalHeight, topPadding, bottomPadding))

        // 'A'
        val aTouch = topPadding + itemHeight * 1.5f
        assertEquals('A', calculateCharForTouch(aTouch, totalHeight, topPadding, bottomPadding))

        // 'Z'
        val zTouch = topPadding + itemHeight * 26.5f
        assertEquals('Z', calculateCharForTouch(zTouch, totalHeight, topPadding, bottomPadding))

        // Dot
        val dotTouch = topPadding + itemHeight * 27.5f
        assertEquals(DOT_CHAR, calculateCharForTouch(dotTouch, totalHeight, topPadding, bottomPadding))
    }

    @Test
    fun calculateCurveDeflection_peaksAtFinger() {
        val fingerY = 500f
        val maxDeflection = 60f
        val sigma = 70f

        // Center item (distance = 0)
        val peakDeflection = calculateCurveDeflection(fingerY, fingerY, maxDeflection, sigma)
        assertEquals(maxDeflection, peakDeflection, 0.001f)

        // Item 50px away
        val nearDeflection = calculateCurveDeflection(fingerY + 50f, fingerY, maxDeflection, sigma)
        assertTrue(nearDeflection < peakDeflection)
        assertTrue(nearDeflection > 0f)

        // Symmetric
        val symmetricDeflection = calculateCurveDeflection(fingerY - 50f, fingerY, maxDeflection, sigma)
        assertEquals(nearDeflection, symmetricDeflection, 0.001f)

        // Far away item (e.g. 400px away) should have negligible deflection
        val farDeflection = calculateCurveDeflection(fingerY + 400f, fingerY, maxDeflection, sigma)
        assertTrue(farDeflection < 0.1f)
    }

    @Test
    fun calculateCurveScale_magnifiesNearestLetter() {
        val fingerY = 500f
        val sigma = 70f
        val scaleIncrease = 0.35f

        val peakScale = calculateCurveScale(fingerY, fingerY, scaleIncrease, sigma)
        assertEquals(1.35f, peakScale, 0.001f)

        val farScale = calculateCurveScale(fingerY + 500f, fingerY, scaleIncrease, sigma)
        assertEquals(1.0f, farScale, 0.01f)
    }
}
