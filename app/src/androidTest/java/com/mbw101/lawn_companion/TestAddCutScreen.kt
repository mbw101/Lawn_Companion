package com.mbw101.lawn_companion

import android.widget.TimePicker
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.mbw101.lawn_companion.ui.AddCutActivity
import com.mbw101.lawn_companion.ui.MainActivity
import org.hamcrest.Matchers
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

    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    @Before
    fun setup() {
        Intents.init()
    }

    @Test
    // tests day options for each month selected
    fun testDropdownMenus() {
        onView(withId(R.id.monthDropdownMenu)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("February"))).perform(click())
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
    // tests back buttons to see if main activity is shown
    fun testPhysicalBackButton() {
        // hit back button
        pressBack()
        // test to see if main activity appeared on screen
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }

    @Test
    // tests add cut button see if main activity is shown
    fun testAddAndDeleteCut() {
        // hit add cut button
        onView(withId(R.id.addCutButton)).perform(click())
        // test to see if main activity appeared on screen
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }

    @Test
    fun testTimePicker() {
        onView(withId(R.id.selectedTimeTextView)).perform(click())
        setTime(13, 10)
        onView(withId(R.id.selectedTimeTextView)).check(matches(withText(containsString("1:10")))) // PM or p.m. on some devices

        onView(withId(R.id.selectedTimeTextView)).perform(click())
        setTime(5, 30)
        onView(withId(R.id.selectedTimeTextView)).check(matches(withText(containsString("5:30")))) // AM or a.m. on some devices
    }

    @After
    fun release() {
        Intents.release()
    }

    /***
     * Assumes the timepicker dialog is shown before the method executes
     */
    private fun setTime(hour24Time: Int, minutes: Int) {
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name))).perform(
            PickerActions.setTime(
                hour24Time,
                minutes
            )
        )
        onView(withId(android.R.id.button1)).perform(click())
    }
}