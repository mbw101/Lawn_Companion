package com.mbw101.lawn_companion

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.mbw101.lawn_companion.ui.IntroActivity
import com.mbw101.lawn_companion.ui.MainActivity
import com.mbw101.lawn_companion.ui.SaveLocationActivity
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-08-22
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class TestFirstUse {
    @get:Rule
    val introActivityTestRule: ActivityTestRule<IntroActivity> = ActivityTestRule(IntroActivity::class.java)

    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION)

    @Before
    fun setup() {
        Intents.init()
    }

    @Test
    fun testHappyPathOfAppNavigation() {
        pressNavButtons()
        ensureSaveActivityIsShown()
        pressSaveLocation()
        ensureMainActivityIsShown()
        compareHappyExpectedOutputs()
    }

    @Test
    fun testDenyLocationPathOfAppNavigation() {
        pressNavButtons()
        ensureSaveActivityIsShown()
        pressDenySaveLocation()
        ensureMainActivityIsShown()
        compareNoLocationExpectedOutputs()
    }

    private fun pressNavButtons() {
        Espresso.onView(ViewMatchers.withId(R.id.nextButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.nextButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.nextButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.getStartedButton)).perform(ViewActions.click())
    }

    private fun pressSaveLocation() {
        Espresso.onView(ViewMatchers.withId(R.id.acceptSaveLocationButton)).perform(ViewActions.click())
    }

    private fun pressDenySaveLocation() {
        Espresso.onView(ViewMatchers.withId(R.id.denySaveLocationButton)).perform(ViewActions.click())
    }

    private fun ensureSaveActivityIsShown() {
        Intents.intended(IntentMatchers.hasComponent(SaveLocationActivity::class.java.name))
    }

    private fun ensureMainActivityIsShown() {
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }

    private fun compareHappyExpectedOutputs() {
        Espresso.onView(ViewMatchers.withId(R.id.mainMessageTextView)).check(
            matches(
                isCompletelyDisplayed()
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.mainMessageTextView)).check(matches(
            ViewMatchers.withText(CoreMatchers.containsString("No cuts have been made yet. Add a new cut to get started!"))
        ))
        Espresso.onView(ViewMatchers.withId(R.id.openPermissionsButton)).check(
            matches(not(isDisplayed()))
        )
        Espresso.onView(ViewMatchers.withId(R.id.createLawnLocationButton)).check(
            matches(not(isDisplayed()))
        )
    }

    private fun compareNoLocationExpectedOutputs() {
        Espresso.onView(ViewMatchers.withId(R.id.mainMessageTextView)).check(
            matches(
                isCompletelyDisplayed()
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.openPermissionsButton)).check(
            matches(not(isDisplayed()))
        )
        Espresso.onView(ViewMatchers.withId(R.id.mainMessageTextView)).check(matches(
            ViewMatchers.withText(CoreMatchers.containsString("There is no current lawn location saved. Add a location to receive notifications."))
        ))
        Espresso.onView(ViewMatchers.withId(R.id.createLawnLocationButton)).check(
            matches(isCompletelyDisplayed())
        )
    }

    @After
    fun release() {
        Intents.release()
    }
}