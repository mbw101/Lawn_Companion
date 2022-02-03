package com.mbw101.lawn_companion

import android.content.Context
import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.mbw101.lawn_companion.database.AppDatabase
import com.mbw101.lawn_companion.database.AppDatabaseBuilder
import com.mbw101.lawn_companion.database.CuttingSeasonDatesDao
import com.mbw101.lawn_companion.database.LawnLocationDAO
import com.mbw101.lawn_companion.ui.IntroActivity
import com.mbw101.lawn_companion.ui.MainActivity
import com.mbw101.lawn_companion.ui.SaveLocationActivity
import com.mbw101.lawn_companion.utils.ApplicationPrefs
import com.mbw101.lawn_companion.utils.UtilFunctions
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-08-22
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class TestFirstUse {
    @get:Rule
    val introActivityTestRule: ActivityTestRule<IntroActivity> = ActivityTestRule(IntroActivity::class.java, true, false)

    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION)

    private lateinit var db: AppDatabase
    private lateinit var cuttingSeasonDatesDao: CuttingSeasonDatesDao
    private lateinit var lawnLocationDAO: LawnLocationDAO
    private lateinit var customIntent: Intent

    companion object {
        fun resetAppPreferences() {
            // set not first time to true
            val preferenceManager = ApplicationPrefs()
            preferenceManager.clearPreferences()
        }
    }

    @Before
    fun setup() {
//        AccessibilityChecks.enable().setRunChecksFromRootView(true)
        Intents.init()
        setupDatesDB()
        customIntent = Intent()
        customIntent.putExtra("your_key", "your_value")
    }

    private fun setupDatesDB() {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        db = AppDatabaseBuilder.getInstance(context)
        cuttingSeasonDatesDao = db.cuttingSeasonDatesDao()
        lawnLocationDAO = db.lawnLocationDao()
    }
    
    @Test
    // Sometimes this test doesn't work on emulator due to network location
    fun testHappyPathOfAppNavigation() {
        resetAppPreferences()
        introActivityTestRule.launchActivity(customIntent)
        ensureIntroActivityIsShown()
        pressNavButtons()
        ensureSaveActivityIsShown()
        Thread.sleep(1000)
        pressSaveLocation()
        Thread.sleep(2000)
        pressHomeNavButton()
        ensureMainActivityIsShown()
        compareHappyExpectedOutputs()
        ensureHasDefaultDatesSaved()
        ensureDefaultDatesAreDisplayed()
    }

    private fun pressHomeNavButton() {
        onView(ViewMatchers.withId(R.id.home)).perform(ViewActions.click())
            .check(matches(isDisplayed()))
    }

    @Test
    fun testDenyLocationPathOfAppNavigation() {
        resetAppPreferences()
        introActivityTestRule.launchActivity(customIntent)
        ensureIntroActivityIsShown()
        pressNavButtons()
        ensureSaveActivityIsShown()
        pressDenySaveLocation()
        ensureMainActivityIsShown()
        compareNoLocationExpectedOutputs()
        ensureHasDefaultDatesSaved()
        ensureDefaultDatesAreDisplayed()
    }

    private fun pressNavButtons() {
        onView(ViewMatchers.withId(R.id.nextButton)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.nextButton)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.nextButton)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.getStartedButton)).perform(ViewActions.click())
    }

    private fun pressSaveLocation() {
        onView(ViewMatchers.withId(R.id.acceptSaveLocationButton)).perform(ViewActions.click())
    }

    private fun pressDenySaveLocation() {
        onView(ViewMatchers.withId(R.id.denySaveLocationButton)).perform(ViewActions.click())
    }

    private fun ensureSaveActivityIsShown() {
        Intents.intended(IntentMatchers.hasComponent(SaveLocationActivity::class.java.name))
    }

    private fun ensureMainActivityIsShown() {
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }

    private fun ensureIntroActivityIsShown() {
        Intents.intended(IntentMatchers.hasComponent(IntroActivity::class.java.name))
    }

    private fun compareHappyExpectedOutputs() {
        onView(ViewMatchers.withId(R.id.mainMessageTextView)).check(
            matches(
                isCompletelyDisplayed()
            )
        )
        onView(ViewMatchers.withId(R.id.mainMessageTextView)).check(matches(
            ViewMatchers.withText(CoreMatchers.containsString("No cuts have been made yet. Add a new cut to get started!"))
        ))
        onView(ViewMatchers.withId(R.id.openPermissionsButton)).check(
            matches(not(isDisplayed()))
        )
        onView(ViewMatchers.withId(R.id.createLawnLocationButton)).check(
            matches(not(isDisplayed()))
        )
    }

    private fun ensureHasDefaultDatesSaved() = runBlocking {
        assertEquals(cuttingSeasonDatesDao.hasStartDate(), true)
        assertEquals(cuttingSeasonDatesDao.hasEndDate(), true)

        val startDate = cuttingSeasonDatesDao.getStartDate()
        val endDate = cuttingSeasonDatesDao.getEndDate()

        assertEquals(startDate!!.calendarValue.get(Calendar.MONTH), Calendar.JANUARY)
        assertEquals(startDate.calendarValue.get(Calendar.DAY_OF_MONTH), 1)
        assertEquals(endDate!!.calendarValue.get(Calendar.MONTH), Calendar.DECEMBER)
        assertEquals(endDate.calendarValue.get(Calendar.DAY_OF_MONTH), 31)
    }

    private fun ensureDefaultDatesAreDisplayed() {
        onView(ViewMatchers.withId(R.id.settingsIcon)).perform(ViewActions.click())
        TestSettingsScreen.tapSetCuttingSeasonDates()
        val year = UtilFunctions.getCurrentYear()
        onView(ViewMatchers.withId(R.id.startDateSelector)).check(matches(ViewMatchers.withText("$year/1/1")))
        onView(ViewMatchers.withId(R.id.endDateSelector)).check(matches(ViewMatchers.withText("$year/12/31")))
    }

    private fun compareNoLocationExpectedOutputs() {
        onView(ViewMatchers.withId(R.id.mainMessageTextView)).check(
            matches(
                isCompletelyDisplayed()
            )
        )
        onView(ViewMatchers.withId(R.id.secondaryTextView)).check(
            matches(
                isCompletelyDisplayed()
            )
        )
        onView(ViewMatchers.withId(R.id.openPermissionsButton)).check(
            matches(not(isDisplayed()))
        )
        onView(ViewMatchers.withId(R.id.secondaryTextView)).check(matches(
            ViewMatchers.withText(CoreMatchers.containsString("There is no current lawn location saved. Add a location to receive notifications."))
        ))
        onView(ViewMatchers.withId(R.id.createLawnLocationButton)).check(
            matches(isCompletelyDisplayed())
        )
    }

    @After
    fun release() {
//        AccessibilityChecks.disable()
        Intents.release()
    }
}