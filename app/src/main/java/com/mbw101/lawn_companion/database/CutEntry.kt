package com.mbw101.lawn_companion.database

import androidx.room.ColumnInfo
import androidx.room.Entity

/**
Lawn Companion
Created by Malcolm Wright
Date: May 16th, 2021
 */

@Entity(tableName = "cuts_table", primaryKeys = ["month_name", "day_number"])
data class CutEntry (

    @ColumnInfo(name="cut_time") val time: String,

    @ColumnInfo(name="day_number") val day: Int,

    // used to organize into the month headings in CutLogActivity
    @ColumnInfo(name="month_name") val month: String,

    @ColumnInfo(name="month_num") val monthNum: Int,

    @ColumnInfo(name = "millis") val millis: Long = System.currentTimeMillis()

    // TODO: Implement converters, so we can add Date objects into database
//    @ColumnInfo(name = "date") val cutDate: Date?
)