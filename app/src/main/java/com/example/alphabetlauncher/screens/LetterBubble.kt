package com.example.alphabetlauncher.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabetlauncher.ui.theme.DarkBubbleBackground

@Composable
fun LetterBubble(
    letter: Char,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(66.dp)
            .shadow(elevation = 10.dp, shape = CircleShape)
            .background(color = DarkBubbleBackground, shape = CircleShape)
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter.toString(),
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}