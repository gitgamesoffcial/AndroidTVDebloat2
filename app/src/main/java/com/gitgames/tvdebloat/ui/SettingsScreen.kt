package com.gitgames.tvdebloat.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.gitgames.tvdebloat.data.AppPreferences
import com.gitgames.tvdebloat.data.InstalledApp
import com.gitgames.tvdebloat.data.TvInput
import com.gitgames.tvdebloat.ui.theme.TvColors
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun SettingsScreen(
    inputs: List<TvInput>,
    apps: List<InstalledApp>,
    bootTarget: String,
    pinnedInputId: String?,
    hiddenPackages: Set<String>,
    onBootTargetChange: (String) -> Unit,
    onPinnedInputChange: (String?) -> Unit,
    onToggleAppVisibility: (String) -> Unit,
    onClose: () -> Unit,
) {
    BackHandler(onBack = onClose)

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(TvColors.Background),
        contentPadding = PaddingValues(horizontal = 56.dp, vertical = 40.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.displaySmall,
                    color = TvColors.TextPrimary,
                    modifier = Modifier.weight(1f),
                )
                ActionChip(label = "Done", onClick = onClose)
            }
        }

        item { SettingsHeading("Screen on boot") }
        item {
            OptionRow(
                title = "Home",
                selected = bootTarget == AppPreferences.BOOT_HOME,
                onClick = { onBootTargetChange(AppPreferences.BOOT_HOME) },
            )
        }
        items(inputs, key = { "boot-${it.id}" }) { input ->
            OptionRow(
                title = "Open ${input.label}",
                selected = bootTarget == input.id,
                onClick = { onBootTargetChange(input.id) },
            )
        }

        item { SettingsHeading("Pinned input") }
        item {
            OptionRow(
                title = "None",
                selected = pinnedInputId == null,
                onClick = { onPinnedInputChange(null) },
            )
        }
        items(inputs, key = { "pin-${it.id}" }) { input ->
            OptionRow(
                title = input.label,
                selected = pinnedInputId == input.id,
                onClick = { onPinnedInputChange(input.id) },
            )
        }

        item { SettingsHeading("Apps on home") }
        items(apps, key = { "app-${it.packageName}" }) { app ->
            val shown = app.packageName !in hiddenPackages
            OptionRow(
                title = app.label,
                detail = if (shown) "Shown" else "Hidden",
                selected = shown,
                onClick = { onToggleAppVisibility(app.packageName) },
            )
        }
    }
}

@Composable
private fun SettingsHeading(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.titleSmall,
        color = TvColors.Accent,
        modifier = Modifier.padding(top = 16.dp),
    )
}

@Composable
private fun OptionRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    detail: String? = null,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = ClickableSurfaceDefaults.shape(shape = RoundedCornerShape(12.dp)),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = if (selected) TvColors.SurfaceFocused else TvColors.Surface,
            contentColor = TvColors.TextPrimary,
            focusedContainerColor = TvColors.Accent,
            focusedContentColor = TvColors.TextPrimary,
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.02f),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(2.dp, TvColors.TextPrimary),
                shape = RoundedCornerShape(12.dp),
            ),
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = detail ?: if (selected) "Selected" else "",
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) TvColors.Accent else TvColors.TextMuted,
            )
        }
    }
}
