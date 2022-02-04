package com.mbw101.lawn_companion

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.mbw101.lawn_companion.database.AppDatabase
import com.mbw101.lawn_companion.database.AppDatabaseBuilder
import com.mbw101.lawn_companion.database.CutEntry
import com.mbw101.lawn_companion.database.CutEntryDAO
import com.mbw101.lawn_companion.ui.MainActivity
import com.mbw101.lawn_companion.utils.Constants
import com.mbw101.lawn_companion.utils.UtilFunctions
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.hamcrest.core.AllOf
import org.hamcrest.core.Is
import org.hamcrest.core.IsInstanceOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class TestCutLogScreen {
    private lateinit var db: AppDatabase
    private lateinit var cutEntryDao: CutEntryDAO

    @get:Rule
    val mainActivityTestRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java, true, false)

    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION)

    private lateinit var customIntent: Intent

    companion object {
        private const val MONTH_TO_TEST = 8

        fun withViewAtPosition(position: Int, itemMatcher: Matcher<View?>): Matcher<View?> {
            return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
                override fun matchesSafely(recyclerView: RecyclerView): Boolean {
                    val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
                    return viewHolder != null && itemMatcher.matches(viewHolder.itemView)
                }

                override fun describeTo(description: org.hamcrest.Description?) {
                    itemMatcher.describeTo(description)
                }
            }
        }
    }

    private fun setupCutEntryDB() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = AppDatabaseBuilder.getInstance(context)
        cutEntryDao = db.cutEntryDao()
    }

    @Before
    fun setup() {
//        AccessibilityChecks.enable().setRunChecksFromRootView(true)
        Intents.init()
        setupCutEntryDB()
        customIntent = Intent()
        customIntent.putExtra("your_key", "your_value")
    }

    @Test
    fun testCutEntryInDifferentYearWithDropdown() {
        // test current year
        addEntriesInPreviousYear()
        addTestEntryInSameYear()
        mainActivityTestRule.launchActivity(customIntent)
        navigateToCutLog()
        ensureCutLogIsDisplayed()
        // test to make sure correct entries are there for current year and previous year cut entries are not there
        onView(withId(R.id.cutlog_recyclerview))
            .check(matches(not(withViewAtPosition(MONTH_TO_TEST + 1, hasDescendant(allOf(withText("Completed Cut"), isDisplayed()))))))
        onView(withId(R.id.cutlog_recyclerview))
            .check(matches(withViewAtPosition(MONTH_TO_TEST, hasDescendant(allOf(withText("Completed Cut"), isDisplayed())))))

        // test previous year
        onView(withId(R.id.spinner)).perform(click())
        val previousYear = UtilFunctions.getCurrentYear() - 1
        Espresso.onData(
            AllOf.allOf(
                Is.`is`(IsInstanceOf.instanceOf(String::class.java)),
                Is.`is`(previousYear.toString())
            )
        ).perform(click())

        Thread.sleep(500)
        // test to make sure correct entries are there for current year and previous year cut entries are not there
        onView(withId(R.id.cutlog_recyclerview))
            .check(matches(withViewAtPosition(MONTH_TO_TEST + 1, hasDescendant(allOf(withText("Completed Cut"), isDisplayed())))))
        onView(withId(R.id.cutlog_recyclerview))
            .check(matches(not(withViewAtPosition(MONTH_TO_TEST, hasDescendant(allOf(withText("Completed Cut"), isDisplayed()))))))
    }

    private fun addTestEntryInSameYear() = runBlocking {
        cutEntryDao.insertAll(
            CutEntry("4:36pm", 17, "September", 9, UtilFunctions.getCurrentYear())
        )
    }

    private fun ensureCutLogIsDisplayed() {
        onView(withId(R.id.cutLogConstraintLayout)).check(matches(isDisplayed()))
    }

    private fun navigateToCutLog() {
        onView(withId(R.id.cutlog)).perform(click())
    }

    private fun addEntriesInPreviousYear() = runBlocking {
        cutEntryDao.insertAll(
//            CutEntry("4:36pm", 17, "September", 9, UtilFunctions.getCurrentYear() - 1),
            CutEntry("4:36pm", 5, "October", 10, UtilFunctions.getCurrentYear() - 1),
//            CutEntry("4:36pm", 28, "September", 9, UtilFunctions.getCurrentYear() - 1),
            CutEntry("4:36pm", 1, "October", 10, UtilFunctions.getCurrentYear() - 1)
        )
    }

    @Test
    fun testDeletingCutEntry() {
        mainActivityTestRule.launchActivity(customIntent)
        addTestEntryInSameYear()
        navigateToCutLog()
        ensureCutLogIsDisplayed()
        // make sure entry is there first
        onView(withId(R.id.cutlog_recyclerview))
            .check(matches(withViewAtPosition(MONTH_TO_TEST, hasDescendant(allOf(withText("Completed Cut"), isDisplayed())))))

        // delete cut entry
        onView(withId(R.id.cutlog_recyclerview)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(8, click()))
        onView(withId(Constants.AlertDialogButton.POSITIVE.resId)).perform(click()) // for cut entry details
        onView(withId(Constants.AlertDialogButton.POSITIVE.resId)).perform(click()) // for confirmation alert dialog

        // that entry should be gone now
        onView(withId(R.id.cutlog_recyclerview))
            .check(matches(not(withViewAtPosition(MONTH_TO_TEST, hasDescendant(allOf(withText("Completed Cut"), isDisplayed()))))))
    }

    @Test
    fun testShowEditScreen() {
        mainActivityTestRule.launchActivity(customIntent)
        addTestEntryInSameYear()
        navigateToCutLog()
        ensureCutLogIsDisplayed()
        // make sure entry is there first
        onView(withId(R.id.cutlog_recyclerview))
            .check(matches(withViewAtPosition(MONTH_TO_TEST, hasDescendant(allOf(withText("Completed Cut"), isDisplayed())))))

        // delete cut entry
        onView(withId(R.id.cutlog_recyclerview)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(8, click()))
        onView(withId(Constants.AlertDialogButton.NEGATIVE.resId)).perform(click()) // for cut entry details
        // Ensure edit screen shows up
        onView(withId(R.id.editCutLayout)).check(matches(isDisplayed()))
    }

    @After
    fun cleanup() {
//        AccessibilityChecks.disable()
        Intents.release()
    }
}