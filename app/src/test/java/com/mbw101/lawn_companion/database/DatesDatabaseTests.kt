package com.mbw101.lawn_companion.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
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
@RunWith(AndroidJUnit4 ::class)
@Config(sdk = [28])
class DatesDatabaseTests {

    private lateinit var db: AppDatabase
    private lateinit var cuttingSeasonDatesDao: CuttingSeasonDatesDao
    private val startDateMonth = Calendar.JANUARY
    private val startDateDay = 4
    private val endDateMonth = Calendar.DECEMBER
    private val endDateDay = 31

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        cuttingSeasonDatesDao = db.cuttingSeasonDatesDao()
    }

    @Test
    @Throws(Exception::class)
    fun testInsertAll() = runBlocking {
        val (startDate: Calendar, endDate: Calendar) = setupStartEndCalendars()
        val (startVal, endVal) = buildDates(startDate, endDate)

        cuttingSeasonDatesDao.insertAll(startVal)
        assertEquals(cuttingSeasonDatesDao.getNumEntries(), 1)
        cuttingSeasonDatesDao.insertAll(endVal)
        assertEquals(cuttingSeasonDatesDao.getNumEntries(), 2)
    }

    @Test
    @Throws(Exception::class)
    fun testInsertStartDate() = runBlocking {
        val (startDate: Calendar, _: Calendar) = setupStartEndCalendars()

        cuttingSeasonDatesDao.insertStartDate(startDate)
        assertEquals(cuttingSeasonDatesDao.getNumEntries(), 1)
        assertEquals(cuttingSeasonDatesDao.getStartDate()!!.typeOfDate, DateType.START_DATE)
        assertEquals(cuttingSeasonDatesDao.getStartDate()!!.calendarValue, startDate)
    }

    @Test
    @Throws(Exception::class)
    fun testInsertEndDate() = runBlocking {
        val (_: Calendar, endDate: Calendar) = setupStartEndCalendars()

        cuttingSeasonDatesDao.insertEndDate(endDate)
        assertEquals(cuttingSeasonDatesDao.getNumEntries(), 1)
        assertEquals(cuttingSeasonDatesDao.getEndDate()!!.typeOfDate, DateType.END_DATE)
        assertEquals(cuttingSeasonDatesDao.getEndDate()!!.calendarValue, endDate)
    }

    @Test
    @Throws(Exception::class)
    fun testHasStartEndDates() = runBlocking {
        val (startDate: Calendar, endDate: Calendar) = setupStartEndCalendars()

        assertHasNoDates()
        cuttingSeasonDatesDao.insertEndDate(endDate)
        assertOnlyHasEndDate()
        cuttingSeasonDatesDao.insertStartDate(startDate)
        assertHasBothDates()
    }

    @Test
    @Throws(Exception::class)
    fun testDeletion() = runBlocking {
        val (startDate: Calendar, endDate: Calendar) = setupStartEndCalendars()
        val (startVal, endVal) = buildDates(startDate, endDate)
        insertDatesIntoDB(startVal, endVal)

        assertEquals(cuttingSeasonDatesDao.getNumEntries(), 2)
        cuttingSeasonDatesDao.deleteAll()
        assertEquals(cuttingSeasonDatesDao.getNumEntries(), 0)
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteDateByType() = runBlocking {
        val (startDate: Calendar, endDate: Calendar) = setupStartEndCalendars()
        val (startVal, endVal) = buildDates(startDate, endDate)
        insertDatesIntoDB(startVal, endVal)

        assertEquals(cuttingSeasonDatesDao.getNumEntries(), 2)

        cuttingSeasonDatesDao.deleteDateByType(DateType.START_DATE)
        assertEquals(cuttingSeasonDatesDao.getNumEntries(), 1)
        assertOnlyHasEndDate()

        cuttingSeasonDatesDao.deleteDateByType(DateType.END_DATE)
        assertEquals(cuttingSeasonDatesDao.getNumEntries(), 0)
        assertHasNoDates()

        insertDatesIntoDB(startVal, endVal)
        cuttingSeasonDatesDao.deleteDateByType(DateType.END_DATE)
        assertEquals(cuttingSeasonDatesDao.getNumEntries(), 1)
        assertOnlyHasStartDate()
    }

    @Test
    @Throws(Exception::class)
    fun testDateSpecificDeletion() = runBlocking {
        val (startDate: Calendar, endDate: Calendar) = setupStartEndCalendars()
        val (startVal, endVal) = buildDates(startDate, endDate)
        insertDatesIntoDB(startVal, endVal)

        assertEquals(cuttingSeasonDatesDao.getNumEntries(), 2)

        cuttingSeasonDatesDao.deleteStartDate()
        assertEquals(cuttingSeasonDatesDao.getNumEntries(), 1)
        assertOnlyHasEndDate()

        cuttingSeasonDatesDao.deleteEndDate()
        assertEquals(cuttingSeasonDatesDao.getNumEntries(), 0)
        assertHasNoDates()

        insertDatesIntoDB(startVal, endVal)
        cuttingSeasonDatesDao.deleteEndDate()
        assertEquals(cuttingSeasonDatesDao.getNumEntries(), 1)
        assertOnlyHasStartDate()
    }

    private suspend fun insertDatesIntoDB(startDate: CuttingSeasonDate, endDate: CuttingSeasonDate) {
        cuttingSeasonDatesDao.insertAll(startDate)
        cuttingSeasonDatesDao.insertAll(endDate)
    }

    private suspend fun assertHasNoDates() {
        assertEquals(cuttingSeasonDatesDao.hasStartDate(), false)
        assertEquals(cuttingSeasonDatesDao.hasEndDate(), false)
    }

    private suspend fun assertOnlyHasStartDate() {
        assertEquals(cuttingSeasonDatesDao.hasEndDate(), false)
        assertEquals(cuttingSeasonDatesDao.hasStartDate(), true)
    }

    private suspend fun assertOnlyHasEndDate() {
        assertEquals(cuttingSeasonDatesDao.hasEndDate(), true)
        assertEquals(cuttingSeasonDatesDao.hasStartDate(), false)
    }

    private suspend fun assertHasBothDates() {
        assertEquals(cuttingSeasonDatesDao.hasStartDate(), true)
        assertEquals(cuttingSeasonDatesDao.hasEndDate(), true)
    }

    @Test
    @Throws(Exception::class)
    fun testCalendarFunctionality() = runBlocking {
        val (startDate: Calendar, endDate: Calendar) = setupStartEndCalendars()
        val (startVal, endVal) = buildDates(startDate, endDate)
        insertDatesIntoDB(startVal, endVal)

        val startDateCopy = cuttingSeasonDatesDao.getDateByType(DateType.START_DATE)
        val endDateCopy = cuttingSeasonDatesDao.getDateByType(DateType.END_DATE)
        val startCal = startDateCopy!!.calendarValue
        val endCal = endDateCopy!!.calendarValue
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

        startDate.set(Calendar.MONTH, startDateMonth)
        startDate.set(Calendar.DAY_OF_MONTH, startDateDay)
        endDate.set(Calendar.MONTH, endDateMonth)
        endDate.set(Calendar.DAY_OF_MONTH, endDateDay)
        return Pair(startDate, endDate)
    }

    private fun setupOutsideStartEndCalendars(): Pair<Calendar, Calendar> {
        val startDate: Calendar = Calendar.getInstance()
        val endDate: Calendar = Calendar.getInstance()

        startDate.add(Calendar.DAY_OF_MONTH, 1)
        endDate.add(Calendar.DAY_OF_MONTH, 2)
        println(startDate)
        println(endDate)
        return Pair(startDate, endDate)
    }

    @Test
    @Throws(Exception::class)
    fun testGetStartAndEndDates() = runBlocking {
        val (startDate: Calendar, endDate: Calendar) = setupStartEndCalendars()
        val (startVal, endVal) = buildDates(startDate, endDate)
        insertDatesIntoDB(startVal, endVal)

        val startCal = cuttingSeasonDatesDao.getStartDate()!!.calendarValue
        val endCal = cuttingSeasonDatesDao.getEndDate()!!.calendarValue

        assertEquals(startCal.get(Calendar.MONTH), startDateMonth)
        assertEquals(endCal.get(Calendar.MONTH), endDateMonth)
        assertEquals(startCal.get(Calendar.DAY_OF_MONTH), startDateDay)
        assertEquals(endCal.get(Calendar.DAY_OF_MONTH), endDateDay)
    }

    @Test
    @Throws(Exception::class)
    fun testInCuttingSeason() = runBlocking {
        val (startDate: Calendar, endDate: Calendar) = setupStartEndCalendars()

        val (startVal, endVal) = buildDates(startDate, endDate)
        insertDatesIntoDB(startVal, endVal)
        assertEquals(cuttingSeasonDatesDao.isInCuttingSeasonDates(), true)
        assertEquals(cuttingSeasonDatesDao.isOutsideOfCuttingSeasonDates(), false)
    }

    // Edge cases where the today's calendar aligns on the same day as both the start/end dates and is offset by one of the time fields
    @Test
    @Throws(Exception::class)
    fun testInCuttingSeasonHourOfDayEdgeCase() = runBlocking {
        val startCal = Calendar.getInstance()
        val endCal = Calendar.getInstance()
        startCal.set(Calendar.HOUR_OF_DAY, 10)
        endCal.set(Calendar.HOUR_OF_DAY, 5)

        val (startVal, endVal) = buildDates(startCal, endCal)
        insertDatesIntoDB(startVal, endVal)
        assertEquals(cuttingSeasonDatesDao.isInCuttingSeasonDates(), true)
    }

    @Test
    @Throws(Exception::class)
    fun testInCuttingSeasonHourEdgeCase() = runBlocking {
        val startCal = Calendar.getInstance()
        val endCal = Calendar.getInstance()
        startCal.set(Calendar.HOUR, 10)
        endCal.set(Calendar.HOUR, 5)

        val (startVal, endVal) = buildDates(startCal, endCal)
        insertDatesIntoDB(startVal, endVal)
        assertEquals(cuttingSeasonDatesDao.isInCuttingSeasonDates(), true)
    }

    @Test
    @Throws(Exception::class)
    fun testInCuttingSeasonMinuteEdgeCase() = runBlocking {
        val startCal = Calendar.getInstance()
        val endCal = Calendar.getInstance()
        startCal.set(Calendar.MINUTE, 10)
        endCal.set(Calendar.MINUTE, 5)

        val (startVal, endVal) = buildDates(startCal, endCal)
        insertDatesIntoDB(startVal, endVal)
        assertEquals(cuttingSeasonDatesDao.isInCuttingSeasonDates(), true)
    }

    @Test
    @Throws(Exception::class)
    fun testInCuttingSeasonSecondEdgeCase() = runBlocking {
        val startCal = Calendar.getInstance()
        val endCal = Calendar.getInstance()
        startCal.set(Calendar.SECOND, 10)
        endCal.set(Calendar.SECOND, 5)

        val (startVal, endVal) = buildDates(startCal, endCal)
        insertDatesIntoDB(startVal, endVal)
        assertEquals(cuttingSeasonDatesDao.isInCuttingSeasonDates(), true)
    }

    @Test
    @Throws(Exception::class)
    fun testInCuttingSeasonMillisecondEdgeCase() = runBlocking {
        val startCal = Calendar.getInstance()
        val endCal = Calendar.getInstance()
        startCal.set(Calendar.MILLISECOND, 10)
        endCal.set(Calendar.MILLISECOND, 5)

        val (startVal, endVal) = buildDates(startCal, endCal)
        insertDatesIntoDB(startVal, endVal)
        assertEquals(cuttingSeasonDatesDao.isInCuttingSeasonDates(), true)
    }

    @Test
    @Throws(Exception::class)
    fun testInCuttingSeasonPMEdgeCase() = runBlocking {
        val startCal = Calendar.getInstance()
        val endCal = Calendar.getInstance()
        startCal.set(Calendar.AM_PM, Calendar.PM)
        endCal.set(Calendar.AM_PM, Calendar.PM)

        val (startVal, endVal) = buildDates(startCal, endCal)
        insertDatesIntoDB(startVal, endVal)
        assertEquals(cuttingSeasonDatesDao.isInCuttingSeasonDates(), true)
    }


    @Test
    @Throws(Exception::class)
    fun testOutsideCuttingSeason() = runBlocking {
        val (startDate: Calendar, endDate: Calendar) = setupOutsideStartEndCalendars()

        val (startVal, endVal) = buildDates(startDate, endDate)
        insertDatesIntoDB(startVal, endVal)
        assertEquals(cuttingSeasonDatesDao.isOutsideOfCuttingSeasonDates(), true)
        assertEquals(cuttingSeasonDatesDao.isInCuttingSeasonDates(), false)
    }

    @After
    fun tearDown() {
        db.close()
    }
}