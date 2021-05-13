package com.mbw101.lawn_companion.utils

import android.content.SharedPreferences
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

    // TODO: Add functions for desiredCutDate in shared preferences
}