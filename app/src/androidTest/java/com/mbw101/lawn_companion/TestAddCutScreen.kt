package com.mbw101.lawn_companion

import android.widget.DatePicker
import android.widget.TimePicker
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.mbw101.lawn_companion.ui.AddCutActivity
import com.mbw101.lawn_companion.ui.MainActivity
import com.mbw101.lawn_companion.ui.MainRecyclerAdaptor
import org.hamcrest.Matchers
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsInstanceOf.instanceOf
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

        // test adding a cut and seeing if it appears in the cut log fragment
        onView(withId(R.id.cutLog)).perform(click())
        // check the current month section for the new cut (ensure the size of list in month section is 1)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        onView(withId(R.id.main_recyclerview))
            .perform(actionOnItemAtPosition<MainRecyclerAdaptor.CustomViewHolder>(month, click()))

        // test to see if the delete cut dialog is in view
        onView(withText("Delete cut entry?")).check(matches(isDisplayed()))

        // now, perform a click on the delete button and make sure the recyclerview is there afterwards
        onView(withId(android.R.id.button1)).perform(click())
        onView(withId(R.id.main_recyclerview)).check(matches(isDisplayed()))
    }

    @Test
    fun testTimePicker() {
        onView(withId(R.id.selectedTimeTextView)).perform(click())
        setTime(13, 10)
        onView(withId(R.id.selectedTimeTextView)).check(matches(withText("1:10 PM")))

        onView(withId(R.id.selectedTimeTextView)).perform(click())
        setTime(5, 30)
        onView(withId(R.id.selectedTimeTextView)).check(matches(withText("5:30 AM")))
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