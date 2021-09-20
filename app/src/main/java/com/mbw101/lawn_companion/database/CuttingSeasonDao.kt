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
interface CuttingSeasonDao {

    @Query("SELECT * FROM ${Constants.CUTTING_SEASON_TABLE_NAME} WHERE typeOfDate=:dateType")
    suspend fun getDateByType(dateType: DateType): CuttingSeasonDate

    suspend fun getStartDate(): CuttingSeasonDate = getDateByType(DateType.START_DATE)
    suspend fun getEndDate(): CuttingSeasonDate = getDateByType(DateType.END_DATE)

    suspend fun isInCuttingSeasonDates(): Boolean {
        // TODO: Look into determining if the current date is within the bounds
        val currentDate = Calendar.getInstance()
        return false
    }

    suspend fun outsideOfCuttingSeasonDates(): Boolean {
        return !isInCuttingSeasonDates()
    }

    // insertion queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg dates: CuttingSeasonDate)

    @Query("SELECT COUNT(*) FROM ${Constants.CUTTING_SEASON_TABLE_NAME}")
    suspend fun getNumEntries(): Int

    @Query("DELETE FROM ${Constants.CUTTING_SEASON_TABLE_NAME}")
    suspend fun deleteAll()
}