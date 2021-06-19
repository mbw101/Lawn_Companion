package com.mbw101.lawn_companion

import androidx.test.runner.AndroidJUnit4
import com.mbw101.lawn_companion.database.CutEntry
import com.mbw101.lawn_companion.ui.HomeFragment
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

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
        // test no cuts made
        var list: MutableList<CutEntry> = mutableListOf<CutEntry>()
        var expectedString = "No cuts have been made yet. Add a new cut to get started!"

        var returnedString = HomeFragment.getDescriptionMessage(list.toList())
        assertEquals(returnedString, expectedString)

        // test 1 cut in the list
        list.add(CutEntry("4:36pm", 25, "January", 1))

        // take into account calendar to calculate the correct days since this cut
        expectedString = "168 days since last cut"
    }
}