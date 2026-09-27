package com.example.alphabetlauncher.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.alphabetlauncher.model.AppInfo

@Composable
fun AppList(
    apps: List<AppInfo>,
    modifier: Modifier = Modifier,
    isFavorite: ((String) -> Boolean)? = null,
    onToggleFavorite: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    var selectedAppForMenu by remember { mutableStateOf<AppInfo?>(null) }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = apps,
            key = { it.packageName }
        ) { app ->
            AppRow(
                app = app,
                onAppClick = {
                    launchApp(context, app.packageName)
                },
                onAppLongClick = {
                    selectedAppForMenu = app
                }
            )
        }
    }

    selectedAppForMenu?.let { app ->
        val isFav = isFavorite?.invoke(app.packageName) ?: false

        AlertDialog(
            onDismissRequest = { selectedAppForMenu = null },
            containerColor = androidx.compose.ui.graphics.Color(0xFF1E1E1E),
            titleContentColor = androidx.compose.ui.graphics.Color.White,
            textContentColor = androidx.compose.ui.graphics.Color.White,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val iconBitmap = app.iconBitmap ?: remember(app.packageName) {
                        try {
                            app.icon?.toBitmap(100, 100)?.asImageBitmap()
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (iconBitmap != null) {
                        Image(
                            bitmap = iconBitmap,
                            contentDescription = app.name,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    Text(
                        text = app.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = app.packageName,
                        fontSize = 12.sp,
                        color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextButton(
                        onClick = {
                            onToggleFavorite?.invoke(app.packageName)
                            val msg = if (isFav) "Removed from favourites" else "Added to favourites"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            selectedAppForMenu = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isFav) "★ Remove from Favourites" else "☆ Add to Favourites",
                            color = androidx.compose.ui.graphics.Color.White,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    TextButton(
                        onClick = {
                            openAppDetails(context, app.packageName)
                            selectedAppForMenu = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "ℹ App Info",
                            color = androidx.compose.ui.graphics.Color.White,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    TextButton(
                        onClick = {
                            launchApp(context, app.packageName)
                            selectedAppForMenu = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "▶ Open App",
                            color = androidx.compose.ui.graphics.Color.White,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedAppForMenu = null }) {
                    Text("Close", color = androidx.compose.ui.graphics.Color.White)
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppRow(
    app: AppInfo,
    onAppClick: () -> Unit,
    onAppLongClick: () -> Unit
) {
    val iconBitmap = app.iconBitmap ?: remember(app.packageName) {
        try {
            app.icon?.toBitmap(100, 100)?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .combinedClickable(
                onClick = onAppClick,
                onLongClick = onAppLongClick
            )
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconBitmap != null) {
            Image(
                bitmap = iconBitmap,
                contentDescription = app.name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }

        Text(
            text = app.name,
            color = androidx.compose.ui.graphics.Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(start = 18.dp)
        )
    }
}

private fun launchApp(context: Context, packageName: String) {
    try {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
        } else {
            Toast.makeText(context, "Cannot open app", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

private fun openAppDetails(context: Context, packageName: String) {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Cannot open app details", Toast.LENGTH_SHORT).show()
    }
}