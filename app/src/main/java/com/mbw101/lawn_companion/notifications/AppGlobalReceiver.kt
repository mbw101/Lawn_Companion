package com.mbw101.lawn_companion.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import androidx.core.app.NotificationManagerCompat
import com.mbw101.lawn_companion.database.CutEntry
import com.mbw101.lawn_companion.database.CutEntryRepository
import com.mbw101.lawn_companion.database.DatabaseBuilder
import com.mbw101.lawn_companion.utils.Constants
import kotlinx.coroutines.runBlocking
import java.util.*

/**
Lawn Companion
Created by Malcolm Wright
Date: July 1st, 2021
 */

// handles the mark as cut action from notification
class AppGlobalReceiver : BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = "notification_id"
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        if (context != null && intent != null) {
            NotificationManagerCompat.from(context).cancel(NotificationHelper.CUT_NOTIFICATION_ID)

            // Debug
//            Toast.makeText(context,
//                "Deleted ID: ${NotificationHelper.CUT_NOTIFICATION_ID}",
//                Toast.LENGTH_SHORT
//            ).show()

            // Add new cut to the database
            val dao = DatabaseBuilder.getInstance(context.applicationContext).cutEntryDao()
            val repository = CutEntryRepository(dao)
            // use the current time & date
            val cutTimeString = DateUtils.formatDateTime(context, Calendar.getInstance().timeInMillis, DateUtils.FORMAT_SHOW_TIME)
            val monthNum = Calendar.getInstance().get(Calendar.MONTH) + 1
            val cutEntry = CutEntry(cutTimeString, Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                Constants.months[monthNum-1], monthNum)
            runBlocking {
                repository.addCut(cutEntry)
            }
        }
    }
}