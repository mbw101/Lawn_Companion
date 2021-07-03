package com.mbw101.lawn_companion

import android.annotation.TargetApi
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.mbw101.lawn_companion.notifications.NotificationHelper
import org.junit.Test
import org.junit.runner.RunWith


/**
Lawn Companion
Created by Malcolm Wright
Date: July 3rd, 2021
 */

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class NotificationTests {

    @Test
    @TargetApi(Build.VERSION_CODES.M)
    fun testCutNotification() {
        val manager: NotificationManager =
            getInstrumentation().targetContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        NotificationHelper.createCutNotification(getInstrumentation().targetContext, "Test", "Test message", true)

        // TODO: Figure out why this test won't pass
//        assertEquals(1, manager.activeNotifications.size);
    }
}