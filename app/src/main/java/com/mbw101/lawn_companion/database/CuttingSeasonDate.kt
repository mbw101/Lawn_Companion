package com.mbw101.lawn_companion.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.mbw101.lawn_companion.utils.Constants
import java.util.*

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-09-18
 */
@Entity(tableName = Constants.CUTTING_SEASON_TABLE_NAME, primaryKeys = ["typeOfDate"])
data class CuttingSeasonDate (
    @ColumnInfo(name="typeOfDate") val typeOfDate: DateType,
    @ColumnInfo(name="calendarValue") val calendarValue: Calendar
)