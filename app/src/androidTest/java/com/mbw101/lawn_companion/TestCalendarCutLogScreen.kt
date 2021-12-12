package com.mbw101.lawn_companion

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.mbw101.lawn_companion.database.AppDatabase
import com.mbw101.lawn_companion.database.AppDatabaseBuilder
import com.mbw101.lawn_companion.database.CutEntry
import com.mbw101.lawn_companion.database.CutEntryDAO
import com.mbw101.lawn_companion.ui.MainActivity
import com.mbw101.lawn_companion.utils.UtilFunctions
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.core.IsInstanceOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class TestCalendarCutLogScreen {
    private lateinit var db: AppDatabase
    private lateinit var cutEntryDao: CutEntryDAO

    @get:Rule
    val mainActivityTestRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    private fun ensureCutLogIsDisplayed() {
        onView(withId(R.id.newCutLogConstraintLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    private fun navigateToCutLog() {
        onView(withId(R.id.cutLog)).perform(click())
    }

    private fun addTestEntriesForDropdown() = runBlocking {
        cutEntryDao.insertAll(
            CutEntry("4:36pm", 17, "September", 9, UtilFunctions.getCurrentYear() - 1),
            CutEntry("4:36pm", 5, "October", 10, UtilFunctions.getCurrentYear() - 1),
            CutEntry("4:36pm", 28, "September", 9, UtilFunctions.getCurrentYear()),
            CutEntry("4:36pm", 1, "October", 10, UtilFunctions.getCurrentYear()),
            CutEntry("4:36pm", 1, "October", 10, 2016)
        )
    }

    private fun addTestEntryInSameYear() = runBlocking {
        cutEntryDao.insertAll(
            CutEntry("4:36pm", 17, "September", 9, UtilFunctions.getCurrentYear())
        )
    }

    @Before
    fun setup() {
        Intents.init()
        setupCutEntryDB()
    }

    private fun setupCutEntryDB() {
        val context: Context = ApplicationProvider.getApplicationContext<Context>()
        db = AppDatabaseBuilder.getInstance(context)
        cutEntryDao = db.cutEntryDao()
    }

    @Test
    fun testCutEntryInDifferentYear() {
        addTestEntriesForDropdown()
        navigateToCutLog()
        ensureCutLogIsDisplayed()

        pressSpecificYearDropdownOption("2021")
        pressSpecificYearDropdownOption("2020")
        pressSpecificYearDropdownOption("2016")
    }

    private fun pressSpecificYearDropdownOption(year: String) {
        onView(withId(R.id.yearDropdownMenu)).perform(click())
        onData(
            allOf(
                `is`(IsInstanceOf.instanceOf(String::class.java)),
                `is`(year)
            )
        ).perform(click())
    }

    @Test
    fun testDeletingCutEntry() {
        // TODO: When custom calendar view is created and added onto the screen
    }

    @After
    fun release() {
        Intents.release()
    }

}