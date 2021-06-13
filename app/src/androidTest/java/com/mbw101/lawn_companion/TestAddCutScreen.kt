package com.mbw101.lawn_companion

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withSpinnerText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.mbw101.lawn_companion.ui.AddCutActivity
import com.mbw101.lawn_companion.ui.MainActivity
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.hamcrest.core.StringContains.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-06-13
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class TestAddCutScreen {
    @get:Rule
    val addCutActivityTestRule: ActivityTestRule<AddCutActivity> = ActivityTestRule(AddCutActivity::class.java)

    @Before
    fun setup() {
        Intents.init()
    }

    @Test
    // tests day options for each month selected

    fun testDropdownMenus() {
        onView(withId(R.id.monthDropdownMenu)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("February"))).perform(click())

        // TODO: try values that we know shouldn't work and assert that they fail
        onView(withId(R.id.dayDropdownMenu)).check(matches(withSpinnerText(containsString("30"))))
    }

    @Test
    // tests back buttons to see if main activity is shown
    fun testBackButton() {
        // hit back icon
        onView(withId(R.id.backIcon)).perform(click())
        // test to see if main activity appeared on screen
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }

    @Test
    // tests add cut button see if main activity is shown
    fun testAddCut() {
        // hit add cut button
        onView(withId(R.id.addCutButton)).perform(click())
        // test to see if main activity appeared on screen
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }

    @After
    fun release() {
        Intents.release()
    }
}