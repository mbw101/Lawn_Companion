package com.mbw101.lawn_companion.database

import androidx.room.ColumnInfo
import androidx.room.Entity

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-08-15
 */

@Entity(tableName = "location_table", primaryKeys = ["latitude", "longitude"])
data class LawnLocation (
    @ColumnInfo(name="latitude") val latitude: Double,
    @ColumnInfo(name="longitude") val longitude: Double
)