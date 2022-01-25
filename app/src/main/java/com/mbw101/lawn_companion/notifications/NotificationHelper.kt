package com.mbw101.lawn_companion.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.ui.MainActivity
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
Lawn Companion
Created by Malcolm Wright
Date: June 29th, 2021
 */

object NotificationHelper {

    const val CUT_NOTIFICATION_ID = 1
    private const val MARK_AS_CUT_CODE = 2021
    private const val SKIP_CODE = 2022
    private const val TIMEOUT_NOTIFICATION_MINUTES: Long = 10 // represents time a notification stays shown until its dismissed (API 26 and above)

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

    /**
     * Creates a cut notification suggesting a new cut to be added
     * along with an action button for adding a cut by simply
     * tapping it.
     */
    fun createCutNotification(context: Context, title: String, message: String, autoCancel: Boolean) {
        // create unique channel id for the app
        val channelId = "${context.packageName}-${context.getString(R.string.app_name)}"

        // build the notification
        val notificationBuilder =
            getNotificationBuilder(context, channelId, title, message, autoCancel)

        // Add 2 actions: Mark as cut and skip
        val markCutPendingIntent = createPendingIntentForMarkCutAction(context)
        val skipCutPendingIntent = createPendingIntentForSkipAction(context)

        notificationBuilder.addAction(
            R.drawable.indicator_selected,
            context.getString(R.string.markAsCut),
            markCutPendingIntent)

        notificationBuilder.addAction(
            R.drawable.indicator_selected,
            context.getString(R.string.skipCut),
            skipCutPendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setTimeoutAfter(TimeUnit.MINUTES.toMillis(TIMEOUT_NOTIFICATION_MINUTES))
        }
        notificationManager.notify(CUT_NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getNotificationBuilder(context: Context, channelId: String, title: String, message: String, autoCancel: Boolean): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(R.drawable.ic_notificationiconoptimized)
            setContentTitle(title) // title for notification
            setContentText(message) // content
            color = ContextCompat.getColor(context, R.color.medium_spring_green_darker_shade)
//            setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            priority = NotificationCompat.PRIORITY_HIGH
            setAutoCancel(autoCancel) // auto cancels notification when taped

            // create intent to open main activity
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            }
            else {
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }
            // attaches intent for when the user presses the notification itself
            setContentIntent(pendingIntent)
        }
    }

    // TODO: In the future, refactor these 2 methods by making a general function for a pending action and call that with these 2 methods
    // passing in the request code and action name
    /**
     * Creates the pending intent for the "Mark as cut" action on the notification.
     */
    private fun createPendingIntentForMarkCutAction(context: Context): PendingIntent? {
        // create an Intent to update the CutEntry database if "Mark as cut"" action is clicked
        val markCutIntent = Intent(context, AppGlobalReceiver::class.java).apply {
            action = context.getString(R.string.markAsCut)
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(context, MARK_AS_CUT_CODE, markCutIntent, PendingIntent.FLAG_IMMUTABLE)
        }
        else {
            PendingIntent.getBroadcast(context, MARK_AS_CUT_CODE, markCutIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    /**
     * Creates the pending intent for the "Skip until tomorrow" action on the notification.
     */
    private fun createPendingIntentForSkipAction(context: Context): PendingIntent? {
        // create an intent to update the CutEntry database if "Mark as cut"" action is clicked
        val skipCutIntent = Intent(context, AppGlobalReceiver::class.java).apply {
            action = context.getString(R.string.skipCut)
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(context, SKIP_CODE, skipCutIntent, PendingIntent.FLAG_IMMUTABLE)
        }
        else {
            PendingIntent.getBroadcast(context, SKIP_CODE, skipCutIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    /**
     * Returns a string with the format of YYYY-MM-DD.
     * The skip date is the date that a cut notification was skipped
     */
    fun createSkipDateString(): String {
        val dateFormatter = SimpleDateFormat("yyyy-MM-DD", Locale.getDefault())
        val date = Date()
        return dateFormatter.format(date)
    }
}