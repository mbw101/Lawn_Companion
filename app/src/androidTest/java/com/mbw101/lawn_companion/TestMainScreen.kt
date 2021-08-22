package com.mbw101.lawn_companion

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
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
    private val goodNight = "Good Night!"
    private lateinit var lawnLocationRepository: LawnLocationRepository

    @get:Rule
    val mainActivityTestRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @get:Rule var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION,
    android.Manifest.permission.ACCESS_COARSE_LOCATION)

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
        // Get time and get coresponding message
        val expectedVal = when (cal.get(Calendar.HOUR_OF_DAY)) {
            5, 6, 7, 8, 9, 10, 11 -> { // good morning
                goodMorning
            }

            12, 13, 14, 15, 16, 17 -> { // good afternoon
                goodAfternoon
            }

            else -> { // good evening
                goodNight
            }
        }

        return expectedVal
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

    // test switching permissions off
    @Test
    fun testTurningOffCutSeason() {
        onView(withId(R.id.home)).perform(click()).check(matches(isDisplayed())) // open home fragment and test visibility

        onView(withId(R.id.mainMessageTextView)).check(matches(
            withText(containsString("No cuts have been made yet. Add a new cut to get started!"))))

        // navigate to settings screen
        onView(withId(R.id.settingsIcon)).perform(click())

        // turns off the cutting season in the settings
        onView(withId(androidx.preference.R.id.recycler_view))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText(R.string.cuttingSeasonTitle)),
                click()))

        // navigate using back button
        pressBack()

        // Compare the text on main text view with "cutting season is turned off"
        onView(withId(R.id.mainMessageTextView))
            .check(matches(withText(containsString("cutting season"))))

        // Then, navigate back to turn on the cutting season again

        // navigate to settings screen
        onView(withId(R.id.settingsIcon)).perform(click())

        // turns off the cutting season in the settings
        onView(withId(androidx.preference.R.id.recycler_view))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText(R.string.cuttingSeasonTitle)),
                    click()))

        // navigate using back button
        pressBack()

        // Compare the text on main text view with "cutting season is turned off"
        onView(withId(R.id.mainMessageTextView))
            .check(matches(withText(containsString("No cuts have been made yet. Add a new cut to get started!"))))
    }

    @Test
    fun testTurningOffCutSeasonWithLocationSaved() {
        removeExistingLocation()

        onView(withId(R.id.home)).perform(click()).check(matches(isDisplayed())) // open home fragment and test visibility

        onView(withId(R.id.mainMessageTextView)).check(matches(
            withText(containsString("There is no current lawn location saved. Add a location to receive notifications"))))

        onView(withId(R.id.createLawnLocationButton)).perform(click())

        // check save location is shown
        intended(hasComponent(SaveLocationActivity::class.java.name))

        onView(withId(R.id.acceptSaveLocationButton)).perform(click())

        onView(withId(R.id.mainMessageTextView)).check(matches(
            withText(containsString("No cuts have been made yet. Add a new cut to get started!"))))

        // navigate to settings screen
        onView(withId(R.id.settingsIcon)).perform(click())

        // turns off the cutting season in the settings
        onView(withId(androidx.preference.R.id.recycler_view))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText(R.string.cuttingSeasonTitle)),
                    click()))

        pressBack()

        // Compare the text on main text view with "cutting season is turned off"
        onView(withId(R.id.mainMessageTextView))
            .check(matches(withText(containsString("cutting season"))))

        // navigate to settings screen
        onView(withId(R.id.settingsIcon)).perform(click())

        // turns off the cutting season in the settings
        onView(withId(androidx.preference.R.id.recycler_view))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText(R.string.cuttingSeasonTitle)),
                    click()))

        // navigate using back button
        pressBack()

        // Compare the text on main text view with "cutting season is turned off"
        onView(withId(R.id.mainMessageTextView))
            .check(matches(withText(containsString("No cuts have been made yet. Add a new cut to get started!"))))

        addTestLocationBack()
    }

    @Test
    fun testLawnLocationButtonVisibility() {
        removeExistingLocation()

        onView(withId(R.id.home)).perform(click()) // press home to update text

        // test main text too
        onView(withId(R.id.mainMessageTextView)).check(matches(
            withText(containsString("There is no current lawn location saved. Add a location to receive notifications."))))

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

    @After
    fun release() {
        Intents.release()
    }
}