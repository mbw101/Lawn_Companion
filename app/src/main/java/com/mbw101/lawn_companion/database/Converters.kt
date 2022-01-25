package com.mbw101.lawn_companion.database

import androidx.room.TypeConverter
import java.util.*

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-09-18
 */

enum class DateType(val typeOfDate: String){
    START_DATE("startdate"),
    END_DATE("enddate"),
    ERROR("error")
}

object Converters {
    @TypeConverter
    fun fromTimestamp(longValue: Long?): Calendar? = longValue?.let { value ->
        GregorianCalendar().also { calendar ->
            calendar.timeInMillis = value
        }
    }

    @TypeConverter
    fun toTimestamp(timestamp: Calendar?): Long? = timestamp?.timeInMillis

    @TypeConverter
    fun fromDateType(value: DateType): String {
        return value.typeOfDate
    }

    @TypeConverter
    fun toDateType(value: String): DateType {
        return when(value.lowercase()){
            "startdate" -> DateType.START_DATE
            "enddate" -> DateType.END_DATE
            else -> DateType.ERROR
        }
    }
}