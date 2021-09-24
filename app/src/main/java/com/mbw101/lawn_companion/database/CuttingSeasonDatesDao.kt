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

        val currentDate: Date = Calendar.getInstance().time
        val startDate = getStartDate()!!.calendarValue.time
        val endDate = getEndDate()!!.calendarValue.time

        return (currentDate.after(startDate) && currentDate.before(endDate))
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