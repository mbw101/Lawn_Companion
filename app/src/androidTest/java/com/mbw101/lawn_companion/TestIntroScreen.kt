package com.mbw101.lawn_companion

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.mbw101.lawn_companion.ui.IntroActivity
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class TestIntroScreen {
    @get:Rule
    val introActivityRule: ActivityTestRule<IntroActivity> = ActivityTestRule(IntroActivity::class.java, true, false)

    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    private lateinit var customIntent: Intent

    @Before
    fun setup() {
        TestFirstUse.resetAppPreferences()
        Intents.init()
        customIntent = Intent()
        customIntent.putExtra("your_key", "your_value")
    }

    @Test
    // using next button
    fun testSwitchingScreens() {
        TestFirstUse.resetAppPreferences()
        introActivityRule.launchActivity(customIntent)
        // test visibility by making sure view is displayed
        onView(allOf(
            withId(R.id.introViewPager), isCompletelyDisplayed()))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun testSwipingThroughScreens() {
        introActivityRule.launchActivity(customIntent)
        for (i in 1..3) {
            ensureNextButtonIsShown()
            swipeLeft()
        }
        swipeLeft()
        ensureGetStartedIsShown()
        for (i in 1..3) {
            swipeRight()
            ensureNextButtonIsShown()
        }
    }

    @Test
    fun testNextButton() {
        introActivityRule.launchActivity(customIntent)
        for (i in 1..3) {
            ensureNextButtonIsShown()
            pressNextButton()
        }
        ensureGetStartedIsShown()
        for (i in 1..3) {
            pressBack()
            ensureNextButtonIsShown()
        }
    }

    private fun ensureGetStartedIsShown() {
        onView(withId(R.id.getStartedButton)).check(matches(isCompletelyDisplayed()))
    }

    private fun ensureNextButtonIsShown() {
        onView(withId(R.id.nextButton)).check(matches(isCompletelyDisplayed()))
    }

    private fun swipeRight() {
        onView(withId(R.id.introViewPager)).perform(ViewActions.swipeRight())
    }

    private fun swipeLeft() {
        onView(withId(R.id.introViewPager)).perform(ViewActions.swipeLeft())
    }

    private fun pressNextButton() {
        onView(withId(R.id.nextButton)).perform(ViewActions.click())
    }

    @After
    fun cleanup() {
        Intents.release()
    }
}
