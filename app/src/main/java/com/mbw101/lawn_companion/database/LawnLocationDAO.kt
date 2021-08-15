package com.mbw101.lawn_companion.database

import androidx.room.*

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-08-15
 */
@Dao
interface LawnLocationDAO {

    @Query("SELECT * FROM location_table LIMIT 1")
    fun getFirstLocation(): LawnLocation?

    @Query("SELECT * FROM location_table")
    fun getAllLocations(): List<LawnLocation>?

    // insertion queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg locations: LawnLocation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LawnLocation)

    @Query("SELECT COUNT(*) FROM location_table")
    suspend fun getNumEntries(): Int

    @Delete
    suspend fun deleteLocations(vararg locations: LawnLocation): Int

    @Query("DELETE FROM location_table")
    suspend fun deleteAll()
}