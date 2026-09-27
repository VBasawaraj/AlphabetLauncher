package com.example.alphabetlauncher.model

import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap

data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: Drawable? = null,
    val iconBitmap: ImageBitmap? = null
)