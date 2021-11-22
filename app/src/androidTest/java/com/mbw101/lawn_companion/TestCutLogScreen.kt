package com.mbw101.lawn_companion

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.mbw101.lawn_companion.database.AppDatabase
import com.mbw101.lawn_companion.database.AppDatabaseBuilder
import com.mbw101.lawn_companion.database.CutEntryDAO
import com.mbw101.lawn_companion.ui.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class TestCutLogScreen {
    private lateinit var db: AppDatabase
    private lateinit var cutEntryDao: CutEntryDAO

    @get:Rule
    val mainActivityTestRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION)

    private fun setupCutEntryDB() {
        val context: Context = ApplicationProvider.getApplicationContext<Context>()
        db = AppDatabaseBuilder.getInstance(context)
        cutEntryDao = db.cutEntryDao()
    }

    @Before
    fun setup() {
        Intents.init()
        setupCutEntryDB()
    }

    @Test
    fun testDeletingCutEntry() {

    }

    @After
    fun release() {
        Intents.release()
    }
}