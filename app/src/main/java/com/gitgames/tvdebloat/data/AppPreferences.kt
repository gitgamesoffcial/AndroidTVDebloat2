package com.gitgames.tvdebloat.data

import android.content.Context
import androidx.core.content.edit

class AppPreferences(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var bootTarget: String
        get() = prefs.getString(KEY_BOOT_TARGET, BOOT_HOME) ?: BOOT_HOME
        set(value) = prefs.edit { putString(KEY_BOOT_TARGET, value) }

    var pinnedInputId: String?
        get() = prefs.getString(KEY_PINNED_INPUT, null)
        set(value) = prefs.edit { putString(KEY_PINNED_INPUT, value) }

    var hiddenPackages: Set<String>
        get() = prefs.getStringSet(KEY_HIDDEN_APPS, emptySet())?.toSet() ?: emptySet()
        set(value) = prefs.edit { putStringSet(KEY_HIDDEN_APPS, value) }

    companion object {
        const val BOOT_HOME = "home"
        private const val PREFS_NAME = "tvdebloat"
        private const val KEY_BOOT_TARGET = "boot_target"
        private const val KEY_PINNED_INPUT = "pinned_input"
        private const val KEY_HIDDEN_APPS = "hidden_apps"
    }
}
