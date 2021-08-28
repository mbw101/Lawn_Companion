package com.mbw101.lawn_companion

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.mbw101.lawn_companion.ui.MainActivity
import com.mbw101.lawn_companion.ui.MyApplication
import com.mbw101.lawn_companion.ui.SettingsActivity
import com.mbw101.lawn_companion.utils.ApplicationPrefs
import com.mbw101.lawn_companion.utils.Constants
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*


/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-06-26
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class TestSettingsScreen {
    @get:Rule
    val settingsActivityTestRule: ActivityTestRule<SettingsActivity> = ActivityTestRule(SettingsActivity::class.java)

    @Before
    fun setup() {
        Intents.init()
    }

    @Test
    // tests back buttons to see if main activity is shown
    fun testPhysicalBackButton() {
        // hit back button
        Espresso.pressBack()
        // test to see if main activity appeared on screen
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }

    @Test
    fun testCreateNewLawnLocationVisibility() {
        tapCreateLawnLocationPreference()
        TestMainScreen.ensureSaveLocationActivityIsShown()
    }

    private fun tapCreateLawnLocationPreference() {
        onView(withId(androidx.preference.R.id.recycler_view))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(R.string.createNewLocationSummary)),
                    ViewActions.click()
                ))
    }

    @Test
    fun testTimeOfDayCheckboxPreference() {
        // mornings, afternoons, and evenings are true by default while nights are false
        val prefs = ApplicationPrefs()
        assertEquals(prefs.areMorningsSelected(), true)
        tapMorningsPreference()
        assertEquals(prefs.areMorningsSelected(), false)

        assertEquals(prefs.areAfternoonsSelected(), true)
        tapAfternoonPreference()
        assertEquals(prefs.areAfternoonsSelected(), false)

        assertEquals(prefs.areEveningsSelected(), true)
        tapEveningsPreference()
        assertEquals(prefs.areEveningsSelected(), false)

        assertEquals(prefs.areNightsSelected(), false)
        tapNightsPreference()
        assertEquals(prefs.areNightsSelected(), true)
    }

    @Test
    fun testInTimeOfDayCheck() {
        // mornings, afternoons, and evenings are true by default while nights are false
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val prefs = ApplicationPrefs()
        if (currentHour in Constants.MORNING_HOUR_START_TIME..Constants.MORNING_HOUR_END_TIME) {
            assertEquals(prefs.isInTimeOfDay(), true)
            tapMorningsPreference()
            assertEquals(prefs.isInTimeOfDay(), false)
        }
        else if (currentHour in Constants.AFTERNOON_HOUR_START_TIME..Constants.AFTERNOON_HOUR_END_TIME) {
            assertEquals(prefs.isInTimeOfDay(), true)
            tapAfternoonPreference()
            assertEquals(prefs.isInTimeOfDay(), false)
        }
        else if (currentHour in Constants.EVENING_HOUR_START_TIME..Constants.EVENING_HOUR_END_TIME) {
            assertEquals(prefs.isInTimeOfDay(), true)
            tapEveningsPreference()
            assertEquals(prefs.isInTimeOfDay(), false)
        }
        else if (currentHour in Constants.NIGHT_HOUR_START_TIME downTo Constants.NIGHT_HOUR_END_TIME) {
            assertEquals(prefs.isInTimeOfDay(), false)
            tapNightsPreference()
            assertEquals(prefs.isInTimeOfDay(), true)
        }
    }

    private fun tapNightsPreference() {
        val nightTitle = MyApplication.applicationContext().getString(R.string.nightTimeOfDayTitle)
        swipeUpOnPreferences()
        tapSpecificCheckboxPreference(nightTitle)
    }

    private fun swipeUpOnPreferences() {
        onView(withId(androidx.preference.R.id.recycler_view)).perform(swipeUp())
    }

    private fun tapEveningsPreference() {
        val eveningsTitle = MyApplication.applicationContext().getString(R.string.eveningTimeOfDayTitle)
        tapSpecificCheckboxPreference(eveningsTitle)
    }

    private fun tapAfternoonPreference() {
        val afternoonTitle = MyApplication.applicationContext().getString(R.string.afternoonTimeOfDayTitle)
        tapSpecificCheckboxPreference(afternoonTitle)
    }

    private fun tapMorningsPreference() {
        val morningTitle = MyApplication.applicationContext().getString(R.string.morningTimeOfDayTitle)
        tapSpecificCheckboxPreference(morningTitle)
    }

    private fun tapSpecificCheckboxPreference(title: String) {
        onView(withId(androidx.preference.R.id.recycler_view))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(title)),
                    ViewActions.click()
                )
            )
    }

    @After
    fun release() {
        Intents.release()
    }
}