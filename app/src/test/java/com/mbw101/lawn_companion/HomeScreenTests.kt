package com.mbw101.lawn_companion

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mbw101.lawn_companion.database.CutEntry
import com.mbw101.lawn_companion.ui.HomeFragment
import com.mbw101.lawn_companion.utils.UtilFunctions
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.*

/**
Lawn Companion
Created by Malcolm Wright
Date: June 18th, 2021
 */

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class HomeScreenTests {

    // tests the string that is returned to see if it matches
    // with the expected output. The string is what will be shown
    // on the home screen based on last cut or if there even is a cut made
    @Test
    fun testExpectedMessage() {
        val currentDate = Calendar.getInstance()

        // test no cuts made
        val list: MutableList<CutEntry> = mutableListOf()
        var expectedString = "No cuts have been made yet. Add a new cut to get started!"
        assertEquals(HomeFragment.getDescriptionMessage(list.toList()), expectedString)

        // test 1 cut in the list (with cut on the same day)
        list.add(CutEntry(
            "4:36pm", currentDate.get(Calendar.DAY_OF_MONTH), "January",
            currentDate.get(Calendar.MONTH)+1, UtilFunctions.getCurrentYear()
        ))

        // take into account calendar to calculate the correct days since this cut
        expectedString = "No need to cut the lawn! An entry has been made today."
        assertEquals(HomeFragment.getDescriptionMessage(list), expectedString)

        // test 1 cut in the list (within the 7 day interval)
        val newDate = Calendar.getInstance()
        newDate.add(Calendar.DAY_OF_YEAR, -1) // set to 1 day ago

        list.clear()
        list.add(CutEntry(
            "4:36pm", newDate.get(Calendar.DAY_OF_MONTH), "January",
            newDate.get(Calendar.MONTH)+1, UtilFunctions.getCurrentYear()
        ))
        expectedString = "1 day since last cut"
        assertEquals(HomeFragment.getDescriptionMessage(list), expectedString)

        // test another day within the 7 days
        list.clear()
        newDate.add(Calendar.DAY_OF_YEAR, -2) // set to 3 days ago
        list.add(CutEntry(
            "4:36pm", newDate.get(Calendar.DAY_OF_MONTH), "January",
            newDate.get(Calendar.MONTH)+1, UtilFunctions.getCurrentYear()
        ))
        expectedString = "3 days since last cut"
        assertEquals(HomeFragment.getDescriptionMessage(list), expectedString)

        // test a date outside the 7 interval
        newDate.add(Calendar.DAY_OF_YEAR, -11) // set to 14 day ago (3 + 11)
        list.clear()
        list.add(CutEntry(
            "4:36pm", newDate.get(Calendar.DAY_OF_MONTH), "January",
            newDate.get(Calendar.MONTH)+1, UtilFunctions.getCurrentYear()
        ))
        expectedString = "Your last cut has surpassed the cutting interval. You will likely need a cut."
        assertEquals(HomeFragment.getDescriptionMessage(list), expectedString)

        // test a crazy date outside the 7 interval
        newDate.add(Calendar.DAY_OF_YEAR, -10000)
        list.clear()
        list.add(CutEntry(
            "4:36pm", newDate.get(Calendar.DAY_OF_MONTH), "January",
            newDate.get(Calendar.MONTH)+1, UtilFunctions.getCurrentYear()
        ))
        expectedString = "Your last cut has surpassed the cutting interval. You will likely need a cut."
        assertEquals(HomeFragment.getDescriptionMessage(list), expectedString)
    }
}