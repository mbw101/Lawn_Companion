package com.mbw101.lawn_companion.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mbw101.lawn_companion.utils.Constants
import java.util.*

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-09-18
 */
@Dao
interface CuttingSeasonDatesDao {

    suspend fun hasStartDate(): Boolean {
        if (getDateByType(DateType.START_DATE) == null) {
            return false
        }
        return true
    }

    suspend fun hasEndDate(): Boolean {
        if (getDateByType(DateType.END_DATE) == null) {
            return false
        }
        return true
    }

    @Query("SELECT * FROM ${Constants.CUTTING_SEASON_TABLE_NAME} WHERE typeOfDate=:dateType")
    suspend fun getDateByType(dateType: DateType): CuttingSeasonDate?

    suspend fun getStartDate(): CuttingSeasonDate? = getDateByType(DateType.START_DATE)
    suspend fun getEndDate(): CuttingSeasonDate? = getDateByType(DateType.END_DATE)

    suspend fun isInCuttingSeasonDates(): Boolean {
        if (!hasEndDate() || !hasStartDate()) {
            return false
        }

        val todayCalendar = Calendar.getInstance()
        val startCal = getStartDate()!!.calendarValue
        val endCal = getEndDate()!!.calendarValue

        // ensure all the times are the same before getting dates
        ensureCalendarsWorkWithComparison(todayCalendar, startCal, endCal)

        val currentDate = todayCalendar.time
        val startDate = startCal.time
        val endDate = endCal.time

        // this allows for the dates to match and still be considered in the cutting season
        return !(currentDate.before(startDate) || currentDate.after(endDate))
    }

    // For the calendar comparison to work properly when the current calendar aligns with either the start or end date,
    // we need the second and millisecond parts to match one another.
    private fun ensureCalendarsWorkWithComparison(todayCalendar: Calendar, startCal: Calendar, endCal: Calendar) {
        todayCalendar.set(Calendar.HOUR_OF_DAY, 1)
        todayCalendar.set(Calendar.HOUR, 1)
        todayCalendar.set(Calendar.MINUTE, 0)
        todayCalendar.set(Calendar.SECOND, 0)
        todayCalendar.set(Calendar.MILLISECOND, 0)
        todayCalendar.set(Calendar.AM_PM, Calendar.AM)

        startCal.set(Calendar.HOUR_OF_DAY, 1)
        startCal.set(Calendar.HOUR, 1)
        startCal.set(Calendar.MINUTE, 0)
        startCal.set(Calendar.SECOND, 0)
        startCal.set(Calendar.MILLISECOND, 0)
        startCal.set(Calendar.AM_PM, Calendar.AM)

        endCal.set(Calendar.HOUR_OF_DAY, 1)
        endCal.set(Calendar.HOUR, 1)
        endCal.set(Calendar.MINUTE, 0)
        endCal.set(Calendar.SECOND, 0)
        endCal.set(Calendar.MILLISECOND, 0)
        endCal.set(Calendar.AM_PM, Calendar.AM)
    }

    suspend fun isOutsideOfCuttingSeasonDates(): Boolean {
        return !isInCuttingSeasonDates()
    }

    // insertion queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg dates: CuttingSeasonDate)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStartDate(startDate: Calendar) {
        val cuttingSeasonDate = CuttingSeasonDate(DateType.START_DATE, startDate)
        insertAll(cuttingSeasonDate)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEndDate(endDate: Calendar) {
        val cuttingSeasonDate = CuttingSeasonDate(DateType.END_DATE, endDate)
        insertAll(cuttingSeasonDate)
    }

    @Query("SELECT COUNT(*) FROM ${Constants.CUTTING_SEASON_TABLE_NAME}")
    suspend fun getNumEntries(): Int

    @Query("DELETE FROM ${Constants.CUTTING_SEASON_TABLE_NAME}")
    suspend fun deleteAll()

    @Query("DELETE FROM ${Constants.CUTTING_SEASON_TABLE_NAME} WHERE typeOfDate=:dateType")
    suspend fun deleteDateByType(dateType: DateType)

    // date specific deletion
    suspend fun deleteStartDate() = deleteDateByType(DateType.START_DATE)
    suspend fun deleteEndDate() = deleteDateByType(DateType.END_DATE)
}