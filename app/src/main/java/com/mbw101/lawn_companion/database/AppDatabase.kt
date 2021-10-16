package com.mbw101.lawn_companion.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-22
 */

@Database(entities = [CutEntry::class, LawnLocation::class,
    CuttingSeasonDate::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cutEntryDao(): CutEntryDAO
    abstract fun lawnLocationDao(): LawnLocationDAO
    abstract fun cuttingSeasonDatesDao(): CuttingSeasonDatesDao
}