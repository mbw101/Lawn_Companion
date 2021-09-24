package com.mbw101.lawn_companion

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.rule.ActivityTestRule
import com.mbw101.lawn_companion.database.AppDatabase
import com.mbw101.lawn_companion.database.AppDatabaseBuilder
import com.mbw101.lawn_companion.database.CuttingSeasonDatesDao
import com.mbw101.lawn_companion.ui.SetDatesActivity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-09-24
 */
class TestSetDatesActivity {
    @get:Rule
    val setDatesActivityTestRule: ActivityTestRule<SetDatesActivity> = ActivityTestRule(
        SetDatesActivity::class.java)

    @Before
    fun setup() {
        Intents.init()
    }

    @Test
    fun testBackButton() {

    }

    @Test
    // the android back button
    fun testPhysicalBackButton() {

    }

    @Test
    fun testSaveButton() {

    }

    @Test
    fun testAlertDialogVisibility() {

    }

    @After
    fun release() {
        Intents.release()
    }
}