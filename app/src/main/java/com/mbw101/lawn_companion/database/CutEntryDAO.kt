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

    // get cuts queries (all sorted) Jan-Dec order
    @Query("SELECT * FROM cuts_table ORDER BY month_num ASC, day_number ASC")
    fun getAllCutsSorted(): LiveData<List<CutEntry>>

    // This query should get the # of entries in the cuts table
    @Query("SELECT COUNT(*) FROM cuts_table")
    fun getNumEntries(): Int

    @Query("SELECT * FROM cuts_table WHERE month_name = :month")
    fun findByMonthName(month: String): LiveData<List<CutEntry>>

    @Query("SELECT * FROM cuts_table WHERE month_num = :month_num")
    fun findByMonthNum(month_num: Int): LiveData<List<CutEntry>>

    // (returns a single Cut Entry)
    @Query("SELECT * FROM cuts_table WHERE month_name = :month AND day_number = :day")
    fun getSpecificCut(month: String, day: Int): LiveData<CutEntry>

    @Query("SELECT * FROM cuts_table ORDER BY month_num DESC, day_number DESC LIMIT 1")
    fun getLastCut(): LiveData<CutEntry> // returns a single Cut Entry

<<<<<<< HEAD
=======
    @Query("SELECT * FROM cuts_table ORDER BY month_num DESC, day_number DESC LIMIT 1")
    suspend fun getLastCutSync(): CutEntry? // returns a single Cut Entry (synchronously) for the broadcast receiver

>>>>>>> develop
    @Query("SELECT * FROM cuts_table ORDER BY millis DESC LIMIT 1")
    fun getLastCutMillis(): LiveData<CutEntry> // returns a single Cut Entry by utilizing the millis member

    // TODO: Implement this query in the future when I implement Date objects into the database
//    @Query("SELECT * FROM cuts_table WHERE date = :targetDate")
//    fun getCutWithDate(targetDate: Date): CutEntry? (returns the CutEntry that has a matching Date object if it exists)

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
    @Delete
    suspend fun deleteCuts(vararg cuts: CutEntry): Int

    // Clears all entries in the "cuts" table
    @Query("DELETE FROM cuts_table")
    suspend fun deleteAll()
}