package com.mbw101.lawn_companion

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.LargeTest
import com.mbw101.lawn_companion.database.CutEntry
import com.mbw101.lawn_companion.database.CutEntryDAO
import com.mbw101.lawn_companion.database.CutEntryDatabase
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-22
 */

@RunWith(RobolectricTestRunner::class)
@LargeTest
class CutEntryDatabaseTest: TestCase() {

    private lateinit var db: CutEntryDatabase
    private lateinit var cutEntryDao: CutEntryDAO

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CutEntryDatabase::class.java).build()
        cutEntryDao = db.cutEntryDao()
    }

    @Test
    @Throws(Exception::class)
    fun test() {
        assert(1+1 == 2)
    }

    @Test
    @Throws(Exception::class)
    //  -> Tests the get all cuts by adding 3 CutEntries and ensuring a list with 3 entries are returned
    fun insertAndGetAllCuts() = runBlocking {
        cutEntryDao.insertAll(CutEntry("4:36pm", 17, "September", 9),
            CutEntry("4:36pm", 28, "September", 9),
            CutEntry("4:36pm", 5, "October", 10))

        val returnedList = cutEntryDao.getAllCuts()

        assert(returnedList.size == 3)
    }

    @After
    @Throws(IOException::class)
    // -> Call deleteAll() function from DAO and then close the database
    fun closeDb() {

        db.close()
    }
}