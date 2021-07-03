package com.mbw101.lawn_companion.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mbw101.lawn_companion.R

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-01
 */

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null && intent.action != null) {

            if (intent.action!!.equals(context.getString(R.string.markAsCut), ignoreCase = true)) {
                NotificationHelper.createCutNotification(context, context.getString(R.string.app_name),
                    context.getString(R.string.cutSuggestionMessage), true)
            }
        }
    }

}