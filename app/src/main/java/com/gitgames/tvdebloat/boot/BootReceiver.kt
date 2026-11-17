package com.gitgames.tvdebloat.boot

import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.gitgames.tvdebloat.MainActivity
import com.gitgames.tvdebloat.data.AppPreferences
import com.gitgames.tvdebloat.data.DeviceRepository

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val target = AppPreferences(context).bootTarget
        val launch = if (target == AppPreferences.BOOT_HOME) {
            Intent(context, MainActivity::class.java)
        } else {
            DeviceRepository(context).inputIntent(target)
        }.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            context.startActivity(launch)
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
}
