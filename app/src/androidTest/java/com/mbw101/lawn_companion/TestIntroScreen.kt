package com.mbw101.lawn_companion

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.mbw101.lawn_companion.ui.IntroActivity
import org.hamcrest.CoreMatchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class TestIntroScreen {
    @get:Rule
    val introActivityRule: ActivityTestRule<IntroActivity> = ActivityTestRule(IntroActivity::class.java)

    @Test
    // using next button
    fun testSwitchingScreens() {
        // test visibility by making sure view is displayed
        onView(allOf(
            withId(R.id.introViewPager), isCompletelyDisplayed()))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun testSwippingThroughScreens() {
        for (i in 1..3) {
            ensureNextButtonIsShown()
            swipeLeft()
        }
        ensureGetStartedIsShown()
        for (i in 1..3) {
            swipeRight()
            ensureNextButtonIsShown()
        }
    }

    @Test
    fun testNextButton() {
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
}
