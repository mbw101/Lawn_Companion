package com.mbw101.lawn_companion.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mbw101.lawn_companion.utils.UtilFunctions
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4 ::class)
@Config(sdk = [28])
class CutDatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var cutEntryDao: CutEntryDAO

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        cutEntryDao = db.cutEntryDao()
    }

    @Test
    @Throws(Exception::class)
    fun testNumEntries() = runBlocking {
        cutEntryDao.insertAll(CutEntry("4:36pm", 17, "September", 9, UtilFunctions.getCurrentYear()),
            CutEntry("4:36pm", 5, "October", 10, UtilFunctions.getCurrentYear()),
            CutEntry("4:36pm", 28, "September", 9, UtilFunctions.getCurrentYear()),
            CutEntry("4:36pm", 1, "October", 10, UtilFunctions.getCurrentYear()))

        assertEquals(cutEntryDao.getNumEntries(), 4)
    }

    @Test
    @Throws(Exception::class)
    //  Tests the get all cuts by adding 3 CutEntries and ensuring a list with 3 entries are returned
    fun insertAndGetAllCuts() = runBlocking {
        cutEntryDao.insertAll(CutEntry("4:36pm", 17, "September", 9, UtilFunctions.getCurrentYear()),
            CutEntry("4:36pm", 28, "September", 9, UtilFunctions.getCurrentYear()),
            CutEntry("4:36pm", 5, "October", 10, UtilFunctions.getCurrentYear()))

        assertEquals(cutEntryDao.getNumEntries(), 3)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndFindByMonthName() = runBlocking {
        cutEntryDao.insertAll(CutEntry("4:36pm", 17, "September", 9, UtilFunctions.getCurrentYear()),
            CutEntry("4:36pm", 28, "September", 9, UtilFunctions.getCurrentYear()),
            CutEntry("4:36pm", 5, "October", 10, UtilFunctions.getCurrentYear()))

        var returnedEntries: List<CutEntry>?
        runBlocking {
            returnedEntries = cutEntryDao.findByMonthName("October")
            assertEquals(returnedEntries!!.size, 1)
        }

        runBlocking {
            returnedEntries = cutEntryDao.findByMonthName("September")
            assertEquals(returnedEntries!!.size, 2)
        }
    }

    @Test
    @Throws(Exception::class)
    fun insertAndFindByMonthNum() {
        runBlocking {
            cutEntryDao.insertAll(CutEntry("4:36pm", 17, "September", 9, UtilFunctions.getCurrentYear()),
                CutEntry("4:36pm", 28, "September", 9, UtilFunctions.getCurrentYear()),
                CutEntry("4:36pm", 5, "October", 10, UtilFunctions.getCurrentYear()))

            var returnedEntries = cutEntryDao.findByMonthNum(10)
            assertEquals(returnedEntries!!.size, 1)

            returnedEntries = cutEntryDao.findByMonthNum(9)
            assertEquals(returnedEntries!!.size, 2)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testFindLastCut() {
        runBlocking {
            cutEntryDao.insertAll(
                CutEntry("4:36pm", 17, "September", 9, UtilFunctions.getCurrentYear() - 1),
                CutEntry("4:36pm", 5, "October", 10, UtilFunctions.getCurrentYear() - 1),
                CutEntry("4:36pm", 28, "September", 9, UtilFunctions.getCurrentYear() - 1),
                CutEntry("4:36pm", 1, "October", 10, UtilFunctions.getCurrentYear() - 1)
            )

            var returnedEntry = cutEntryDao.getMostRecentCut()
            assertEquals(returnedEntry!!.month_name, "October")
            assertEquals(returnedEntry.day_number, 5)

            // test case where there are entries in the previous year and one in the current year
            cutEntryDao.insertAll(
                CutEntry("8:00am", 11, "January", 1, UtilFunctions.getCurrentYear()),
            )

            returnedEntry = cutEntryDao.getMostRecentCut()
            assertEquals(returnedEntry!!.month_name, "January")
            assertEquals(returnedEntry.day_number, 11)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testFindAllCutsWithYear() {
        runBlocking {
            cutEntryDao.insertAll(
                CutEntry("4:36pm", 17, "September", 9, UtilFunctions.getCurrentYear()),
                CutEntry("4:36pm", 5, "October", 10, UtilFunctions.getCurrentYear()),
                CutEntry("4:36pm", 28, "September", 9, UtilFunctions.getCurrentYear()),
                CutEntry("4:36pm", 1, "October", 10, UtilFunctions.getCurrentYear())
            )

            var entriesFromCurrentYear = cutEntryDao.getEntriesFromSpecificYear(UtilFunctions.getCurrentYear())
            assertEquals(entriesFromCurrentYear.size, 4)

            cutEntryDao.insertAll(
                CutEntry("4:36pm", 28, "September", 9, 2020),
                CutEntry("4:36pm", 1, "October", 10, 2020)
            )

            entriesFromCurrentYear = cutEntryDao.getEntriesFromSpecificYear(2020)
            assertEquals(entriesFromCurrentYear.size, 2)
            entriesFromCurrentYear = cutEntryDao.getEntriesFromSpecificYear(2019)
            assertEquals(entriesFromCurrentYear.isEmpty(), true)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testFindLastEntryFromPreviousYear() {
        runBlocking {
            cutEntryDao.insertAll(
                CutEntry("4:36pm", 5, "October", 10, 2020),
                CutEntry("4:36pm", 17, "September", 9, 2020),
                CutEntry("4:36pm", 28, "September", 9, UtilFunctions.getCurrentYear()),
                CutEntry("4:36pm", 1, "October", 10, UtilFunctions.getCurrentYear())
            )

            var lastEntryFrom2020 = cutEntryDao.getLastEntryFromSpecificYear(2020)
            assertEquals(lastEntryFrom2020!!.day_number, 5) // oct 5th, 2020 is the last entry of 2020
            assertEquals(lastEntryFrom2020.month_number, 10)

            cutEntryDao.insertAll(
                CutEntry("4:36pm", 5, "December", 12, 2020)
            )

            lastEntryFrom2020 = cutEntryDao.getLastEntryFromSpecificYear(2020)
            assertEquals(lastEntryFrom2020!!.day_number, 5)
            assertEquals(lastEntryFrom2020.month_number, 12)
            assertEquals(cutEntryDao.getLastEntryFromSpecificYear(2019), null)
        }
    }

    // sorting queries
    @Test
    @Throws(Exception::class)
    fun testFindAllCutsWithYearSorted() {
        runBlocking {
            cutEntryDao.insertAll(
                CutEntry("4:36pm", 17, "September", 9, UtilFunctions.getCurrentYear()),
                CutEntry("4:36pm", 5, "October", 10, UtilFunctions.getCurrentYear()),
                CutEntry("4:36pm", 28, "September", 9, UtilFunctions.getCurrentYear()),
                CutEntry("4:36pm", 1, "October", 10, UtilFunctions.getCurrentYear())
            )

            var entriesFromCurrentYear = cutEntryDao.getEntriesFromSpecificYearSortedAsync(UtilFunctions.getCurrentYear())
            assertEquals(entriesFromCurrentYear.size, 4)
            assertEquals(entriesFromCurrentYear[0].day_number, 17)
            assertEquals(entriesFromCurrentYear[1].day_number, 28)
            assertEquals(entriesFromCurrentYear[2].day_number, 1)
            assertEquals(entriesFromCurrentYear[3].day_number, 5)

            cutEntryDao.insertAll(
                CutEntry("4:36pm", 1, "September", 10, 2020)
            )

            entriesFromCurrentYear = cutEntryDao.getEntriesFromSpecificYearSortedAsync(2020)
            assertEquals(entriesFromCurrentYear[0].day_number, 1)
            entriesFromCurrentYear = cutEntryDao.getEntriesFromSpecificYearSortedAsync(2019)
            assertEquals(entriesFromCurrentYear.isEmpty(), true)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testFindAllCutsSorted() {
        runBlocking {
            cutEntryDao.insertAll(
                CutEntry("4:36pm", 17, "September", 9, UtilFunctions.getCurrentYear()),
                CutEntry("4:36pm", 5, "October", 10, UtilFunctions.getCurrentYear()),
                CutEntry("4:36pm", 28, "September", 9, UtilFunctions.getCurrentYear()),
                CutEntry("4:36pm", 1, "October", 10, UtilFunctions.getCurrentYear())
            )

            var entriesFromCurrentYear = cutEntryDao.getAllCutsSortedAsync()
            assertEquals(entriesFromCurrentYear.size, 4)
            assertEquals(entriesFromCurrentYear[0].day_number, 17)
            assertEquals(entriesFromCurrentYear[1].day_number, 28)
            assertEquals(entriesFromCurrentYear[2].day_number, 1)
            assertEquals(entriesFromCurrentYear[3].day_number, 5)

            cutEntryDao.insertAll(
                CutEntry("4:36pm", 1, "September", 9, 2020)
            )

            entriesFromCurrentYear = cutEntryDao.getAllCutsSortedAsync()
            assertEquals(entriesFromCurrentYear[0].day_number, 1)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteCut() {
        runBlocking {
            cutEntryDao.insertAll(
                CutEntry("4:36pm", 17, "September", 9, UtilFunctions.getCurrentYear()),
                CutEntry("4:36pm", 5, "October", 10, UtilFunctions.getCurrentYear()),
                CutEntry("4:36pm", 28, "September", 9, UtilFunctions.getCurrentYear()),
                CutEntry("4:36pm", 1, "October", 10, UtilFunctions.getCurrentYear())
            )

            var listOfEntries: List<CutEntry> = cutEntryDao.getAllCutsSortedAsync().reversed()
            println(cutEntryDao.getAllCutsSortedAsync())

            // delete both the october cuts and check each time afterwards
            cutEntryDao.deleteCutById(listOfEntries[0].id)
            println("Deleting ID = ${listOfEntries[0].id}")
            println(cutEntryDao.getAllCutsSortedAsync())
            var returnedEntry = cutEntryDao.getMostRecentCut()
            assertEquals(cutEntryDao.getNumEntries(), 3)
            assertEquals(returnedEntry!!.month_name, "October")

            listOfEntries = cutEntryDao.getAllCutsSortedAsync().reversed()
            cutEntryDao.deleteCutById(listOfEntries[0].id)
            println("Deleting ID = ${listOfEntries[0].id}")
            returnedEntry = cutEntryDao.getMostRecentCut()
            println(cutEntryDao.getAllCutsSortedAsync())
            assertEquals(cutEntryDao.getNumEntries(), 2)
            assertEquals(returnedEntry!!.month_name, "September")

            // check size after deleting
            assertEquals(cutEntryDao.getNumEntries(), 2)
        }
    }

    @Test
    fun testNotesOnCutEntry() {
        runBlocking {
            val note = "Hello world"
            cutEntryDao.insertAll(
                CutEntry("4:36pm", 5, "October", 10, UtilFunctions.getCurrentYear(), note)
            )

            assertNotNull(cutEntryDao.getMostRecentCut()!!.note)
            assertEquals(cutEntryDao.getMostRecentCut()!!.note, note)

            cutEntryDao.insertAll(
                CutEntry("4:36pm", 5, "December", 12, UtilFunctions.getCurrentYear()),
            )

            assertNull(cutEntryDao.getMostRecentCut()!!.note)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteAllCuts() = runBlocking {
        cutEntryDao.insertAll(CutEntry("4:36pm", 17, "September", 9, UtilFunctions.getCurrentYear()),
            CutEntry("4:36pm", 5, "October", 10, UtilFunctions.getCurrentYear()),
            CutEntry("4:36pm", 28, "September", 9, UtilFunctions.getCurrentYear()),
            CutEntry("4:36pm", 1, "October", 10, UtilFunctions.getCurrentYear()))

        // delete both the october cuts and check each time afterwards
        cutEntryDao.deleteAll()

        // check size after deleting
        assertEquals(cutEntryDao.getNumEntries(), 0)
    }

    @Test
    @Throws(Exception::class)
    fun testGetYearDropdownArray() = runBlocking {
        assertEquals(cutEntryDao.getYearDropdownArray().size, 0)
        assertEquals(cutEntryDao.getYearDropdownArray(), emptyList<String>())

        cutEntryDao.insertAll(CutEntry("4:36pm", 17, "September", 9, 2021),
            CutEntry("4:36pm", 5, "October", 10, 2021),
            CutEntry("4:36pm", 28, "September", 9, 2020),
            CutEntry("4:36pm", 1, "October", 10, 2020))
        cutEntryDao.insertAll(CutEntry("4:36pm", 17, "September", 9, 2018))

        assertEquals(cutEntryDao.getYearDropdownArray().size, 3)
        assertEquals(cutEntryDao.getYearDropdownArray(), listOf("2021", "2020", "2018"))

        cutEntryDao.insertAll(CutEntry("4:36pm", 17, "September", 9, 2019))

        assertEquals(cutEntryDao.getYearDropdownArray().size, 4)
        assertEquals(cutEntryDao.getYearDropdownArray(), listOf("2021", "2020", "2019", "2018"))
    }

    @Test
    fun hasExistingEntry() {
        val testCut = CutEntry("4:36pm", 17, "September", 9, 2021)
        val sameDayCut = CutEntry("2:56pm", 17, "September", 9, 2021)
        val sameDayCutDiffYear = CutEntry("2:56pm", 17, "September", 9, 2020)
        runBlocking {
            assertFalse(cutEntryDao.hasExistingCut(testCut))
            cutEntryDao.insertAll(testCut)
            assertTrue(cutEntryDao.hasExistingCut(testCut))
            assertTrue(cutEntryDao.hasExistingCut(sameDayCut))
            assertFalse(cutEntryDao.hasExistingCut(sameDayCutDiffYear))
        }
    }

    @After
    fun tearDown() {
        db.close()
    }
}