package com.mbw101.lawn_companion.utils

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-13
 */
object Constants {
    const val DATABASE_NAME = "lawncompanion-db"
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
}