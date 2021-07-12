package com.mbw101.lawn_companion.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.Data
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.database.CutEntry
import com.mbw101.lawn_companion.database.CutEntryRepository
import com.mbw101.lawn_companion.database.DatabaseBuilder
import com.mbw101.lawn_companion.utils.ApplicationPrefs
import com.mbw101.lawn_companion.utils.Constants
import java.util.*

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-01
 */

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            // TODO: check if eligible for cut via shared preferences/DAO
            // maybe use a onetime worker request to access DAO for the notification log here.
//                DatabaseWorker.run()
//                    .observeForever(object : Observer<WorkInfo> {
//                        override fun onChanged(workInfo: WorkInfo) {
//                            if (workInfo.state == WorkInfo.State.SUCCEEDED) {
//                                val data = workInfo.outputData
//                                if (!isEmptyData(data)) {
//                                    val cal = buildCalendarFromData(data)
//
//                                    showNotification(intent, context)
//                                }
//                            } else if (workInfo.state == WorkInfo.State.FAILED) {
//
//                            }
//                        }
//                    })

            val repository = CutEntryRepository(DatabaseBuilder.getInstance(context).cutEntryDao())
            val observer: Observer<CutEntry> = Observer { lastCut ->
                Log.d(Constants.TAG, "Cut Entry = $lastCut")

                // TODO: Fix last cut entry being null

                // TODO: Figure out how to communicate that lastCut is null in the Data

                // build Data object based on this last cut
//                returnDataBuilder = Data.Builder()
//                    .putInt(DatabaseWorker.MONTH_KEY, lastCut?.month_number ?: 0)
//                    .putInt(DatabaseWorker.DAY_KEY, lastCut?.day_number  ?: 0)
//                    .putString(
//                        DatabaseWorker.HAS_CUT_KEY,
//                        if (lastCut == null) {
//                            DatabaseWorker.EMPTY_MESSAGE
//                        }
//                        else {
//                            DatabaseWorker.FULL_MESSAGE
//                        }
//                    )
            }
            val liveData: LiveData<CutEntry> = repository.getLastCut()
            liveData.observeForever(observer)
            liveData.removeObserver(observer)

//            showNotification(intent, context)

        }
    }

    private fun isEmptyData(data: Data): Boolean {
        if (data.getString(DatabaseWorker.HAS_CUT_KEY) == DatabaseWorker.EMPTY_MESSAGE) return true
        if (data.getInt(DatabaseWorker.DAY_KEY, 0) == 0) return true
        if (data.getInt(DatabaseWorker.MONTH_KEY, 0) == 0) return true
        return false
    }

    private fun buildCalendarFromData(data: Data): Calendar {
        val cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, data.getInt(DatabaseWorker.MONTH_KEY, 1) - 1) // month numbers in Calendar start at 0
        cal.set(Calendar.DAY_OF_MONTH, data.getInt(DatabaseWorker.DAY_KEY, 1))
        return cal
    }

    private fun showNotification(intent: Intent, context: Context) {
        // use the user's preference for notifications/in cutting season
        val preferences = ApplicationPrefs()
        val showNotifications: Boolean = preferences.isNotificationsEnabled()
        val inCuttingSeason: Boolean = preferences.isInCuttingSeason()

        if (showNotifications || inCuttingSeason) {
            NotificationHelper.createCutNotification(
                context, context.getString(R.string.app_name),
                context.getString(R.string.cutSuggestionMessage), true
            )
        }
    }
}