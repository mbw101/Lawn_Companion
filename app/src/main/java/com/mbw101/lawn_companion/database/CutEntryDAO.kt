package com.mbw101.lawn_companion.database

import androidx.lifecycle.LiveData
import androidx.room.*

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-22
 */
@Dao
interface CutEntryDAO {

    // get cuts queries (all sorted)
    @Query("SELECT * FROM cuts_table")
    fun getAllCuts(): LiveData<List<CutEntry>>

    @Query("SELECT * FROM cuts_table")
    fun getAllCutsRegularReturn(): List<CutEntry>

    @Query("SELECT * FROM cuts_table ORDER BY year DESC")
    fun getAllCutsOrderedByYear(): List<CutEntry>

    // get cuts queries (all sorted) Jan-Dec order
    @Query("SELECT * FROM cuts_table ORDER BY month_num ASC, day_number ASC")
    fun getAllCutsSorted(): LiveData<List<CutEntry>>

    @Query("SELECT * FROM cuts_table ORDER BY month_num ASC, day_number ASC")
    fun getAllCutsSortedAsync(): List<CutEntry>

    // This query should get the # of entries in the cuts table
    @Query("SELECT COUNT(*) FROM cuts_table")
    fun getNumEntries(): Int

    @Query("SELECT * FROM cuts_table WHERE month_name = :month")
    suspend fun findByMonthName(month: String): List<CutEntry>?

    @Query("SELECT * FROM cuts_table WHERE month_num = :month_num")
    suspend fun findByMonthNum(month_num: Int): List<CutEntry>?

    // (returns a single Cut Entry)
    @Query("SELECT * FROM cuts_table WHERE month_name = :month AND day_number = :day")
    suspend fun getSpecificCut(month: String, day: Int): CutEntry?

    // this query finds the most recent cut that's in the DB. Since the UI does not allow for future entries,
    // we can assume that the most recent cut will be the cut with the largest year, month, and day values
    @Query("SELECT * FROM cuts_table ORDER BY year DESC, month_num DESC, day_number DESC LIMIT 1")
    suspend fun getMostRecentCut(): CutEntry? // returns a single Cut Entry

    @Query("SELECT * FROM cuts_table ORDER BY year DESC, month_num DESC, day_number DESC LIMIT 1")
    suspend fun getMostRecentCutAsync(): CutEntry? // returns a single Cut Entry (synchronously) for the broadcast receiver

    @Query("SELECT * FROM cuts_table ORDER BY millis DESC LIMIT 1")
    suspend fun getLastCutMillis(): CutEntry? // returns a single Cut Entry by utilizing the millis member

    @Query("SELECT * FROM cuts_table WHERE year = :year")
    suspend fun getEntriesFromSpecificYear(year: Int): List<CutEntry>

    @Query("SELECT * FROM cuts_table WHERE year = :year ORDER BY month_num ASC, day_number ASC")
    suspend fun getEntriesFromSpecificYearSortedAsync(year: Int): List<CutEntry>

    @Query("SELECT * FROM cuts_table WHERE year = :year ORDER BY month_num DESC, day_number DESC")
    suspend fun getLastEntryFromSpecificYear(year: Int): CutEntry?

    suspend fun getYearDropdownArray(): List<String> {
        val entries: List<CutEntry> = getAllCutsOrderedByYear()
        val yearDropdownArray: MutableList<String> = mutableListOf()

        // Not very efficient but takes into account every possible year (ex: non-consecutive years)
        for (entry in entries) {
            if (!yearDropdownArray.contains(entry.year.toString())) {
                yearDropdownArray.add(entry.year.toString())
            }
        }

        return yearDropdownArray
    }

    // insertion queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg cuts: CutEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cuts: List<CutEntry>) // same as above method but with a list instead

    // update query
    // Will be used in the future where we allow the user to update existing cuts in the database
    @Update
    suspend fun updateCut(vararg cuts: CutEntry)

    // delete queries
    @Query("DELETE FROM cuts_table WHERE id = :cutId")
    suspend fun deleteCutById(cutId: Int)

    @Delete
    suspend fun deleteCuts(vararg cuts: CutEntry)

    // Clears all entries in the "cuts" table
    @Query("DELETE FROM cuts_table")
    suspend fun deleteAll()
}