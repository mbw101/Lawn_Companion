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

    private lateinit var db: AppDatabase
    private lateinit var cutEntryDao: CutEntryDAO

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        cutEntryDao = db.cutEntryDao()
    }

    @Test
    @Throws(Exception::class)
    fun testNumEntries() = runBlocking {
        cutEntryDao.insertAll(CutEntry("4:36pm", 17, "September", 9),
            CutEntry("4:36pm", 5, "October", 10),
            CutEntry("4:36pm", 28, "September", 9),
            CutEntry("4:36pm", 1, "October", 10))

        Assert.assertEquals(cutEntryDao.getNumEntries(), 4)
    }

    @Test
    @Throws(Exception::class)
    //  Tests the get all cuts by adding 3 CutEntries and ensuring a list with 3 entries are returned
    fun insertAndGetAllCuts() = runBlocking {
        cutEntryDao.insertAll(CutEntry("4:36pm", 17, "September", 9),
            CutEntry("4:36pm", 28, "September", 9),
            CutEntry("4:36pm", 5, "October", 10))

        val returnedList = cutEntryDao.getAllCuts()

        Assert.assertEquals(cutEntryDao.getNumEntries(), 3)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndFindByMonthName() = runBlocking {
        cutEntryDao.insertAll(CutEntry("4:36pm", 17, "September", 9),
            CutEntry("4:36pm", 28, "September", 9),
            CutEntry("4:36pm", 5, "October", 10))

        var returnedEntries: List<CutEntry>? = null
        runBlocking {
            returnedEntries = cutEntryDao.findByMonthName("October")
            Assert.assertEquals(returnedEntries!!.size, 1)
        }

        runBlocking {
            returnedEntries = cutEntryDao.findByMonthName("September")
            Assert.assertEquals(returnedEntries!!.size, 2)
        }
    }

    @Test
    @Throws(Exception::class)
    fun insertAndFindByMonthNum() {
        runBlocking {
            cutEntryDao.insertAll(CutEntry("4:36pm", 17, "September", 9),
                CutEntry("4:36pm", 28, "September", 9),
                CutEntry("4:36pm", 5, "October", 10))

            var returnedEntries = cutEntryDao.findByMonthNum(10)
            Assert.assertEquals(returnedEntries!!.size, 1)

            returnedEntries = cutEntryDao.findByMonthNum(9)
            Assert.assertEquals(returnedEntries!!.size, 2)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testFindLastCut() {
        runBlocking {
            cutEntryDao.insertAll(
                CutEntry("4:36pm", 17, "September", 9),
                CutEntry("4:36pm", 5, "October", 10),
                CutEntry("4:36pm", 28, "September", 9),
                CutEntry("4:36pm", 1, "October", 10)
            )

            val returnedEntry = cutEntryDao.getLastCut()
            Assert.assertEquals(returnedEntry!!.month_name, "October")
            Assert.assertEquals(returnedEntry!!.day_number, 5)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteCut() {
        runBlocking {
            cutEntryDao.insertAll(
                CutEntry("4:36pm", 17, "September", 9),
                CutEntry("4:36pm", 5, "October", 10),
                CutEntry("4:36pm", 28, "September", 9),
                CutEntry("4:36pm", 1, "October", 10)
            )

            // delete both the october cuts and check each time afterwards
            cutEntryDao.deleteCuts(CutEntry("4:36pm", 1, "October", 10))
            var returnedEntry = cutEntryDao.getLastCut()
            Assert.assertEquals(returnedEntry!!.month_name, "October")

            cutEntryDao.deleteCuts(CutEntry("4:36pm", 5, "October", 10))
            returnedEntry = cutEntryDao.getLastCut()
            Assert.assertEquals(returnedEntry!!.month_name, "September")

            // check size after deleting
            val remainingEntries = cutEntryDao.getAllCuts()
            Assert.assertEquals(cutEntryDao.getNumEntries(), 2)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteAllCuts() = runBlocking {
        cutEntryDao.insertAll(CutEntry("4:36pm", 17, "September", 9),
            CutEntry("4:36pm", 5, "October", 10),
            CutEntry("4:36pm", 28, "September", 9),
            CutEntry("4:36pm", 1, "October", 10))

        // delete both the october cuts and check each time afterwards
        cutEntryDao.deleteAll()

        // check size after deleting
        val remainingEntries = cutEntryDao.getAllCuts()
        Assert.assertEquals(cutEntryDao.getNumEntries(), 0)
    }

    @After
    fun tearDown() {
        db.close()
    }
}