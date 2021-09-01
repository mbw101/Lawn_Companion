package com.mbw101.lawn_companion

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.mbw101.lawn_companion.ui.MainActivity
import com.mbw101.lawn_companion.ui.SaveLocationActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-08-20
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class TestSaveLocationScreen {

    @get:Rule
    val mainActivityTestRule: ActivityTestRule<SaveLocationActivity> = ActivityTestRule(SaveLocationActivity::class.java)

    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION)

    companion object {
        fun ensureMainActivityIsShown() {
            Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
        }
    }

    @Before
    fun setup() {
        Intents.init()
    }

    @Test
    fun testDeny() {
        Espresso.onView(ViewMatchers.withId(R.id.denySaveLocationButton)).perform(ViewActions.click())
        // test to see if main activity appeared on screen
        ensureMainActivityIsShown()
    }

    @Test
    fun testAccept() {
        Espresso.onView(ViewMatchers.withId(R.id.acceptSaveLocationButton)).perform(ViewActions.click())
        // test to see if main activity appeared on screen
        ensureMainActivityIsShown()
    }

    @After
    fun release() {
        Intents.release()
    }
}