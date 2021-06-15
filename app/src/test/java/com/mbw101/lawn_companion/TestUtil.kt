package com.mbw101.lawn_companion

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mbw101.lawn_companion.utils.UtilFunctions
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

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
}