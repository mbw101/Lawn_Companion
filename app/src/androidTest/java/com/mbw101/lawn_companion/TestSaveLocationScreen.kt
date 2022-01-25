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
import java.util.*
import kotlin.concurrent.schedule

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-08-20
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class TestSaveLocationScreen {

    @get:Rule
    val saveActivityTestRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @get:Rule var coarsePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION)//,
//        android.Manifest.permission.ACCESS_COARSE_LOCATION)

    companion object {
        fun ensureActivityIsShown(className: String) {
            Intents.intended(IntentMatchers.hasComponent(className))
        }

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
        Espresso.onView(ViewMatchers.withId(R.id.settingsIcon)).perform(ViewActions.click())
        TestMainScreen.pressPreferenceWithTitle("Create a lawn location")
        ensureActivityIsShown(SaveLocationActivity::class.java.name)
        Espresso.onView(ViewMatchers.withId(R.id.denySaveLocationButton)).perform(ViewActions.click())
        // test to see if main activity appeared on screen
        ensureMainActivityIsShown()
    }

    @Test
    fun testAccept() {
        Espresso.onView(ViewMatchers.withId(R.id.settingsIcon)).perform(ViewActions.click())
        TestMainScreen.pressPreferenceWithTitle("Create a lawn location")
        ensureActivityIsShown(SaveLocationActivity::class.java.name)
        Espresso.onView(ViewMatchers.withId(R.id.acceptSaveLocationButton)).perform(ViewActions.click())
        // test to see if main activity appeared on screen
        Timer().schedule(1500) {
            ensureMainActivityIsShown()
        }
    }

    @After
    fun release() {
        Intents.release()
    }
}