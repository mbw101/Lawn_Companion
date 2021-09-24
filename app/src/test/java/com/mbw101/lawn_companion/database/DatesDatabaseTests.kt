package com.mbw101.lawn_companion.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.runner.AndroidJUnit4
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.*

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-09-18
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class DatesDatabaseTests {

    private lateinit var db: AppDatabase
    private lateinit var cuttingSeasonDao: CuttingSeasonDao

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        cuttingSeasonDao = db.cuttingSeasonDao()
    }

    @Test
    @Throws(Exception::class)
    fun testInsertAll() = runBlocking {
        val (startDate: Calendar, endDate: Calendar) = setupStartEndCalendars()
        val (startVal, endVal) = buildDates(startDate, endDate)

        cuttingSeasonDao.insertAll(startVal)
        assertEquals(cuttingSeasonDao.getNumEntries(), 1)
        cuttingSeasonDao.insertAll(endVal)
        assertEquals(cuttingSeasonDao.getNumEntries(), 2)
    }

    @Test
    @Throws(Exception::class)
    fun testDeletion() = runBlocking {
        val (startDate: Calendar, endDate: Calendar) = setupStartEndCalendars()
        val (startVal, endVal) = buildDates(startDate, endDate)
        insertDatesIntoDB(startVal, endVal)

        assertEquals(cuttingSeasonDao.getNumEntries(), 2)
        cuttingSeasonDao.deleteAll()
        assertEquals(cuttingSeasonDao.getNumEntries(), 0)
    }

    private suspend fun insertDatesIntoDB(startDate: CuttingSeasonDate, endDate: CuttingSeasonDate) {
        cuttingSeasonDao.insertAll(startDate)
        cuttingSeasonDao.insertAll(endDate)
    }

    @Test
    @Throws(Exception::class)
    fun testCalendarFunctionality() = runBlocking {
        val (startDate: Calendar, endDate: Calendar) = setupStartEndCalendars()
        val (startVal, endVal) = buildDates(startDate, endDate)
        insertDatesIntoDB(startVal, endVal)

        val startDateCopy = cuttingSeasonDao.getDateByType(DateType.START_DATE)
        val endDateCopy = cuttingSeasonDao.getDateByType(DateType.END_DATE)
        val startCal = startDateCopy.calendarValue
        val endCal = endDateCopy.calendarValue
        assertEquals(startCal, startDate)
        assertEquals(endCal, endDate)
    }

    private fun buildDates(
        startDate: Calendar,
        endDate: Calendar
    ): Pair<CuttingSeasonDate, CuttingSeasonDate> {
        val startVal = CuttingSeasonDate(DateType.START_DATE, startDate)
        val endVal = CuttingSeasonDate(DateType.END_DATE, endDate)
        return Pair(startVal, endVal)
    }

    private fun setupStartEndCalendars(): Pair<Calendar, Calendar> {
        val startDate: Calendar = Calendar.getInstance()
        val endDate: Calendar = Calendar.getInstance()

        startDate.set(Calendar.MONTH, Calendar.JANUARY)
        startDate.set(Calendar.DAY_OF_MONTH, 1)
        endDate.set(Calendar.MONTH, Calendar.SEPTEMBER)
        endDate.set(Calendar.DAY_OF_MONTH, 25)
        return Pair(startDate, endDate)
    }

    private fun setupOutsideStartEndCalendars(): Pair<Calendar, Calendar> {
        val startDate: Calendar = Calendar.getInstance()
        val endDate: Calendar = Calendar.getInstance()

        startDate.add(Calendar.DAY_OF_MONTH, 1)
        endDate.add(Calendar.DAY_OF_MONTH, 2)
        return Pair(startDate, endDate)
    }

    @Test
    @Throws(Exception::class)
    fun testGetStartAndEndDates() = runBlocking {
        val (startDate: Calendar, endDate: Calendar) = setupStartEndCalendars()
        val (startVal, endVal) = buildDates(startDate, endDate)
        insertDatesIntoDB(startVal, endVal)

        val startCal = cuttingSeasonDao.getStartDate().calendarValue
        val endCal = cuttingSeasonDao.getEndDate().calendarValue

        assertEquals(startCal.get(Calendar.MONTH), Calendar.JANUARY)
        assertEquals(endCal.get(Calendar.MONTH), Calendar.SEPTEMBER)
        assertEquals(startCal.get(Calendar.DAY_OF_MONTH), 1)
        assertEquals(endCal.get(Calendar.DAY_OF_MONTH), 25)
    }

    @Test
    @Throws(Exception::class)
    fun testInCuttingSeason() = runBlocking {
        val (startDate: Calendar, endDate: Calendar) = setupStartEndCalendars()

        val (startVal, endVal) = buildDates(startDate, endDate)
        insertDatesIntoDB(startVal, endVal)
        assertEquals(cuttingSeasonDao.isInCuttingSeasonDates(), true)
        assertEquals(cuttingSeasonDao.isOutsideOfCuttingSeasonDates(), false)
    }

    @Test
    @Throws(Exception::class)
    fun testOutsideCuttingSeason() = runBlocking {
        val (startDate: Calendar, endDate: Calendar) = setupOutsideStartEndCalendars()

        val (startVal, endVal) = buildDates(startDate, endDate)
        insertDatesIntoDB(startVal, endVal)
        assertEquals(cuttingSeasonDao.isOutsideOfCuttingSeasonDates(), true)
        assertEquals(cuttingSeasonDao.isInCuttingSeasonDates(), false)
    }

    @After
    fun tearDown() {
        db.close()
    }
}