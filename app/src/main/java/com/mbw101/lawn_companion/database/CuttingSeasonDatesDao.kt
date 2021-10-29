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
        todayCalendar.set(Calendar.HOUR, 1)
        todayCalendar.set(Calendar.MINUTE, 0)
        todayCalendar.set(Calendar.AM_PM, Calendar.AM)
        todayCalendar.set(Calendar.MILLISECOND, startCal.get(Calendar.MILLISECOND))
        startCal.set(Calendar.HOUR, 1)
        startCal.set(Calendar.MINUTE, 0)
        startCal.set(Calendar.AM_PM, Calendar.AM)
        endCal.set(Calendar.HOUR, 1)
        endCal.set(Calendar.MINUTE, 0)
        endCal.set(Calendar.AM_PM, Calendar.AM)

        println("Today's calendar: $todayCalendar")
        println("Start date calendar: $startCal")
        println("End date's calendar: $endCal")

        val currentDate = todayCalendar.time
        val startDate = startCal.time
        val endDate = endCal.time

        println("!currentDate.before(startDate) = " + !currentDate.before(startDate))
        println("!currentDate.after(endDate) = " + !currentDate.after(endDate))

        // this allows for the dates to match and still be considered in the cutting season
        return !(currentDate.before(startDate) || currentDate.after(endDate))
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