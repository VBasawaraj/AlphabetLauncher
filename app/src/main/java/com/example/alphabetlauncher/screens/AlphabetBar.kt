package com.example.alphabetlauncher.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabetlauncher.utils.DOT_CHAR
import com.example.alphabetlauncher.utils.HapticHelper
import com.example.alphabetlauncher.utils.STAR_CHAR
import com.example.alphabetlauncher.utils.TOTAL_BAR_ITEMS
import com.example.alphabetlauncher.utils.calculateCurveDeflection
import com.example.alphabetlauncher.utils.calculateCurveScale
import com.example.alphabetlauncher.utils.calculateItemCenterY
import com.example.alphabetlauncher.utils.calculateItemIndex
import com.example.alphabetlauncher.utils.getItemChar
import kotlin.math.roundToInt

@Composable
fun AlphabetBar(
    selectedLetter: Char?,
    availableLetters: Set<Char>,
    onLetterSelected: (Char) -> Unit,
    onRelease: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val context = LocalContext.current
    val hapticHelper = remember { HapticHelper(context) }

    var currentTouchY by remember { mutableFloatStateOf(0f) }
    var isPressed by remember { mutableStateOf(false) }
    var lastScrubbedChar by remember { mutableStateOf<Char?>(null) }

    val bendProgress = remember { Animatable(0f) }

    // Instantaneous spring for curve formation, bouncy overshoot upon release
    LaunchedEffect(isPressed) {
        if (isPressed) {
            bendProgress.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessHigh
                )
            )
        } else {
            // Elastic spring overshoot and settle upon release
            bendProgress.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = 0.55f, // Elastic bounce back to straight bar
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .width(100.dp)
            .fillMaxHeight()
    ) {
        val totalHeightPx = constraints.maxHeight.toFloat()
        val topPaddingPx = with(density) { 56.dp.toPx() }
        val bottomPaddingPx = with(density) { 56.dp.toPx() }
        val usableHeightPx = (totalHeightPx - topPaddingPx - bottomPaddingPx).coerceAtLeast(1f)
        val itemHeightPx = usableHeightPx / TOTAL_BAR_ITEMS.toFloat()
        val itemHeightDp = with(density) { itemHeightPx.toDp() }

        val maxDeflectionPx = with(density) { 58.dp.toPx() }
        val sigmaPx = with(density) { 72.dp.toPx() }

        Box(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(totalHeightPx) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val touchY = down.position.y
                        currentTouchY = touchY
                        isPressed = true

                        val index = calculateItemIndex(touchY, totalHeightPx, topPaddingPx, bottomPaddingPx)
                        val char = getItemChar(index)
                        lastScrubbedChar = char
                        hapticHelper.performLetterTick()
                        onLetterSelected(char)
                        down.consume()

                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (!change.pressed) {
                                break
                            }
                            if (change.positionChanged()) {
                                val moveY = change.position.y
                                currentTouchY = moveY // Instant real-time tracking with 0ms delay!
                                val newIndex = calculateItemIndex(moveY, totalHeightPx, topPaddingPx, bottomPaddingPx)
                                val newChar = getItemChar(newIndex)
                                if (newChar != lastScrubbedChar) {
                                    lastScrubbedChar = newChar
                                    hapticHelper.performLetterTick()
                                    onLetterSelected(newChar)
                                }
                                change.consume()
                            }
                        }

                        isPressed = false
                        lastScrubbedChar = null
                        onRelease()
                    }
                }
        ) {
            // Render all 28 items in the alphabet bar
            for (k in 0 until TOTAL_BAR_ITEMS) {
                val char = getItemChar(k)
                val itemCenterY = calculateItemCenterY(k, totalHeightPx, topPaddingPx, bottomPaddingPx)
                val isAvailable = when (char) {
                    STAR_CHAR, DOT_CHAR -> true
                    else -> availableLetters.contains(char)
                }

                val topOffsetDp = with(density) {
                    (topPaddingPx + k * itemHeightPx).toDp()
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(
                            x = (-16).dp,
                            y = topOffsetDp
                        )
                        .size(width = 32.dp, height = itemHeightDp)
                        .graphicsLayer {
                            // Read currentTouchY directly: zero coroutine overhead, instant 60/120fps tracking!
                            val currentFinger = currentTouchY
                            val progress = bendProgress.value

                            val deflection = calculateCurveDeflection(
                                itemCenterY = itemCenterY,
                                fingerY = currentFinger,
                                maxDeflection = maxDeflectionPx,
                                sigma = sigmaPx
                            )

                            val scale = calculateCurveScale(
                                itemCenterY = itemCenterY,
                                fingerY = currentFinger,
                                maxScaleIncrease = 0.35f,
                                sigma = sigmaPx
                            )

                            translationX = -deflection * progress
                            scaleX = 1f + (scale - 1f) * progress
                            scaleY = 1f + (scale - 1f) * progress

                            // Calculate proximity to finger for lighting up
                            val proximity = (deflection / maxDeflectionPx).coerceIn(0f, 1f) * progress
                            val baseAlpha = if (isAvailable) 0.88f else 0.28f
                            alpha = (baseAlpha + (1f - baseAlpha) * proximity).coerceIn(0.2f, 1f)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val fontSize = when (char) {
                        STAR_CHAR -> 18.sp
                        DOT_CHAR -> 20.sp
                        else -> 14.sp
                    }

                    val fontWeight = when (char) {
                        STAR_CHAR, DOT_CHAR -> FontWeight.Bold
                        else -> if (selectedLetter == char) FontWeight.Bold else FontWeight.Normal
                    }

                    Text(
                        text = char.toString(),
                        color = Color.White,
                        fontSize = fontSize,
                        fontWeight = fontWeight
                    )
                }
            }

            // Letter Bubble enlarged next to the curved bar
            val showBubble = isPressed && selectedLetter != null
            val bubbleRadiusPx = with(density) { 33.dp.toPx() }
            val minBubbleY = topPaddingPx
            val maxBubbleY = (totalHeightPx - bottomPaddingPx - bubbleRadiusPx * 2).coerceAtLeast(minBubbleY)

            AnimatedVisibility(
                visible = showBubble,
                enter = scaleIn(
                    initialScale = 0.4f,
                    animationSpec = spring(
                        dampingRatio = 0.6f,
                        stiffness = Spring.StiffnessHigh
                    )
                ) + fadeIn(),
                exit = scaleOut(
                    targetScale = 0.4f,
                    animationSpec = spring(stiffness = Spring.StiffnessMedium)
                ) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset {
                        val clampedY = (currentTouchY - bubbleRadiusPx).coerceIn(minBubbleY, maxBubbleY)
                        IntOffset(
                            x = with(density) { (-88).dp.roundToPx() },
                            y = clampedY.roundToInt()
                        )
                    }
            ) {
                if (selectedLetter != null) {
                    LetterBubble(
                        letter = selectedLetter
                    )
                }
            }
        }
    }
}