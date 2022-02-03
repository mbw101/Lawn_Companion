package com.mbw101.lawn_companion

import android.content.Context
import android.widget.TimePicker
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.mbw101.lawn_companion.database.AppDatabase
import com.mbw101.lawn_companion.database.AppDatabaseBuilder
import com.mbw101.lawn_companion.database.CutEntryDAO
import com.mbw101.lawn_companion.ui.AddCutActivity
import com.mbw101.lawn_companion.ui.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.endsWith
import org.hamcrest.Matchers
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.hamcrest.core.StringContains.containsString
import org.junit.After
import org.junit.Assert.*
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
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION)

    private lateinit var db: AppDatabase
    private lateinit var cutEntryDao: CutEntryDAO

    @Before
    fun setup() {
//        AccessibilityChecks.enable().setRunChecksFromRootView(true)
        Intents.init()
        setupDB()
    }

    private fun setupDB() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = AppDatabaseBuilder.getInstance(context)
        cutEntryDao = db.cutEntryDao()
    }

    @Test
    // tests day options for each month selected
    fun testDropdownMenus() {
        onView(withId(R.id.monthDropdownMenu)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("February"))).perform(click())
    }

    @Test
    // tests back buttons to see if main activity is shown
    // Causes StrictMode violation
    fun testBackButton() {
        // hit back icon
        onView(withId(R.id.backIcon)).perform(click())
        // test to see if main activity appeared on screen
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }

    @Test
    // tests back buttons to see if main activity is shown
    // Causes violation with StrictMode
    fun testPhysicalBackButton() {
        // hit back button
        pressBack()
        // test to see if main activity appeared on screen
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }

    @Test
    // tests add cut button see if main activity is shown
    // Causes StrictMode violation
    fun testAddAndDeleteCut() {
        // hit add cut button
        onView(withId(R.id.addCutButton)).perform(click())

        runBlocking {
            launch (Dispatchers.IO) {
                assertNull(cutEntryDao.getMostRecentCut()!!.note)
            }
        }

        // test to see if main activity appeared on screen
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }

    @Test
    fun testAddingNote() {
        val text = "One Love"
        // add note to cut entry via edit text
        onView(allOf(withClassName(endsWith("EditText"))))
            .perform(replaceText(text))

        onView(withId(R.id.addCutButton)).perform(click())
        runBlocking {
            launch (Dispatchers.IO) {
                assertNotNull(cutEntryDao.getMostRecentCut()!!.note)
                assertEquals(cutEntryDao.getMostRecentCut()!!.note, text)
            }
        }

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
//        AccessibilityChecks.disable()
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