package com.mbw101.lawn_companion.utils

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.mbw101.lawn_companion.BuildConfig
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.notifications.NotificationHelper
import com.mbw101.lawn_companion.ui.MyApplication
import com.mbw101.lawn_companion.utils.UtilFunctions.allowReads
import java.util.*

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-13
 */
class ApplicationPrefs {
    private val mPreferences: SharedPreferences = MyApplication.applicationContext().getSharedPreferences(
        Constants.APPLICATION_PREFS, 0
    )

    fun hasLocationSaved(): Boolean {
        return mPreferences.getBoolean(Constants.HAS_LOCATION_SAVED, false)
    }

    fun setHasLocationSavedValue(value: Boolean) {
        val mEditor = mPreferences.edit()
        mEditor.putBoolean(Constants.HAS_LOCATION_SAVED, value).apply()
    }

    fun isNotFirstTime(): Boolean {
        return mPreferences.getBoolean(Constants.IS_FIRST_TIME, false)
    }

    fun setNotFirstTime(b: Boolean) {
        val mEditor = mPreferences.edit()
        mEditor.putBoolean(Constants.IS_FIRST_TIME, b).apply()
    }

    /**
     *  compares skip date with current date and returns true
     *  if notification should be skipped
     */
    fun shouldSkipNotification(): Boolean {
        return mPreferences.getString(Constants.SKIP_DATE_KEY, "").equals(NotificationHelper.createSkipDateString())
    }

    fun saveSkipDate(skipDate: String = NotificationHelper.createSkipDateString()) {
        val editor = mPreferences.edit()
        editor.putString(Constants.SKIP_DATE_KEY, skipDate).apply()
    }

    fun clearPreferences() {
        val editor = mPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun getWeatherCheckFrequencyInMillis(): Int {
        val freq = getWeatherCheckFrequency()
        when {
            freq.equals("15 minutes") -> {
                return Constants.FIFTEEN_MINUTES
            }
            freq.equals("30 minutes") -> {
                return Constants.THIRTY_MINUTES
            }
            freq.equals("1 hour") -> {
                return Constants.ONE_HOUR
            }
            freq.equals("2 hours") -> {
                return Constants.TWO_HOURS
            }
            else -> return Constants.FIFTEEN_MINUTES
        }
    }

    fun getWeatherCheckFrequency(): String? {
        val applicationContext =
            if (BuildConfig.DEBUG) {
                allowReads {
                    MyApplication.applicationContext()
                }
            }
            else {
                MyApplication.applicationContext()
            }

        val preferences: SharedPreferences =
            if (BuildConfig.DEBUG) {
                allowReads {
                    PreferenceManager.getDefaultSharedPreferences(
                        applicationContext)
                }
            }
            else {
                PreferenceManager.getDefaultSharedPreferences(
                    applicationContext)
            }
        val defaultWeatherCheckFrequency = "15 minutes"
        return preferences.getString(applicationContext.getString(R.string.weatherCheckFrequencyKey), defaultWeatherCheckFrequency)
    }

    fun getDesiredCutFrequency(): Int {
        val applicationContext = MyApplication.applicationContext()
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val defaultCutFrequency = "7"
        return (preferences.getString(applicationContext.getString(R.string.desiredCutFrequencyKey), defaultCutFrequency)!!.toInt())
    }

    // functions for the preference screen
    // these functions will use getDefaultSharedPreferences, so we can't use mPreferences
    fun isInCuttingSeason(): Boolean {
        return getBooleanPreferenceFromSharedPrefs(MyApplication.applicationContext().getString(R.string.cuttingSeasonKey))
    }

    fun areNotificationsEnabled(): Boolean {
        return getBooleanPreferenceFromSharedPrefs(MyApplication.applicationContext().getString(R.string.notificationPreferenceKey))
    }

    fun isDataUseEnabled(): Boolean {
        return getBooleanPreferenceFromSharedPrefs(MyApplication.applicationContext().getString(R.string.dataPreferenceKey))
    }

    fun isInTimeOfDay(): Boolean {
        val cal = Calendar.getInstance()
        return when (cal.get(Calendar.HOUR_OF_DAY)) {
            in Constants.MORNING_HOUR_START_TIME..Constants.MORNING_HOUR_END_TIME -> {
                areMorningsSelected()
            }
            in Constants.AFTERNOON_HOUR_START_TIME..Constants.AFTERNOON_HOUR_END_TIME -> {
                areAfternoonsSelected()
            }
            in Constants.EVENING_HOUR_START_TIME..Constants.EVENING_HOUR_END_TIME -> {
                areEveningsSelected()
            }
            else -> { // between NIGHT_HOUR_START_TIME downTo Constants.NIGHT_HOUR_END_TIME
                areNightsSelected()
            }
        }
    }

    fun areMorningsSelected(): Boolean {
        return getBooleanPreferenceFromSharedPrefs(MyApplication.applicationContext().getString(R.string.morningTimeOfDayKey))
    }

    fun areAfternoonsSelected(): Boolean {
        return getBooleanPreferenceFromSharedPrefs(MyApplication.applicationContext().getString(R.string.afternoonTimeOfDayKey))
    }

    fun areEveningsSelected(): Boolean {
        return getBooleanPreferenceFromSharedPrefs(MyApplication.applicationContext().getString(R.string.eveningTimeOfDayKey))
    }

    fun areNightsSelected(): Boolean {
        return getBooleanPreferenceFromSharedPrefs(MyApplication.applicationContext().getString(R.string.nightTimeOfDayKey))
    }

    fun saveBoolPreferenceValueInSharedPrefs(preferenceKey: String, value: Boolean) {
        if (BuildConfig.DEBUG) {
            lateinit var preferences: SharedPreferences

            allowReads {
                preferences =
                    PreferenceManager.getDefaultSharedPreferences(MyApplication.applicationContext())
                val mEditor = preferences.edit()
                mEditor.putBoolean(preferenceKey, value).apply()
            }
        }

    }

    /**
     *      Defaults to true for boolean return value
     */
    fun getBooleanPreferenceFromSharedPrefs(preferenceKey: String): Boolean {
        if (BuildConfig.DEBUG) {
            lateinit var preferences: SharedPreferences

            allowReads {
                preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.applicationContext())
            }

            val defaultBooleanPreferenceValue = true
            var booleanPreferenceValue: Boolean = defaultBooleanPreferenceValue
            allowReads {
                booleanPreferenceValue = preferences.getBoolean(preferenceKey, defaultBooleanPreferenceValue)
            }

            return booleanPreferenceValue
        }
        else {
            val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.applicationContext())
            val defaultBooleanPreferenceValue = true
            return preferences.getBoolean(preferenceKey, defaultBooleanPreferenceValue)
        }
    }
}