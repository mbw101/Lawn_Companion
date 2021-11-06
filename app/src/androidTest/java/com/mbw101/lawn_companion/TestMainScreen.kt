package com.mbw101.lawn_companion

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.mbw101.lawn_companion.database.LawnLocation
import com.mbw101.lawn_companion.database.LawnLocationRepository
import com.mbw101.lawn_companion.database.setupLawnLocationRepository
import com.mbw101.lawn_companion.ui.AddCutActivity
import com.mbw101.lawn_companion.ui.MainActivity
import com.mbw101.lawn_companion.ui.SaveLocationActivity
import com.mbw101.lawn_companion.ui.SettingsActivity
import com.mbw101.lawn_companion.utils.ApplicationPrefs
import com.mbw101.lawn_companion.utils.Constants
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*


/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-06-13
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class TestMainScreen {
    private val goodMorning = "Good Morning!"
    private val goodAfternoon = "Good Afternoon!"
    private val goodEvening = "Good Evening!"
    private val goodNight = "Good Night!"
    private lateinit var lawnLocationRepository: LawnLocationRepository

    @get:Rule
    val mainActivityTestRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @get:Rule var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION,
    android.Manifest.permission.ACCESS_COARSE_LOCATION)

    companion object {
        fun ensureSaveLocationActivityIsShown() {
            intended(hasComponent(SaveLocationActivity::class.java.name))
        }

        fun tapRefresh() {
            onView(withId(R.id.refreshIcon)).perform(click())
        }
    }

    @Before
    fun setup() {
        Intents.init()

        setupLocationDB()
        runBlocking {
            createMockLocationEntry()
        }
    }

    private suspend fun createMockLocationEntry() {
        lawnLocationRepository.addLocation(LawnLocation(42.3, 42.2))
        Log.d(Constants.TAG, "# of entries in location DB = ${lawnLocationRepository.getNumEntries()}")
    }

    private fun setupLocationDB() {
        val context: Context = ApplicationProvider.getApplicationContext<Context>()
        lawnLocationRepository = setupLawnLocationRepository(context)
    }

    private fun calculateExpectedMessage(): String {
        val cal: Calendar = Calendar.getInstance()
        // Get time and get corresponding message
        val hourOfDay = cal.get(Calendar.HOUR_OF_DAY)

        return when (hourOfDay) {
            in Constants.MORNING_HOUR_START_TIME..Constants.MORNING_HOUR_END_TIME -> {
                goodMorning
            }
            in Constants.AFTERNOON_HOUR_START_TIME..Constants.AFTERNOON_HOUR_END_TIME -> {
                goodAfternoon
            }
            in Constants.EVENING_HOUR_START_TIME..Constants.EVENING_HOUR_END_TIME -> {
                goodEvening
            }
            else -> goodNight // between NIGHT_HOUR_START_TIME downTo Constants.NIGHT_HOUR_END_TIME
        }
    }

    @Test
    // depending on time of day, it should display expected message
    fun testSalutations() {
        val expected = calculateExpectedMessage()

        // compare text view text with expected output
        onView(withId(R.id.salutationTextView)).check(matches(withText(expected)))
        Log.d(Constants.TAG, "Salutation: $expected")
    }

    @Test
    // tests opening the settings activity
    fun testClickingSettings() {
        onView(withId(R.id.settingsIcon)).perform(click())
        // test to see if settings activity appeared on screen
        intended(hasComponent(SettingsActivity::class.java.name))
    }

    @Test
    // tests opening the add cut FAB
    fun testClickingAddCut() {
        onView(withId(R.id.addCutFAB)).perform(click())
        // test to see if settings activity appeared on screen
        intended(hasComponent(AddCutActivity::class.java.name))
    }

    @Test
    // tests both fragments in the bottom nav
    fun testBottomNav() {
        onView(withId(R.id.cutLog)).perform(click()).check(matches(isDisplayed())) // open cut log fragment and test visibility
        // test going back
        pressBack()
        // check to see if home fragment is there
        onView(withId(R.id.homeConstraintLayout)).check(matches(isDisplayed()))

        // go back to cut log and move back to home once more
        onView(withId(R.id.cutLog)).perform(click())
        onView(withId(R.id.home)).perform(click()).check(matches(isDisplayed())) // open home fragment and test visibility
    }

    @Test
    fun testTurningOffCutSeason() {
        val appPrefs = ApplicationPrefs()
        appPrefs.setHasLocationSavedValue(true)// turns off the lawn location prompt
        onView(withId(R.id.home)).perform(click()).check(matches(isDisplayed())) // removes the permissions text
        mainTextViewContainsText("No cuts have been made yet. Add a new cut to get started!")

        // navigate to settings screen
        onView(withId(R.id.settingsIcon)).perform(click())

        // turns off the cutting season in the settings
        pressCuttingSeasonPreference()

        // navigate using back button
        pressBack()
        mainTextViewContainsText("cutting season") // ensure we are in cutting season

        // Then, navigate back to turn on the cutting season again

        // navigate to settings screen
        onView(withId(R.id.settingsIcon)).perform(click())

        // turns off the cutting season in the settings
        pressCuttingSeasonPreference()

        // navigate using back button
        pressBack()

        // Compare the text on main text view with "cutting season is turned off"
        mainTextViewContainsText("No cuts have been made yet. Add a new cut to get started!")
    }

    @Test
    fun testTurningOffCutSeasonWithoutLocationSaved() {
        removeExistingLocation()

        onView(withId(R.id.home)).perform(click()).check(matches(isDisplayed())) // removes the permissions text

        mainTextViewContainsText("There is no current lawn location saved. Add a location to receive notifications")

        onView(withId(R.id.createLawnLocationButton)).perform(click())

        ensureSaveLocationActivityIsShown()

        onView(withId(R.id.acceptSaveLocationButton)).perform(click())

        Thread.sleep(2500)

        mainTextViewContainsText("No cuts have been made yet. Add a new cut to get started!")

        // navigate to settings screen
        onView(withId(R.id.settingsIcon)).perform(click())

        pressCuttingSeasonPreference() // turns it off

        pressBack()

        // Compare the text on main text view with "cutting season is turned off"
        mainTextViewContainsText("cutting season")

        // navigate to settings screen
        onView(withId(R.id.settingsIcon)).perform(click())

        pressCuttingSeasonPreference() // turns season back on

        // navigate using back button
        pressBack()

        // Compare the text on main text view with "cutting season is turned off"
        mainTextViewContainsText("No cuts have been made yet. Add a new cut to get started!")

        addTestLocationBack()
    }

    private fun mainTextViewContainsText(textToTest: String) {
        onView(withId(R.id.mainMessageTextView))
            .check(matches(withText(containsString(textToTest))))
    }

    private fun pressCuttingSeasonPreference() {
        onView(withId(androidx.preference.R.id.recycler_view))
            .perform(
                actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(R.string.cuttingSeasonTitle)),
                    click()
                )
            )
    }

    @Test
    fun testLawnLocationButtonVisibility() {
        removeExistingLocation()

        onView(withId(R.id.home)).perform(click()) // press home to update text

        mainTextViewContainsText("There is no current lawn location saved. Add a location to receive notifications.")

        // test button text
        onView(withId(R.id.createLawnLocationButton)).check(matches(isCompletelyDisplayed()))

        // navigate to settings screen
        onView(withId(R.id.createLawnLocationButton)).perform(click())

        // verify correct activity
        intended(hasComponent(SaveLocationActivity::class.java.name))

        addTestLocationBack()
    }

    private fun removeExistingLocation() {
        runBlocking {
            lawnLocationRepository.deleteAllCuts()
        }
    }

    private fun addTestLocationBack() {
        runBlocking {
            lawnLocationRepository.addLocation(LawnLocation(42.2, 42.3))
        }
    }

//    @Test
//    fun testRefreshButton() {
//        ensureRefreshWorksWithHomeFrag()
//        ensureRefreshWorksWithLog()
//    }

    private fun ensureRefreshWorksWithLog() {
        onView(withId(R.id.cutLog)).perform(click()).check(matches(isDisplayed()))
        tapRefresh()
        onView(withId(R.id.cutLogConstraintLayout)).check(matches(isDisplayed()))
    }

    private fun ensureRefreshWorksWithHomeFrag() {
        onView(withId(R.id.homeConstraintLayout)).check(matches(isDisplayed()))
        tapRefresh()
        onView(withId(R.id.home)).perform(click()).check(matches(isDisplayed()))
    }

    @After
    fun release() {
        Intents.release()
    }
}

