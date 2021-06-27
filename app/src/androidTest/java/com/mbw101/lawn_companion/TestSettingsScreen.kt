package com.mbw101.lawn_companion

import androidx.test.espresso.Espresso
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.mbw101.lawn_companion.ui.MainActivity
import com.mbw101.lawn_companion.ui.SettingsActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-06-26
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class TestSettingsScreen {
    @get:Rule
    val settingsActivityTestRule: ActivityTestRule<SettingsActivity> = ActivityTestRule(SettingsActivity::class.java)

    @Before
    fun setup() {
        Intents.init()
    }

//    @Test
//    // tests back buttons to see if main activity is shown
//    fun testBackButton() {
//        // hit back icon
//        Espresso.onView(ViewMatchers.withId(R.id.backIcon)).perform(ViewActions.click())
//        // test to see if main activity appeared on screen
//        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
//    }

    @Test
    // tests back buttons to see if main activity is shown
    fun testPhysicalBackButton() {
        // hit back button
        Espresso.pressBack()
        // test to see if main activity appeared on screen
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }


    @After
    fun release() {
        Intents.release()
    }
}