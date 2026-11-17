package com.gitgames.tvdebloat.data

import android.graphics.drawable.Drawable

data class TvInput(val id: String, val label: String)

data class InstalledApp(val packageName: String, val label: String, val icon: Drawable)
