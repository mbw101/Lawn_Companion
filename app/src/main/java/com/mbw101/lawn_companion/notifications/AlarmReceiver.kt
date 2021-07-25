package com.mbw101.lawn_companion.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.database.CutEntry
import com.mbw101.lawn_companion.database.CutEntryRepository
import com.mbw101.lawn_companion.database.DatabaseBuilder
import com.mbw101.lawn_companion.utils.ApplicationPrefs
import com.mbw101.lawn_companion.utils.UtilFunctions
import kotlinx.coroutines.runBlocking
import java.util.*

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-01
 */

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val preferences = ApplicationPrefs()
            if (notificationsAreEnabled(preferences) && isInCuttingSeason(preferences)) {
                val repository =
                    CutEntryRepository(DatabaseBuilder.getInstance(context).cutEntryDao())

                determineNotificationLogic(repository, intent, context)
            }
        }
    }

    private fun notificationsAreEnabled(preferences: ApplicationPrefs): Boolean {
        return preferences.isNotificationsEnabled()
    }

    private fun isInCuttingSeason(preferences: ApplicationPrefs): Boolean {
        return preferences.isInCuttingSeason()
    }

    private fun determineNotificationLogic(repository: CutEntryRepository, intent: Intent, context: Context) {
        // TODO: Get this to run in a separate thread
        runBlocking {
            val lastCut: CutEntry? = repository.getLastCutSync()

            if (lastCut == null) {
                // just suggest an appropriate cut (given weather) anytime since there is no cut registered
                showNotification(intent, context)
            }
            else {
                // calculate the time since last cut until now
                val daysSince = findDaysSince(lastCut)

                if (daysSince >= 7) {
                    // TODO: Get the weather data and factor that into the notification condition
                    showNotification(intent, context)
                }
            }
        }
    }

    private fun findDaysSince(lastCut: CutEntry): Int {
        val cutDate = Calendar.getInstance()
        cutDate.set(Calendar.MONTH, lastCut.month_number - 1)
        cutDate.set(Calendar.DAY_OF_MONTH, lastCut.day_number)
        return UtilFunctions.getNumDaysSince(cutDate)
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