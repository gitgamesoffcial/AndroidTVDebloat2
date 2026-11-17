package com.gitgames.tvdebloat.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.gitgames.tvdebloat.data.AppPreferences
import com.gitgames.tvdebloat.data.DeviceRepository
import com.gitgames.tvdebloat.data.InstalledApp
import com.gitgames.tvdebloat.data.TvInput
import com.gitgames.tvdebloat.ui.theme.TvColors
import android.graphics.drawable.Drawable
import androidx.compose.foundation.BorderStroke

private const val APPS_PER_ROW = 5
private const val ICON_PX = 128

@Composable
fun HomeScreen(
    repository: DeviceRepository,
    preferences: AppPreferences,
    onLaunchInput: (TvInput) -> Unit,
    onLaunchApp: (InstalledApp) -> Unit,
) {
    val inputs = remember { repository.passthroughInputs() }
    val apps = remember { repository.launchableApps() }
    var hidden by remember { mutableStateOf(preferences.hiddenPackages) }
    var pinned by remember { mutableStateOf(preferences.pinnedInputId) }
    var boot by remember { mutableStateOf(preferences.bootTarget) }
    var showSettings by remember { mutableStateOf(false) }

    if (showSettings) {
        SettingsScreen(
            inputs = inputs,
            apps = apps,
            bootTarget = boot,
            pinnedInputId = pinned,
            hiddenPackages = hidden,
            onBootTargetChange = {
                boot = it
                preferences.bootTarget = it
            },
            onPinnedInputChange = {
                pinned = it
                preferences.pinnedInputId = it
            },
            onToggleAppVisibility = { packageName ->
                hidden = if (packageName in hidden) hidden - packageName else hidden + packageName
                preferences.hiddenPackages = hidden
            },
            onClose = { showSettings = false },
        )
        return
    }

    val visibleApps = apps.filter { it.packageName !in hidden }
    val settingsFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { settingsFocus.requestFocus() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TvColors.Background),
        contentPadding = PaddingValues(horizontal = 56.dp, vertical = 40.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TV Debloat",
                        style = MaterialTheme.typography.displaySmall,
                        color = TvColors.TextPrimary,
                    )
                    Text(
                        text = "Inputs and apps, nothing else",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TvColors.TextMuted,
                    )
                }
                ActionChip(
                    label = "Settings",
                    modifier = Modifier.focusRequester(settingsFocus),
                    onClick = { showSettings = true },
                )
            }
        }

        item { SectionTitle("Inputs") }
        item {
            if (inputs.isEmpty()) {
                EmptyHint("No HDMI inputs detected")
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    items(inputs, key = { it.id }) { input ->
                        TileCard(
                            title = input.label,
                            caption = if (input.id == pinned) "Pinned" else "HDMI input",
                            captionColor = if (input.id == pinned) TvColors.Accent else TvColors.TextMuted,
                            modifier = Modifier.width(260.dp).height(150.dp),
                            onClick = { onLaunchInput(input) },
                        )
                    }
                }
            }
        }

        item { SectionTitle("Apps") }
        if (visibleApps.isEmpty()) {
            item { EmptyHint("No apps selected. Open Settings to choose apps.") }
        } else {
            items(visibleApps.chunked(APPS_PER_ROW)) { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    row.forEach { app ->
                        TileCard(
                            title = app.label,
                            caption = "App",
                            icon = app.icon,
                            modifier = Modifier.weight(1f).height(150.dp),
                            onClick = { onLaunchApp(app) },
                        )
                    }
                    repeat(APPS_PER_ROW - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.titleSmall,
        color = TvColors.Accent,
    )
}

@Composable
private fun EmptyHint(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = TvColors.TextMuted,
    )
}

@Composable
fun ActionChip(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = ClickableSurfaceDefaults.shape(shape = RoundedCornerShape(12.dp)),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = TvColors.Surface,
            contentColor = TvColors.TextPrimary,
            focusedContainerColor = TvColors.Accent,
            focusedContentColor = TvColors.TextPrimary,
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.05f),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 14.dp),
        )
    }
}

@Composable
fun TileCard(
    title: String,
    caption: String,
    modifier: Modifier = Modifier,
    captionColor: Color = TvColors.TextMuted,
    icon: Drawable? = null,
    onClick: () -> Unit,
) {
    val iconBitmap = remember(icon) { icon?.toBitmap(ICON_PX, ICON_PX)?.asImageBitmap() }
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = ClickableSurfaceDefaults.shape(shape = RoundedCornerShape(16.dp)),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = TvColors.Surface,
            contentColor = TvColors.TextPrimary,
            focusedContainerColor = TvColors.SurfaceFocused,
            focusedContentColor = TvColors.TextPrimary,
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.05f),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(3.dp, TvColors.Accent),
                shape = RoundedCornerShape(16.dp),
            ),
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (iconBitmap != null) {
                Image(
                    bitmap = iconBitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(56.dp),
                )
                Spacer(Modifier.height(10.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = caption,
                style = MaterialTheme.typography.labelLarge,
                color = captionColor,
                textAlign = TextAlign.Center,
            )
        }
    }
}
