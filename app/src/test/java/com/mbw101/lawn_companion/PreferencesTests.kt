package com.mbw101.lawn_companion

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mbw101.lawn_companion.ui.MyApplication
import com.mbw101.lawn_companion.utils.ApplicationPrefs
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class PreferencesTests {

    @Test
    fun testSkipDateString() {
        // test normal case
        val preferences = ApplicationPrefs()
        assertFalse(preferences.shouldSkipNotification())
        preferences.saveSkipDate()
        assertTrue(preferences.shouldSkipNotification())

        // test edge case (save a custom skipDate, that is not the current date, and compare with today's skipDate)
        preferences.clearPreferences()
        assertFalse(preferences.shouldSkipNotification())
        preferences.saveSkipDate("2022-01-11")
        assertFalse(preferences.shouldSkipNotification())
    }

    @Test
    fun testSaveBoolPreference() {
        val preferences = ApplicationPrefs()
        val testKey = MyApplication.applicationContext().getString(R.string.notificationPreferenceKey)

        assertTrue(preferences.getBooleanPreferenceFromSharedPrefs(testKey))
        assertTrue(preferences.areNotificationsEnabled())
        preferences.saveBoolPreferenceValueInSharedPrefs(testKey, false)
        // Causing build to fail on CI github
        assertFalse(preferences.getBooleanPreferenceFromSharedPrefs(testKey))
        assertFalse(preferences.areNotificationsEnabled())
    }
}