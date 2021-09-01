package com.mbw101.lawn_companion.database

import android.content.Context
import androidx.room.Room
import com.mbw101.lawn_companion.utils.Constants

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-29
 */
object AppDatabaseBuilder {

    private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
        if (INSTANCE == null) {
            synchronized(AppDatabase::class) {
                INSTANCE = buildRoomDB(context)
            }
        }
        return INSTANCE!!
    }

    private fun buildRoomDB(context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()

}