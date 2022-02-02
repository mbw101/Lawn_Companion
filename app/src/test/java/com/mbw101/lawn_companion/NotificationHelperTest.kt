package com.mbw101.lawn_companion

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mbw101.lawn_companion.notifications.NotificationHelper
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.*

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class NotificationHelperTest {

    @Test
    fun testSkipDateHelper() {
        val date = NotificationHelper.createSkipDateString()
        println("Skip date format string = $date")

        assert(date.isNotEmpty())
        val cal = Calendar.getInstance()

        // add leading zero if needed for day and month
        val dayOfMonth: String
        val day = cal.get(Calendar.DAY_OF_MONTH)
        dayOfMonth = if (day < 10) {
            "0$day"
        } else {
            day.toString()
        }

        val monthOfYear: String
        val month = cal.get(Calendar.MONTH) + 1
        monthOfYear = if (day < 10) {
            "0$month"
        } else {
            month.toString()
        }

        assertEquals(date, "${cal.get(Calendar.YEAR)}-${monthOfYear}-${dayOfMonth}")
    }
}