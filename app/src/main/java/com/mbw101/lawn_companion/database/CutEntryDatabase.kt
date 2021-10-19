package com.mbw101.lawn_companion.database

import androidx.room.Database
import androidx.room.RoomDatabase

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-22
 */

@Database(entities = [CutEntry::class], version = 1)
abstract class CutEntryDatabase : RoomDatabase() {
    abstract fun cutEntryDao(): CutEntryDAO
}