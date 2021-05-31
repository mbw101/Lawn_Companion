package com.mbw101.lawn_companion.database

import android.content.Context
import androidx.room.Room
import com.mbw101.lawn_companion.utils.Constants

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-29
 */
object DatabaseBuilder {

    private var INSTANCE: CutEntryDatabase? = null

    fun getInstance(context: Context): CutEntryDatabase {
        if (INSTANCE == null) {
            synchronized(CutEntryDatabase::class) {
                INSTANCE = buildRoomDB(context)
            }
        }
        return INSTANCE!!
    }

    private fun buildRoomDB(context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            CutEntryDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()

}