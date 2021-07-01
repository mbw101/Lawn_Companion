package com.mbw101.lawn_companion.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.ui.MainActivity

/**
Lawn Companion
Created by Malcolm Wright
Date: June 29th, 2021
 */

object NotificationHelper {

    /**
     * Creates the notification channels for API 26+
     * Uses package name + channel name to create unique channelId's
     */
    fun createNotificationChannel(context: Context, importance: Int, showBadge: Boolean, name: String, description: String) {
        // check android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // create the channel info
            // this info will be displayed inside the app's settings
            val channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            // finally, use that channel info and create the channel using a notification manager
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createCutNotification(context: Context, title: String, message: String, autoCancel: Boolean) {
        // create unique channel id for the app
        val channelId = "${context.packageName}-${context.getString(R.string.app_name)}"

        // build the notification
        val notificationBuilder = NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(R.drawable.ic_baseline_home_24) // TODO: Replace with app logo later
            setContentTitle(title) // title for notification
            setContentText(message) // content
//            setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            priority = NotificationCompat.PRIORITY_DEFAULT // default
            setAutoCancel(autoCancel) // auto cancels notification when taped

            // create intent to open main activity
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            // attaches intent
            setContentIntent(pendingIntent)
        }

        val notificationManager = NotificationManagerCompat.from(context)

        notificationManager.notify(1001, notificationBuilder.build())
    }
}