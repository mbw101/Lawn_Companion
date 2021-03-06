package com.mbw101.lawn_companion

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mbw101.lawn_companion.database.CutEntry
import com.mbw101.lawn_companion.ui.AddCutActivity
import com.mbw101.lawn_companion.ui.CutLogFragment
import com.mbw101.lawn_companion.utils.Constants
import com.mbw101.lawn_companion.utils.UtilFunctions
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.*

/**
Lawn Companion
Created by Malcolm Wright
Date: June 17th, 2021
 */

@RunWith(AndroidJUnit4 ::class)
@Config(sdk = [28])
class CutEntryTests {

    // returns a list of mock Cut Entries for testing purposes
    private fun getEntries(): List<CutEntry> {
        // no entries in other months
        return listOf(
            // January
            CutEntry("4:36pm", 25, "January", 1, UtilFunctions.getCurrentYear()),
            CutEntry("4:36pm", 25, "January", 1, UtilFunctions.getCurrentYear()),
            CutEntry("4:36pm", 25, "January", 1, UtilFunctions.getCurrentYear()),

            // February
            CutEntry("4:36pm", 25, "February", 2, UtilFunctions.getCurrentYear()),
            CutEntry("4:36pm", 25, "February", 2, UtilFunctions.getCurrentYear()),

            // March
            CutEntry("4:36pm", 25, "March", 3, UtilFunctions.getCurrentYear()),

            // april
            CutEntry("4:36pm", 25, "april", 4, UtilFunctions.getCurrentYear()),
            CutEntry("4:36pm", 18, "april", 4, UtilFunctions.getCurrentYear()),
            CutEntry("4:36pm", 18, "april", 4, UtilFunctions.getCurrentYear()),
            CutEntry("4:36pm", 18, "april", 4, UtilFunctions.getCurrentYear()),

            //may
            CutEntry("4:36pm", 25, "may", 5, UtilFunctions.getCurrentYear()),
            CutEntry("4:36pm", 21, "may", 5, UtilFunctions.getCurrentYear())
        )
    }

    @Test
    // ensures the cut entries appear in the correct sections of each month
    fun testMapCreation() {
        val entries = getEntries()

        // get hashmap and test the values in it
        val hashMap = CutLogFragment.setupHashmap(entries)


        // test # of entries in jan, feb, mar, april, may
        assertEquals(hashMap[Constants.Month.JANUARY.monthNum]!!.size, 3)
        assertEquals(hashMap[Constants.Month.FEBRUARY.monthNum]!!.size, 2)
        assertEquals(hashMap[Constants.Month.MARCH.monthNum]!!.size, 1)
        assertEquals(hashMap[Constants.Month.APRIL.monthNum]!!.size, 4)
        assertEquals(hashMap[Constants.Month.MAY.monthNum]!!.size, 2)

        // test other months
        assertEquals(hashMap[Constants.Month.SEPTEMBER.monthNum]!!.size, 0)
        assertEquals(hashMap[Constants.Month.DECEMBER.monthNum]!!.size, 0)
    }

    @Test
    // use same hash map as above, and it will use that to create the
    // month section list
    fun testMonthSections() {
        val entries = getEntries()

        // use hashmap to get month sections
        val hashMap = CutLogFragment.setupHashmap(entries)
        val monthSections = CutLogFragment.setupMonthSections(hashMap, UtilFunctions.getCurrentYear())

        // test values inside the monthSection list
        assertEquals(monthSections.size, 12) // 12 months
        assertEquals(monthSections[0].items.size, 3)
        assertEquals(monthSections[1].items.size, 2)
        assertEquals(monthSections[2].items.size, 1)
        assertEquals(monthSections[3].items.size, 4)
        assertEquals(monthSections[4].items.size, 2)
        assertEquals(monthSections[5].items.size, 0)
        // rest of the months will have 0 entries
        for (i in 6..11) {
            assertEquals(monthSections[i].items.size, 0)
        }
    }

    @Test
    fun testDateValidity() {
        val newDate = Calendar.getInstance()
        assertEquals(AddCutActivity.checkDateValidity(newDate), true)
        newDate.add(Calendar.DAY_OF_MONTH, -1)
        assertEquals(AddCutActivity.checkDateValidity(newDate), true)
        newDate.add(Calendar.DAY_OF_MONTH, 2)
        assertEquals(AddCutActivity.checkDateValidity(newDate), false)
    }
}