package com.example.alphabetlauncher.utils

import com.example.alphabetlauncher.model.AppInfo
import kotlin.math.exp

const val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
const val STAR_CHAR = '☆'
const val DOT_CHAR = '•'
const val TOTAL_BAR_ITEMS = 28 // 1 star + 26 letters + 1 dot

/**
 * Calculates the vertical center in pixels for a given item index in the Alphabet bar.
 */
fun calculateItemCenterY(
    index: Int,
    totalHeight: Float,
    topPadding: Float,
    bottomPadding: Float,
    itemCount: Int = TOTAL_BAR_ITEMS
): Float {
    if (totalHeight <= topPadding + bottomPadding || itemCount <= 0) {
        return topPadding
    }
    val usableHeight = totalHeight - topPadding - bottomPadding
    val itemHeight = usableHeight / itemCount.toFloat()
    return topPadding + (index + 0.5f) * itemHeight
}

/**
 * Calculates which item index (0 until itemCount) is closest to the given touch Y position.
 */
fun calculateItemIndex(
    touchY: Float,
    totalHeight: Float,
    topPadding: Float,
    bottomPadding: Float,
    itemCount: Int = TOTAL_BAR_ITEMS
): Int {
    if (totalHeight <= topPadding + bottomPadding || itemCount <= 0) {
        return 0
    }
    val usableHeight = totalHeight - topPadding - bottomPadding
    val itemHeight = usableHeight / itemCount.toFloat()
    val relativeY = touchY - topPadding
    val rawIndex = (relativeY / itemHeight).toInt()
    return rawIndex.coerceIn(0, itemCount - 1)
}

/**
 * Returns the character corresponding to an item index in the bar:
 * Index 0 -> Star
 * Index 1..26 -> 'A'..'Z'
 * Index 27 -> Dot
 */
fun getItemChar(index: Int): Char {
    return when {
        index == 0 -> STAR_CHAR
        index in 1..26 -> ALPHABET[index - 1]
        else -> DOT_CHAR
    }
}

/**
 * Maps a touch Y position to the corresponding character.
 */
fun calculateCharForTouch(
    touchY: Float,
    totalHeight: Float,
    topPadding: Float,
    bottomPadding: Float
): Char {
    val index = calculateItemIndex(touchY, totalHeight, topPadding, bottomPadding)
    return getItemChar(index)
}

/**
 * Calculates horizontal curve deflection (in pixels) using a Gaussian bell curve.
 * Letters closest to fingerY have the largest deflection.
 */
fun calculateCurveDeflection(
    itemCenterY: Float,
    fingerY: Float,
    maxDeflection: Float,
    sigma: Float
): Float {
    if (sigma <= 0f) return 0f
    val distance = itemCenterY - fingerY
    val influence = exp(-(distance * distance) / (2f * sigma * sigma))
    return maxDeflection * influence
}

/**
 * Calculates scale multiplier for letter magnification along the curve.
 */
fun calculateCurveScale(
    itemCenterY: Float,
    fingerY: Float,
    maxScaleIncrease: Float,
    sigma: Float
): Float {
    if (sigma <= 0f) return 1f
    val distance = itemCenterY - fingerY
    val influence = exp(-(distance * distance) / (2f * sigma * sigma))
    return 1f + maxScaleIncrease * influence
}

/**
 * Groups apps by their uppercase first character ('A'..'Z').
 * Apps starting with numbers or symbols are grouped under DOT_CHAR.
 */
fun groupAppsByLetter(apps: List<AppInfo>): Map<Char, List<AppInfo>> {
    return apps
        .filter { it.name.isNotBlank() }
        .groupBy {
            val firstChar = it.name.trim().first().uppercaseChar()
            if (firstChar in 'A'..'Z') firstChar else DOT_CHAR
        }
        .mapValues { (_, value) ->
            value.sortedBy { it.name.lowercase() }
        }
}

/**
 * Returns the set of letters (including DOT_CHAR if applicable) that have at least one installed app.
 */
fun getAvailableLetters(apps: List<AppInfo>): Set<Char> {
    return groupAppsByLetter(apps).keys
}

/**
 * Retrieves the list of apps starting with the specified letter.
 */
fun getAppsForLetter(
    appsByLetter: Map<Char, List<AppInfo>>,
    letter: Char
): List<AppInfo> {
    return appsByLetter[letter] ?: emptyList()
}