package com.gitgames.steamlauchertv

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.gitgames.tvdebloat.data.AppPreferences
import com.gitgames.tvdebloat.data.DeviceRepository
import com.gitgames.tvdebloat.data.InstalledApp
import com.gitgames.tvdebloat.data.TvInput
import com.gitgames.tvdebloat.ui.HomeScreen
import com.gitgames.tvdebloat.ui.theme.DebloatTvTheme

class MainActivity : ComponentActivity() {

    private val repository by lazy { DeviceRepository(this) }
    private val preferences by lazy { AppPreferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DebloatTvTheme {
                HomeScreen(
                    repository = repository,
                    preferences = preferences,
                    onLaunchInput = { launch(repository.inputIntent(it.id), "Cannot switch to ${it.label}") },
                    onLaunchApp = { launchApp(it) },
                )
            }
        }
    }

    private fun launchApp(app: InstalledApp) {
        launch(repository.launchIntent(app.packageName), "${app.label} can't be opened")
    }

    private fun launch(intent: Intent?, failureMessage: String) {
        if (intent == null) {
            Toast.makeText(this, failureMessage, Toast.LENGTH_SHORT).show()
            return
        }
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, failureMessage, Toast.LENGTH_SHORT).show()
        }
    }
}
