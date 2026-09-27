package com.example.alphabetlauncher.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClockSection(
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf(Date()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            delay(1000)
        }
    }

    val timeFormatter = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault())
    }

    val dateFormatter = remember {
        SimpleDateFormat("EEE dd MMM", Locale.getDefault())
    }

    Column(modifier = modifier) {
        Text(
            text = timeFormatter.format(currentTime),
            color = androidx.compose.ui.graphics.Color.White,
            fontSize = 62.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = (-1).sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = dateFormatter.format(currentTime),
            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f),
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal
        )
    }
}