package com.mbw101.lawn_companion

import android.content.Context
import android.widget.DatePicker
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.mbw101.lawn_companion.database.AppDatabase
import com.mbw101.lawn_companion.database.AppDatabaseBuilder
import com.mbw101.lawn_companion.database.CutEntryDAO
import com.mbw101.lawn_companion.database.CuttingSeasonDatesDao
import com.mbw101.lawn_companion.ui.MyApplication
import com.mbw101.lawn_companion.ui.SetDatesActivity
import com.mbw101.lawn_companion.ui.SettingsActivity
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers
import org.junit.After
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
        SetDatesActivity::class.java)
    private lateinit var db: AppDatabase
    private lateinit var cuttingSeasonDatesDao: CuttingSeasonDatesDao

    @Before
    fun setup() {
        Intents.init()
        setupDB()
    }

    private fun setupDB() {
        val context: Context = ApplicationProvider.getApplicationContext<Context>()
        db = AppDatabaseBuilder.getInstance(context)
        cuttingSeasonDatesDao = db.cuttingSeasonDatesDao()
    }

    @Test
    fun testBackButton() {
        // hit back icon
        onView(withId(R.id.backIcon)).perform(click())
        // test to see if main activity appeared on screen
        Intents.intended(IntentMatchers.hasComponent(SettingsActivity::class.java.name))
    }

    @Test
    // the android back button
    fun testPhysicalBackButton() {
        pressBack()
        Intents.intended(IntentMatchers.hasComponent(SettingsActivity::class.java.name))
    }

    @Test
    fun testPickingDates() {
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