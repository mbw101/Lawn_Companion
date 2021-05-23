package com.mbw101.lawn_companion.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.runner.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class CutDatabaseTest {

    private lateinit var db: CutEntryDatabase
    private lateinit var cutEntryDao: CutEntryDAO

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CutEntryDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        cutEntryDao = db.cutEntryDao()
    }

    @Test
    @Throws(Exception::class)
    //  Tests the get all cuts by adding 3 CutEntries and ensuring a list with 3 entries are returned
    fun insertAndGetAllCuts() = runBlocking {
        cutEntryDao.insertAll(CutEntry("4:36pm", 17, "September", 9),
            CutEntry("4:36pm", 28, "September", 9),
            CutEntry("4:36pm", 5, "October", 10))

        val returnedList = cutEntryDao.getAllCuts()

        Assert.assertEquals(returnedList.size, 3)
    }

    @Test
    @Throws(Exception::class)
    //  -> Tests the get all cuts by adding 3 CutEntries and ensuring a list with 3 entries are returned
    fun insertAndFindByMonthName() = runBlocking {
        cutEntryDao.insertAll(CutEntry("4:36pm", 17, "September", 9),
            CutEntry("4:36pm", 28, "September", 9),
            CutEntry("4:36pm", 5, "October", 10))

        var returnedEntries = cutEntryDao.findByMonthName("October")
        Assert.assertEquals(returnedEntries.size, 1)

        returnedEntries = cutEntryDao.findByMonthName("September")
        Assert.assertEquals(returnedEntries.size, 2)
    }

    @After
    fun tearDown() {
        db.close()
    }
}