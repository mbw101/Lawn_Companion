package com.mbw101.lawn_companion

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.mbw101.lawn_companion.database.AppDatabase
import com.mbw101.lawn_companion.database.AppDatabaseBuilder
import com.mbw101.lawn_companion.database.CutEntry
import com.mbw101.lawn_companion.database.CutEntryDAO
import com.mbw101.lawn_companion.ui.MainActivity
import com.mbw101.lawn_companion.utils.UtilFunctions
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TestCutLogScreen {
    private lateinit var db: AppDatabase
    private lateinit var cutEntryDao: CutEntryDAO

    @get:Rule
    val mainActivityTestRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION)

    companion object {
        private const val MONTH_TO_TEST = 9

        fun withViewAtPosition(position: Int, itemMatcher: Matcher<View?>): Matcher<View?>? {
            return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {

                override fun matchesSafely(recyclerView: RecyclerView): Boolean {
                    val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
                    return viewHolder != null && itemMatcher.matches(viewHolder.itemView)
                }

                override fun describeTo(description: org.hamcrest.Description?) {
                    itemMatcher.describeTo(description);
                }
            }
        }
    }

    private fun setupCutEntryDB() {
        val context: Context = ApplicationProvider.getApplicationContext<Context>()
        db = AppDatabaseBuilder.getInstance(context)
        cutEntryDao = db.cutEntryDao()
    }

    @Before
    fun setup() {
        Intents.init()
        setupCutEntryDB()
    }

    @Test
    fun testCutEntryInDifferentYear() {
        navigateToCutLog()
        ensureCutLogIsDisplayed()
        addEntriesInPreviousYear()
        // test to make sure no entries appear in recyclerview
        onView(withId(R.id.main_recyclerview))
            .check(matches(not(withViewAtPosition(MONTH_TO_TEST, hasDescendant(allOf(withText("Sept"), isDisplayed()))))))

        addTestEntryInSameYear()
        TestMainScreen.tapRefresh()
        navigateToCutLog()
//        onView(withId(R.id.main_recyclerview))
//            .check(matches(withViewAtPosition(MONTH_TO_TEST, hasDescendant(allOf(withText("Sept"), isDisplayed()))))) // Completed Cut
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
        onView(withId(R.id.cutLog)).perform(ViewActions.click())
    }

    private fun addEntriesInPreviousYear() = runBlocking {
        cutEntryDao.insertAll(
            CutEntry("4:36pm", 17, "September", 9, UtilFunctions.getCurrentYear() - 1),
            CutEntry("4:36pm", 5, "October", 10, UtilFunctions.getCurrentYear() - 1),
            CutEntry("4:36pm", 28, "September", 9, UtilFunctions.getCurrentYear() - 1),
            CutEntry("4:36pm", 1, "October", 10, UtilFunctions.getCurrentYear() - 1)
        )
    }
}