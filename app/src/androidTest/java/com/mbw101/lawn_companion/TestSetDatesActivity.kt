package com.mbw101.lawn_companion

import android.content.Context
import android.content.Intent
import android.widget.DatePicker
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.mbw101.lawn_companion.database.AppDatabase
import com.mbw101.lawn_companion.database.AppDatabaseBuilder
import com.mbw101.lawn_companion.database.CuttingSeasonDatesDao
import com.mbw101.lawn_companion.ui.SetDatesActivity
import com.mbw101.lawn_companion.ui.SettingsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*


/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-09-24
 */
class TestSetDatesActivity {
    @get:Rule
    val setDatesActivityTestRule: ActivityTestRule<SetDatesActivity> = ActivityTestRule(
        SetDatesActivity::class.java, true, false) // makes it so the activity doesn't start immediately when a test case is ran , true, false
    private lateinit var db: AppDatabase
    private lateinit var cuttingSeasonDatesDao: CuttingSeasonDatesDao
    private lateinit var customIntent: Intent

    @Before
    fun setup() {
        Intents.init()
        setupDB()
        customIntent = Intent()
        customIntent.putExtra("your_key", "your_value")
    }

    private fun setupDB() {
        val context: Context = ApplicationProvider.getApplicationContext<Context>()
        db = AppDatabaseBuilder.getInstance(context)
        cuttingSeasonDatesDao = db.cuttingSeasonDatesDao()
    }

    @Test
    fun testBackButton() {
        setDatesActivityTestRule.launchActivity(customIntent)
        Thread.sleep(500)
        // hit back icon
        onView(withId(R.id.backIcon)).perform(click())
        // test to see if main activity appeared on screen
        Intents.intended(IntentMatchers.hasComponent(SettingsActivity::class.java.name))
    }

    @Test
    // the android back button
    fun testPhysicalBackButton() {
        setDatesActivityTestRule.launchActivity(customIntent)
        Thread.sleep(500)
        pressBack()
        Intents.intended(IntentMatchers.hasComponent(SettingsActivity::class.java.name))
    }

    @Test
    fun testPickingDates() {
        setDatesActivityTestRule.launchActivity(customIntent)
        Thread.sleep(500)
        pickDateTest(R.id.startDateSelector)
        pickDateTest(R.id.endDateSelector)
    }

    private fun pickDateTest(id: Int) {
        onView(withId(id)).check(matches(isDisplayed()))
        onView(withId(id)).perform(click())
        setDate(2021, 9, 25)
        onView(withId(id)).check(matches(withText("25/9/2021")))

        onView(withId(id)).check(matches(isDisplayed()))
        onView(withId(id)).perform(click())
        setDate(2021, 12, 5)
        onView(withId(id)).check(matches(withText("5/12/2021")))
    }

    @Test
    fun testSaveButton() {
        setDatesActivityTestRule.launchActivity(customIntent)
        Thread.sleep(500)
        onView(withId(R.id.startDateSelector)).check(matches(isDisplayed()))
        onView(withId(R.id.startDateSelector)).perform(click())
        setDate(2021, 9, 25)

        onView(withId(R.id.endDateSelector)).check(matches(isDisplayed()))
        onView(withId(R.id.endDateSelector)).perform(click())
        setDate(2021, 12, 5)

        onView(withId(R.id.saveDatesButton)).perform(click())
        Thread.sleep(1000)

        runBlocking {
            launch (Dispatchers.IO) {
                assertEquals(cuttingSeasonDatesDao.getNumEntries(), 2)
                val startDate = cuttingSeasonDatesDao.getStartDate()!!
                assertEquals(startDate.calendarValue.get(Calendar.YEAR), 2021)
                assertEquals(startDate.calendarValue.get(Calendar.MONTH), 9 - 1) // zero indexed
                assertEquals(startDate.calendarValue.get(Calendar.DAY_OF_MONTH), 25)

                val endDate = cuttingSeasonDatesDao.getEndDate()!!
                assertEquals(endDate.calendarValue.get(Calendar.YEAR), 2021)
                assertEquals(endDate.calendarValue.get(Calendar.MONTH), 12 - 1) // zero indexed
                assertEquals(endDate.calendarValue.get(Calendar.DAY_OF_MONTH), 5)
            }
        }

        Intents.intended(IntentMatchers.hasComponent(SettingsActivity::class.java.name))
    }

    @Test
    fun testStartEndDateTextViews() {
        runBlocking {
            launch (Dispatchers.IO) {
                val startDate = Calendar.getInstance()
                startDate.set(Calendar.MONTH, Calendar.JANUARY)
                startDate.set(Calendar.DAY_OF_MONTH, 1)

                val endDate = Calendar.getInstance()
                endDate.set(Calendar.MONTH, Calendar.DECEMBER)
                endDate.set(Calendar.DAY_OF_MONTH, 31)

                cuttingSeasonDatesDao.insertStartDate(startDate)
                cuttingSeasonDatesDao.insertEndDate(endDate)
            }
        }

        setDatesActivityTestRule.launchActivity(customIntent)

        // ensure calendars have expected values
        val startDate = setDatesActivityTestRule.activity.getStartDate()
        val endDate = setDatesActivityTestRule.activity.getEndDate()
        assertNotNull(startDate)
        assertNotNull(endDate)
        assertEquals(startDate!!.get(Calendar.MONTH), Calendar.JANUARY)
        assertEquals(startDate.get(Calendar.MONTH), Calendar.JANUARY)
        assertEquals(endDate!!.get(Calendar.MONTH), Calendar.DECEMBER)
        assertEquals(endDate.get(Calendar.MONTH), Calendar.DECEMBER)

        // Ensure the textview matches proper format
//        Thread.sleep(250)
        onView(withId(R.id.startDateSelector)).check(matches(withText("1/1/2021")))
        onView(withId(R.id.endDateSelector)).check(matches(withText("31/12/2021")))
    }

    @After
    fun release() {
        Intents.release()
    }

    /***
     * Assumes the right date text view is picked. So,
     * the start or end date edit text views will need to be
     * pressed before calling this method.
     */
    private fun setDate(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).perform(
            PickerActions.setDate(
                year,
                monthOfYear,
                dayOfMonth
            )
        )
        onView(withId(android.R.id.button1)).perform(click())
    }
}