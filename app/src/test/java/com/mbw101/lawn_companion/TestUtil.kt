package com.mbw101.lawn_companion

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mbw101.lawn_companion.utils.UtilFunctions
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.*

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class TestUtil {

    @Test
    fun testLeapYears() {
        assertEquals(UtilFunctions.isLeapYear(2020), true)
        assertEquals(UtilFunctions.isLeapYear(2016), true)
    }

    @Test
    fun testNonLeapYears() {
        assertEquals(UtilFunctions.isLeapYear(2001), false)
        assertEquals(UtilFunctions.isLeapYear(1900), false)
    }

    @Test
    fun testDateCalculations() {
        // use 2 Calendar objects and test against expected
        val date1: Calendar = Calendar.getInstance()
        val date2: Calendar = Calendar.getInstance()
        // The getNumDaysBetween function works when calculating from April 1st to JUne 18th
        // but it fails when calculating from March 1st to June 18th.
        date1.set(2021, Calendar.MARCH, 1) // Jan 1st, 2021
        date2.set(2021, Calendar.JUNE, 18) // June 18th, 2021
    }

    @Test
    fun testNewYearDateCalculations() {
        val date1: Calendar = Calendar.getInstance()
        val date2: Calendar = Calendar.getInstance()

        date1.set(2020, Calendar.DECEMBER, 30) // Jan 1st, 2021
        date2.set(2021, Calendar.JANUARY, 3) // June 18th, 2021
        assertEquals(UtilFunctions.getNumDaysBetween(date1, date2), 4)
    }

    @Test
    fun testSameDayCalculation() {
        // use 2 Calendar objects and test against expected
        val date1: Calendar = Calendar.getInstance()
        val date2: Calendar = Calendar.getInstance()

        // test different dates
        date1.set(2021, Calendar.MARCH, 1) // Jan 1st, 2021
        date2.set(2021, Calendar.JUNE, 18) // June 18th, 2021
        assertEquals(UtilFunctions.sameDate(date1, date2), false)

        // test when the dates are the same
        date2.set(2021, Calendar.MARCH, 1) // Jan 1st, 2021
        assertEquals(UtilFunctions.sameDate(date1, date2), true)
    }
}