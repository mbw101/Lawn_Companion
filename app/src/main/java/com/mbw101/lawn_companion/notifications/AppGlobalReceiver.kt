package com.mbw101.lawn_companion.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import androidx.core.app.NotificationManagerCompat
import com.mbw101.lawn_companion.database.AppDatabaseBuilder
import com.mbw101.lawn_companion.database.CutEntry
import com.mbw101.lawn_companion.database.CutEntryRepository
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

    override fun onReceive(context: Context, intent: Intent) {
        NotificationManagerCompat.from(context).cancel(NotificationHelper.CUT_NOTIFICATION_ID)

        val cutEntry = buildEntry(context)
        addEntryToDatabase(context, cutEntry)
    }

    private fun addEntryToDatabase(context: Context, cutEntry: CutEntry) {
        val dao = AppDatabaseBuilder.getInstance(context.applicationContext).cutEntryDao()
        val repository = CutEntryRepository(dao)
        runBlocking {
            repository.addCut(cutEntry)
        }
    }

    private fun buildEntry(context: Context): CutEntry {
        // use the current time & date
        val cutTimeString = DateUtils.formatDateTime(
            context,
            Calendar.getInstance().timeInMillis,
            DateUtils.FORMAT_SHOW_TIME
        )
        val monthNum = Calendar.getInstance().get(Calendar.MONTH) + 1
        return CutEntry(
            cutTimeString, Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
            Constants.months[monthNum - 1], monthNum
        )
    }
}