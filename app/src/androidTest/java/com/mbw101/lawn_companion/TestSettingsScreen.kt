package com.mbw101.lawn_companion

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.mbw101.lawn_companion.ui.MainActivity
import com.mbw101.lawn_companion.ui.MyApplication
import com.mbw101.lawn_companion.ui.SetDatesActivity
import com.mbw101.lawn_companion.ui.SettingsActivity
import com.mbw101.lawn_companion.utils.ApplicationPrefs
import com.mbw101.lawn_companion.utils.Constants
import com.mbw101.lawn_companion.utils.UtilFunctions
import org.junit.After
import org.junit.Assert.*
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

    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION)

    private val currentYear = UtilFunctions.getCurrentYear()

    companion object {
        fun tapSetCuttingSeasonDates() {
            onView(withId(androidx.preference.R.id.recycler_view))
                .perform(
                    RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                        hasDescendant(withText(R.string.setCuttingSeasonDatesTitle)),
                        click()
                    )
                )
        }
    }

    @Before
    fun setup() {
        Intents.init()
    }

    @Test
    // tests back buttons to see if main activity is shown
    fun testPhysicalBackButton() {
        // hit back button
        pressBack()
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
                    click()
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
    fun testWeatherFrequencyListPreference() {
        // 15 minutes is selected as default
        val prefs = ApplicationPrefs()

        ensureWeatherFrequencyIsDisplayed()

        tapWeatherFrequencyPreference()
        pressItemInListPreferenceMenuWithString("15 minutes")
        assertEquals(prefs.getWeatherCheckFrequency(), "15 minutes")
        assertEquals(prefs.getWeatherCheckFrequencyInMillis(), Constants.FIFTEEN_MINUTES)
        assertEquals(prefs.getWeatherCheckFrequencyInMillis(), (15 * 60 * 1000))

        tapWeatherFrequencyPreference()
        pressItemInListPreferenceMenuWithString("30 minutes")
        assertEquals(prefs.getWeatherCheckFrequency(), "30 minutes")
        assertEquals(prefs.getWeatherCheckFrequencyInMillis(), Constants.THIRTY_MINUTES)
        assertEquals(prefs.getWeatherCheckFrequencyInMillis(), (30 * 60 * 1000))

        tapWeatherFrequencyPreference()
        pressItemInListPreferenceMenuWithString("1 hour")
        assertEquals(prefs.getWeatherCheckFrequency(), "1 hour")
        assertEquals(prefs.getWeatherCheckFrequencyInMillis(), Constants.ONE_HOUR)
        assertEquals(prefs.getWeatherCheckFrequencyInMillis(), (60 * 60 * 1000))

        tapWeatherFrequencyPreference()
        pressItemInListPreferenceMenuWithString("2 hours")
        assertEquals(prefs.getWeatherCheckFrequency(), "2 hours")
        assertEquals(prefs.getWeatherCheckFrequencyInMillis(), Constants.TWO_HOURS)
        assertEquals(prefs.getWeatherCheckFrequencyInMillis(), (120 * 60 * 1000))
    }

    private fun ensureWeatherFrequencyIsDisplayed() {
        onView(withText(R.string.adjustWeatherCheckFrequencyTitle)).check(
            matches(isDisplayed())
        )
    }

    private fun pressItemInListPreferenceMenuWithString(text: String) {
        onView(withText(text))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click())
    }

    private fun tapWeatherFrequencyPreference() {
        onView(withId(androidx.preference.R.id.recycler_view))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(R.string.adjustWeatherCheckFrequencyTitle)),
                    click()
                )
            )
    }

    @Test
    fun testDesiredCutFrequency() {
        // 1 week is the default
        val DEFAULT_CUT_FREQUENCY_VALUE = 7
        val prefs = ApplicationPrefs()

        ensureDesiredCutIsDisplayed()

        tapDesiredCutFrequency()
        assertEquals(prefs.getDesiredCutFrequency(), DEFAULT_CUT_FREQUENCY_VALUE)

        pressItemInListPreferenceMenuWithString("5 days")
        assertEquals(prefs.getDesiredCutFrequency(), 5)

        tapDesiredCutFrequency()
        pressItemInListPreferenceMenuWithString("1 week")
        assertEquals(prefs.getDesiredCutFrequency(), 7)

        tapDesiredCutFrequency()
        pressItemInListPreferenceMenuWithString("1.5 weeks (~10 days)")
        assertEquals(prefs.getDesiredCutFrequency(), 10)

        tapDesiredCutFrequency()
        pressItemInListPreferenceMenuWithString("2 weeks")
        assertEquals(prefs.getDesiredCutFrequency(), 14)

        tapDesiredCutFrequency()
        pressItemInListPreferenceMenuWithString("3 weeks")
        assertEquals(prefs.getDesiredCutFrequency(), 21)

        tapDesiredCutFrequency()
        pressItemInListPreferenceMenuWithString("1 month")
        assertEquals(prefs.getDesiredCutFrequency(), 30)
    }

    private fun ensureDesiredCutIsDisplayed() {
        onView(withText(R.string.desiredCutFrequencyTitle)).check(
            matches(isDisplayed())
        )
    }


    private fun tapDesiredCutFrequency() {
        onView(withId(androidx.preference.R.id.recycler_view))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(R.string.desiredCutFrequencyTitle)),
                    click()
                )
            )
    }

    @Test
    fun testInTimeOfDayCheck() {
        // mornings, afternoons, and evenings are true by default while nights are false
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val prefs = ApplicationPrefs()
        when (currentHour) {
            in Constants.MORNING_HOUR_START_TIME..Constants.MORNING_HOUR_END_TIME -> {
                assertEquals(prefs.isInTimeOfDay(), true)
                tapMorningsPreference()
                assertEquals(prefs.isInTimeOfDay(), false)
            }
            in Constants.AFTERNOON_HOUR_START_TIME..Constants.AFTERNOON_HOUR_END_TIME -> {
                assertEquals(prefs.isInTimeOfDay(), true)
                tapAfternoonPreference()
                assertEquals(prefs.isInTimeOfDay(), false)
            }
            in Constants.EVENING_HOUR_START_TIME..Constants.EVENING_HOUR_END_TIME -> {
                assertEquals(prefs.isInTimeOfDay(), true)
                tapEveningsPreference()
                assertEquals(prefs.isInTimeOfDay(), false)
            }
            in Constants.NIGHT_HOUR_START_TIME downTo Constants.NIGHT_HOUR_END_TIME -> {
                assertEquals(prefs.isInTimeOfDay(), false)
                tapNightsPreference()
                assertEquals(prefs.isInTimeOfDay(), true)
            }
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
                    click()
                )
            )
    }

    @Test
    fun testSetDatesSetting() {
        tapSetCuttingSeasonDates()
        ensureSetDatesActivityIsShown()
        onView(withId(R.id.startDateSelector)).perform(click())
        TestSetDatesActivity.setDate(currentYear, Calendar.MARCH + 1, 14)
        onView(withId(R.id.endDateSelector)).perform(click())
        TestSetDatesActivity.setDate(currentYear, Calendar.OCTOBER + 1, 31)

        onView(withId(R.id.startDateSelector)).check(matches(withText("$currentYear/3/14")))
        onView(withId(R.id.endDateSelector)).check(matches(withText("$currentYear/10/31")))

        // test formatting again after closing the activity
        onView(withId(R.id.saveDatesButton)).perform(click())
        Thread.sleep(1000)
        tapSetCuttingSeasonDates()
        Thread.sleep(200)
        onView(withId(R.id.startDateSelector)).check(matches(withText("$currentYear/3/14")))
        onView(withId(R.id.endDateSelector)).check(matches(withText("$currentYear/10/31")))
    }

    private fun ensureSetDatesActivityIsShown() {
        Intents.intended(IntentMatchers.hasComponent(hasClassName(SetDatesActivity::class.java.name)))
    }

    @Test
    fun testNotificationPreferenceHelper() {
        val notificationTitle = MyApplication.applicationContext().getString(R.string.notificationsTitle)
        val prefs = ApplicationPrefs()
        assertTrue(prefs.areNotificationsEnabled())
        onView(withText(notificationTitle)).perform(click())
        assertFalse(prefs.areNotificationsEnabled())
        onView(withText(notificationTitle)).perform(click())
    }

    @Test
    fun testWifiPreferenceHelper() {
        val dataAccessTitle = MyApplication.applicationContext().getString(R.string.weatherDataAccessTitle)
        val prefs = ApplicationPrefs()
        assertTrue(prefs.isDataUseEnabled())
        onView(withText(dataAccessTitle)).perform(click())
        assertFalse(prefs.isDataUseEnabled())
        onView(withText(dataAccessTitle)).perform(click())
    }

    @Test
    fun testInCuttingSeasonPreferenceHelper() {
        val cuttingSeasonTitle = MyApplication.applicationContext().getString(R.string.cuttingSeasonTitle)
        val prefs = ApplicationPrefs()
        assertTrue(prefs.isInCuttingSeason())
        onView(withText(cuttingSeasonTitle)).perform(click())
        assertFalse(prefs.isInCuttingSeason())
        onView(withText(cuttingSeasonTitle)).perform(click())
    }

    @After
    fun release() {
        Intents.release()
    }
}