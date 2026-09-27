package com.example.alphabetlauncher.data

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.example.alphabetlauncher.model.AppInfo

class AppRepository(
    private val context: Context
) {

    fun loadLaunchableApps(): List<AppInfo> {
        val packageManager = context.packageManager

        val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = packageManager.queryIntentActivities(launcherIntent, 0)

        return resolveInfos
            .mapNotNull { resolveInfo ->
                val activityInfo = resolveInfo.activityInfo ?: return@mapNotNull null
                val packageName = activityInfo.packageName
                val appName = resolveInfo.loadLabel(packageManager)?.toString()?.trim()
                    ?: return@mapNotNull null

                if (appName.isEmpty()) {
                    return@mapNotNull null
                }

                val icon = resolveInfo.loadIcon(packageManager)
                val iconBitmap = try {
                    icon.toBitmap(width = 120, height = 120).asImageBitmap()
                } catch (e: Exception) {
                    null
                }

                AppInfo(
                    name = appName,
                    packageName = packageName,
                    icon = icon,
                    iconBitmap = iconBitmap
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.name.lowercase() }
    }
}