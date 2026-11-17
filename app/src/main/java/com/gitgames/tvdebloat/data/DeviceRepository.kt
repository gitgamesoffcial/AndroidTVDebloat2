package com.gitgames.tvdebloat.data

import android.content.Context
import android.content.Intent
import android.media.tv.TvContract
import android.media.tv.TvInputManager

class DeviceRepository(private val context: Context) {

    private val packageManager = context.packageManager

    fun passthroughInputs(): List<TvInput> {
        val manager = context.getSystemService(Context.TV_INPUT_SERVICE) as? TvInputManager
            ?: return emptyList()
        return manager.tvInputList
            .filter { it.isPassthroughInput }
            .map { TvInput(it.id, it.loadLabel(context)?.toString() ?: it.id) }
    }

    fun launchableApps(): List<InstalledApp> {
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return packageManager.queryIntentActivities(launcherIntent, 0)
            .filter { it.activityInfo.packageName != context.packageName }
            .distinctBy { it.activityInfo.packageName }
            .map {
                InstalledApp(
                    packageName = it.activityInfo.packageName,
                    label = it.loadLabel(packageManager).toString(),
                    icon = it.loadIcon(packageManager),
                )
            }
            .sortedBy { it.label.lowercase() }
    }

    fun launchIntent(packageName: String): Intent? =
        packageManager.getLaunchIntentForPackage(packageName)

    fun inputIntent(inputId: String): Intent =
        Intent(Intent.ACTION_VIEW, TvContract.buildChannelUriForPassthroughInput(inputId))
}
