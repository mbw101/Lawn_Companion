package com.mbw101.lawn_companion.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mbw101.lawn_companion.utils.Constants
import kotlinx.parcelize.Parcelize

/**
Lawn Companion
Created by Malcolm Wright
Date: May 16th, 2021
 */

@Entity(tableName = Constants.CUT_ENTRY_TABLE_NAME)
@Parcelize // lets us pass a CutEntry object in an intent
data class CutEntry(

    @ColumnInfo(name = "cut_time") var cut_time: String,

    @ColumnInfo(name = "day_number") var day_number: Int,

    // used to organize into the month headings in CutLogActivity
    @ColumnInfo(name = "month_name") var month_name: String,

    @ColumnInfo(name = "month_num") var month_number: Int,

    @ColumnInfo(name = "year") var year: Int,

    @ColumnInfo(name = "cut_note") var note: String? = null,

    @ColumnInfo(name = "millis") var millis: Long = System.currentTimeMillis(),

    @PrimaryKey(autoGenerate = true) val id: Int = 0 // have ID last, so we don't need to specify named parameters

) : Parcelable