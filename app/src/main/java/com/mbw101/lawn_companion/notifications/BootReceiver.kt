package com.mbw101.lawn_companion.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mbw101.lawn_companion.ui.MainActivity

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            // create notification channel
            MainActivity.createNotificationChannel(context)
            MainActivity.setupNotificationAlarmManager()
        }
    }
}