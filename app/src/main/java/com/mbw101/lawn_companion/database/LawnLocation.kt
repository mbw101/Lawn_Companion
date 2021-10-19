package com.mbw101.lawn_companion.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.mbw101.lawn_companion.utils.Constants

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-08-15
 */

@Entity(tableName = Constants.LAWN_LOCATION_TABLE_NAME, primaryKeys = ["latitude", "longitude"])
data class LawnLocation (
    @ColumnInfo(name="latitude") val latitude: Double,
    @ColumnInfo(name="longitude") val longitude: Double
)