package com.example.alphabetlauncher.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabetlauncher.model.AppInfo
import com.example.alphabetlauncher.utils.DOT_CHAR
import com.example.alphabetlauncher.utils.STAR_CHAR
import com.example.alphabetlauncher.viewmodel.LauncherViewModel

@Composable
fun LauncherScreen(
    viewModel: LauncherViewModel
) {
    val selectedLetter by viewModel.selectedLetter.collectAsState()
    val favouriteApps by viewModel.favouriteApps.collectAsState()
    val availableLetters by viewModel.availableLetters.collectAsState()
    val allApps by viewModel.apps.collectAsState()
    val isSearchActive by viewModel.isSearchActive.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    // Handle back button when in search or letter scrub
    BackHandler(enabled = isSearchActive || selectedLetter != null) {
        if (isSearchActive) {
            viewModel.setSearchActive(false)
        } else if (selectedLetter != null) {
            viewModel.clearSelection()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Transition ONLY between resting Home Screen and active Letter Screen.
        // Scrubbing across letters updates letter and apps INSTANTLY without triggering screen crossfades!
        val isScrubbing = selectedLetter != null

        AnimatedContent(
            targetState = isScrubbing,
            transitionSpec = {
                fadeIn(animationSpec = tween(150)) togetherWith
                        fadeOut(animationSpec = tween(100))
            },
            label = "ScreenModeTransition",
            modifier = Modifier.fillMaxSize()
        ) { scrubbing ->
            if (!scrubbing) {
                HomeScreen(
                    apps = favouriteApps,
                    onSwipeUp = { viewModel.setSearchActive(true) },
                    isFavorite = { viewModel.isFavorite(it) },
                    onToggleFavorite = { viewModel.toggleFavorite(it) }
                )
            } else {
                val currentLetter = selectedLetter ?: 'A'
                LetterScreen(
                    letter = currentLetter,
                    apps = viewModel.getAppsForSelectedLetter(),
                    isFavorite = { viewModel.isFavorite(it) },
                    onToggleFavorite = { viewModel.toggleFavorite(it) }
                )
            }
        }

        // Pinned A-Z Curved Alphabet Bar on right edge
        AlphabetBar(
            selectedLetter = selectedLetter,
            availableLetters = availableLetters,
            onLetterSelected = { letter ->
                viewModel.selectLetter(letter)
            },
            onRelease = {
                viewModel.clearSelection()
            },
            modifier = Modifier.align(Alignment.CenterEnd)
        )

        // Swipe-up Search Overlay (Bonus Feature)
        SearchOverlay(
            isVisible = isSearchActive,
            searchQuery = searchQuery,
            searchResults = searchResults,
            allApps = allApps,
            onQueryChanged = { viewModel.updateSearchQuery(it) },
            onClose = { viewModel.setSearchActive(false) },
            onToggleFavorite = { viewModel.toggleFavorite(it) },
            isFavorite = { viewModel.isFavorite(it) }
        )
    }
}

@Composable
private fun HomeScreen(
    apps: List<AppInfo>,
    onSwipeUp: () -> Unit,
    isFavorite: (String) -> Boolean,
    onToggleFavorite: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(
                start = 28.dp,
                top = 28.dp,
                end = 76.dp,
                bottom = 16.dp
            )
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -25f) {
                        onSwipeUp()
                    }
                }
            }
    ) {
        ClockSection()

        Spacer(modifier = Modifier.height(32.dp))

        AppList(
            apps = apps,
            modifier = Modifier.weight(1f),
            isFavorite = isFavorite,
            onToggleFavorite = onToggleFavorite
        )

        // Subtle modern swipe-up / search affordance at the bottom
        Surface(
            onClick = onSwipeUp,
            shape = RoundedCornerShape(16.dp),
            color = Color.White.copy(alpha = 0.08f),
            modifier = Modifier
                .padding(top = 10.dp, bottom = 4.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "  Swipe up or tap to search",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun LetterScreen(
    letter: Char,
    apps: List<AppInfo>,
    isFavorite: (String) -> Boolean,
    onToggleFavorite: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(
                start = 32.dp,
                top = 24.dp,
                end = 76.dp,
                bottom = 16.dp
            )
    ) {
        val headerTitle = when (letter) {
            STAR_CHAR -> "★ Favourites"
            DOT_CHAR -> "# Numbers & Symbols"
            else -> letter.toString()
        }

        Text(
            text = headerTitle,
            color = Color.White,
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (apps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 100.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No apps",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "No installed applications start with '$letter'",
                        color = Color.White.copy(alpha = 0.45f),
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            AppList(
                apps = apps,
                modifier = Modifier.fillMaxSize(),
                isFavorite = isFavorite,
                onToggleFavorite = onToggleFavorite
            )
        }
    }
}