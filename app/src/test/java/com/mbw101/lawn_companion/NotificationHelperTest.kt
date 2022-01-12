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

        assert(date.isNotEmpty())
        println(date)
        val cal = Calendar.getInstance()
        if (cal.get(Calendar.MONTH) < Calendar.OCTOBER) {
            // include leading zero
            assertEquals(date, "${cal.get(Calendar.YEAR)}-0${cal.get(Calendar.MONTH)+1}-${cal.get(Calendar.DAY_OF_MONTH)}")
        }
        else {
            assertEquals(date, "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)+1}-${cal.get(Calendar.DAY_OF_MONTH)}")
        }
    }
}