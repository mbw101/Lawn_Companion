package com.mbw101.lawn_companion.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.util.Log
import com.mbw101.lawn_companion.BuildConfig
import com.mbw101.lawn_companion.utils.ApplicationPrefs
import com.mbw101.lawn_companion.utils.Constants
import java.util.*
import java.util.Calendar.*

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-01
 */

// Used for scheduling alarms for cuts
object AlarmScheduler {

    /**
     * Schedules a single alarm
     */
    fun scheduleAlarmManager(dayOfWeek: Int, alarmIntent: PendingIntent?, alarmMgr: AlarmManager) {
        // set up a Calendar object for the alarm's time
        val datetimeToAlarm = getInstance(Locale.getDefault())
        datetimeToAlarm.timeInMillis = System.currentTimeMillis()
        datetimeToAlarm.set(HOUR_OF_DAY, datetimeToAlarm.get(HOUR_OF_DAY)) // set as 9am for now
        datetimeToAlarm.set(MINUTE, datetimeToAlarm.get(MINUTE))
        datetimeToAlarm.set(SECOND, 0)
        datetimeToAlarm.set(MILLISECOND, 0)
        datetimeToAlarm.set(DAY_OF_WEEK, dayOfWeek) // Sunday is set to 1

        Log.e(Constants.TAG, datetimeToAlarm.toString())

        // checks if alarm should be schedule today
        val today = getInstance(Locale.getDefault())
        if (shouldNotifyToday(dayOfWeek, today, datetimeToAlarm)) { // schedules the alarm if so
            // setInexactRepeating saves battery life compared to setRepeating (synchronizes multiple notifications)
            val prefs = ApplicationPrefs()
            if (BuildConfig.DEBUG) {
                alarmMgr.setInexactRepeating(
                    AlarmManager.RTC,
                    datetimeToAlarm.timeInMillis,
                    (1000 * 60 * 2).toLong(), alarmIntent) // (1000 * 60 * 2).toLong()
            }
            else {
                alarmMgr.setInexactRepeating(
                    AlarmManager.RTC,
                    datetimeToAlarm.timeInMillis,
                    prefs.getWeatherCheckFrequencyInMillis().toLong(), alarmIntent)
            }
            return
        }
    }

    // Determines if the Alarm should be scheduled for today.
    private fun shouldNotifyToday(dayOfWeek: Int, today: Calendar, datetimeToAlarm: Calendar): Boolean {
        return dayOfWeek == today.get(DAY_OF_WEEK) &&
                today.get(HOUR_OF_DAY) <= datetimeToAlarm.get(HOUR_OF_DAY) &&
                today.get(MINUTE) <= datetimeToAlarm.get(MINUTE)
    }
}