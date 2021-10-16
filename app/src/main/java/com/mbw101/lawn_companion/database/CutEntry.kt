package com.mbw101.lawn_companion.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.mbw101.lawn_companion.utils.Constants

/**
Lawn Companion
Created by Malcolm Wright
Date: May 16th, 2021
 */

@Entity(tableName = Constants.CUT_ENTRY_TABLE_NAME, primaryKeys = ["month_name", "day_number"])
data class CutEntry(

    @ColumnInfo(name = "cut_time") val cut_time: String,

    @ColumnInfo(name = "day_number") val day_number: Int,

    // used to organize into the month headings in CutLogActivity
    @ColumnInfo(name = "month_name") val month_name: String,

    @ColumnInfo(name = "month_num") val month_number: Int,

    @ColumnInfo(name = "year") val year: Int,

    @ColumnInfo(name = "millis") val millis: Long = System.currentTimeMillis(),
)