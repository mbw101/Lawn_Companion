package com.mbw101.lawn_companion.utils

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-13
 */
object Constants {
    const val DATABASE_NAME = "lawncompanion-db"
    const val CUTTING_SEASON_TABLE_NAME = "datesTable"
    val months: Array<String> = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec")
    const val IS_FIRST_TIME = "is_first_time"
    const val APPLICATION_PREFS = "app_preference"
    const val TAG = "LAWN COMPANION"

    enum class Month(val monthNum: Int) {
        JANUARY(1),
        FEBRUARY(2),
        MARCH(3),
        APRIL(4),
        MAY(5),
        JUNE(6),
        JULY(7),
        AUGUST(8),
        SEPTEMBER(9),
        OCTOBER(10),
        NOVEMBER(11),
        DECEMBER(12),
    }

    const val MORNING_HOUR_START_TIME = 5 // am
    const val MORNING_HOUR_END_TIME = 11

    const val AFTERNOON_HOUR_START_TIME = 12
    const val AFTERNOON_HOUR_END_TIME = 16 // 4 pm

    const val EVENING_HOUR_START_TIME = 17
    const val EVENING_HOUR_END_TIME = 20 // 8 pm

    const val NIGHT_HOUR_START_TIME = 21 // 9pm
    const val NIGHT_HOUR_END_TIME = 4  // 4am

    // weather frequency constants
    const val FIFTEEN_MINUTES = (15 * 60 * 1000)
    const val THIRTY_MINUTES = 2 * FIFTEEN_MINUTES
    const val ONE_HOUR = 2 * THIRTY_MINUTES
    const val TWO_HOURS = 2 * ONE_HOUR
}