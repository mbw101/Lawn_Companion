package com.mbw101.lawn_companion.utils

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.mbw101.lawn_companion.ui.MyApplication
import java.util.*
import java.util.Calendar.DAY_OF_YEAR
import java.util.concurrent.TimeUnit
import kotlin.math.abs

object UtilFunctions {
   private const val MY_PERMISSIONS_REQUEST_LOCATION = 99

    /***
     * Returns true if the COARSE location permission has been granted
     */
    fun hasLocationPermissions(): Boolean {
        return (ContextCompat.checkSelfPermission(MyApplication.applicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED)
    }


    /***
     * Avoids having to deal with GregorianCalendar
     */
    fun isLeapYear(year: Int): Boolean {
        val cal: Calendar = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        return cal.getActualMaximum(DAY_OF_YEAR) > 365
    }

    fun getCurrentYear(): Int {
        // add each month section
        val cal: Calendar = Calendar.getInstance()
        return cal.get(Calendar.YEAR)
    }

    // returns the number of days between the two dates
    // The order does NOT made for the calendar objects
    // TODO: Get this figured out (this and the one below will be used for getDescriptionMessage)
    fun getNumDaysBetween(date1: Calendar, date2: Calendar): Int {
        // set hours and minutes to 0
        date1.set(Calendar.HOUR, 12)
        date2.set(Calendar.HOUR, 12)
        date1.set(Calendar.MINUTE, 0)
        date2.set(Calendar.MINUTE, 0)

        val millis = abs(date2.timeInMillis - date1.timeInMillis)
        return TimeUnit.MILLISECONDS.toDays(millis).toInt()
    }

    // Finds num days since the given date (using current date)
    fun getNumDaysSince(date: Calendar): Int {
        return getNumDaysBetween(date, Calendar.getInstance())
    }
}