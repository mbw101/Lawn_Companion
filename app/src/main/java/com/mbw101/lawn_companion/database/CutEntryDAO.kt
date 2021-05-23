package com.mbw101.lawn_companion.database

import androidx.room.*

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-22
 */
@Dao
interface CutEntryDAO {

    // get cuts queries
    @Query("SELECT * FROM cuts_table")
    fun getAllCuts(): List<CutEntry>

    // This query should get the # of entries in the cuts table
    @Query("SELECT COUNT(*) FROM cuts_table")
    fun getNumEntries(): Int

    @Query("SELECT * FROM cuts_table WHERE month_name = :month")
    fun findByMonthName(month: String): List<CutEntry>

    @Query("SELECT * FROM cuts_table WHERE month_num = :month_num")
    fun findByMonthNum(month_num: Int): List<CutEntry>

    // (returns a single Cut Entry)
    @Query("SELECT * FROM cuts_table WHERE month_name = :month AND day_number = :day")
    fun getSpecificCut(month: String, day: Int): CutEntry

    @Query("SELECT * FROM cuts_table ORDER BY month_num DESC, day_number DESC LIMIT 1")
    fun getLastCut(): CutEntry // returns a single Cut Entry

    @Query("SELECT * FROM cuts_table ORDER BY millis DESC LIMIT 1")
    fun getLastCutMillis(): CutEntry // returns a single Cut Entry by utilizing the millis member

    // TODO: Implement this query in the future when I implement Date objects into the database
//    @Query("SELECT * FROM cuts_table WHERE date = :targetDate")
//    fun getCutWithDate(targetDate: Date): CutEntry? (returns the CutEntry that has a matching Date object if it exists)

    // insertion queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg cuts: CutEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(cuts: List<CutEntry>) // same as above method but with a list instead

    // update query
    // Will be used in the future where we allow the user to update existing cuts in the database
    @Update
    fun updateCut(vararg cuts: CutEntry)

    // delete queries

    @Delete
    fun deleteCuts(vararg cuts: CutEntry): Int

    // Clears all entries in the "cuts" table
    @Query("DELETE FROM cuts_table")
    fun deleteAll()
}