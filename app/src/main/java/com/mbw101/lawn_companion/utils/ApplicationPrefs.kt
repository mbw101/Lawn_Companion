package com.mbw101.lawn_companion.utils

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.mbw101.lawn_companion.ui.MyApplication

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-13
 */
class ApplicationPrefs {
    private val mPreferences: SharedPreferences = MyApplication.applicationContext().getSharedPreferences(
        Constants.APPLICATION_PREFS, 0
    )

    fun isNotFirstTime(): Boolean {
        return mPreferences.getBoolean(Constants.IS_FIRST_TIME, false)
    }

    fun setNotFirstTime(b: Boolean) {
        val mEditor = mPreferences.edit()
        mEditor.putBoolean(Constants.IS_FIRST_TIME, b).apply()
    }

    // functions for the preference screen
    // these functions will use getDefaultSharedPreferences, so we can't use mPreferences
    fun isInCuttingSeason(): Boolean {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.applicationContext())
        return preferences.getBoolean("inSeason", true)
    }

    fun isNotificationsEnabled(): Boolean {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.applicationContext())
        return preferences.getBoolean("notificationSwitch", true)
    }

    fun isDataUseEnabled(): Boolean {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.applicationContext())
        return preferences.getBoolean("useData", true)
    }

    // TODO: Add functions for desiredCutDate in shared preferences
}