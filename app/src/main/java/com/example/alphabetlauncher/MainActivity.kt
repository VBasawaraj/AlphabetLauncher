package com.example.alphabetlauncher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.alphabetlauncher.screens.LauncherScreen
import com.example.alphabetlauncher.ui.theme.AlphabetLauncherTheme
import com.example.alphabetlauncher.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {

    private lateinit var launcherViewModel: LauncherViewModel

    private val packageChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            launcherViewModel.reloadApps()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        launcherViewModel = ViewModelProvider(this)[LauncherViewModel::class.java]

        // Register broadcast receiver for live app install/uninstall updates
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
        registerReceiver(packageChangeReceiver, filter)

        setContent {
            AlphabetLauncherTheme {
                LauncherScreen(
                    viewModel = launcherViewModel
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(packageChangeReceiver)
        } catch (_: Exception) {
        }
    }
}