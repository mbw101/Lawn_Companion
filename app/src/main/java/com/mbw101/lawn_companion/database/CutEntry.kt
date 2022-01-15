package com.mbw101.lawn_companion.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mbw101.lawn_companion.utils.Constants

/**
Lawn Companion
Created by Malcolm Wright
Date: May 16th, 2021
 */

//@Parcelize // lets us pass a CutEntry object in an intent
@Entity(tableName = Constants.CUT_ENTRY_TABLE_NAME)
data class CutEntry(

    @ColumnInfo(name = "cut_time") val cut_time: String,

    @ColumnInfo(name = "day_number") val day_number: Int,

    // used to organize into the month headings in CutLogActivity
    @ColumnInfo(name = "month_name") val month_name: String,

    @ColumnInfo(name = "month_num") val month_number: Int,

    @ColumnInfo(name = "year") val year: Int,

    @ColumnInfo(name = "cut_note") val note: String? = null,

    @ColumnInfo(name = "millis") val millis: Long = System.currentTimeMillis(),

    @PrimaryKey(autoGenerate = true) val id: Int = 0 // have ID last, so we don't need to specify named parameters

)