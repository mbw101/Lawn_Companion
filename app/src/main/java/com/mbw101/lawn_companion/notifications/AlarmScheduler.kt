package com.mbw101.lawn_companion.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mbw101.lawn_companion.R
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

    // Creates a pending intent for the alarm
    private fun createPendingIntent(context: Context, day: String?): PendingIntent? {
        // creates intent with AlarmReceiver as the destination
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
            // set action
            action = context.getString(R.string.markAsCut)

            // this has to be unique, so we use day to help
            // if not unique, it will override a pending intent
            type = "$day"
        }

        // we need to use getBroadcast here because a BroadcastReceiver is our target
        return PendingIntent.getBroadcast(context, NotificationHelper.MARK_AS_CUT_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    /**
     * Schedules a single alarm
     */
    fun scheduleAlarm(dayOfWeek: Int, alarmIntent: PendingIntent?, alarmMgr: AlarmManager) {
        // set up a Calendar object for the alarm's time
        val datetimeToAlarm = getInstance(Locale.getDefault())
        datetimeToAlarm.timeInMillis = System.currentTimeMillis()
        // TODO: Implement a setting where they can choose when they'd like notifications
        datetimeToAlarm.set(HOUR_OF_DAY, datetimeToAlarm.get(HOUR_OF_DAY)) // set as 9am for now
        datetimeToAlarm.set(MINUTE, datetimeToAlarm.get(MINUTE))
        datetimeToAlarm.set(SECOND, 0)
        datetimeToAlarm.set(MILLISECOND, 0)
        datetimeToAlarm.set(DAY_OF_WEEK, SATURDAY) // Sunday is set to 1

        Log.e(Constants.TAG, datetimeToAlarm.toString())

        // checks if alarm should be schedule today
        val today = getInstance(Locale.getDefault())
        if (shouldNotifyToday(dayOfWeek, today, datetimeToAlarm)) { // schedules the alarm if so
            // repeat the alarm every 7 days (1 week)
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                datetimeToAlarm.timeInMillis, 60 * 1000, alarmIntent) // (1000 * 60 * 60 * 24 * 7).toLong()
            return
        }

        // otherwise, schedule alarm to repeat every week at that time by adding 1 week to it
        // TODO: Figure out how we'd modify this if the weather isn't suitable
//        datetimeToAlarm.roll(WEEK_OF_YEAR, 1)
//        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
//            datetimeToAlarm.timeInMillis, AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent) // (1000 * 60 * 60 * 24 * 7).toLong()
    }

    // Determines if the Alarm should be scheduled for today.
    private fun shouldNotifyToday(dayOfWeek: Int, today: Calendar, datetimeToAlarm: Calendar): Boolean {
        return dayOfWeek == today.get(DAY_OF_WEEK) &&
                today.get(HOUR_OF_DAY) <= datetimeToAlarm.get(HOUR_OF_DAY) &&
                today.get(MINUTE) <= datetimeToAlarm.get(MINUTE)
    }
}