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
    // TODO: Get this figured out (this and the one below will be used for getDescriptionMessage)
    fun getNumDaysBetween(start: Calendar, end: Calendar): Int {
        // set hours and minutes to 0
        start.set(Calendar.HOUR_OF_DAY, 12)
        end.set(Calendar.HOUR_OF_DAY, 12)
        start.set(Calendar.MINUTE, 0)
        end.set(Calendar.MINUTE, 0)

        val millis = abs(end.timeInMillis - start.timeInMillis)

//        return (millis / (24 * 60 * 60 * 1000)).toInt()
        return TimeUnit.MILLISECONDS.toDays(millis).toInt()
    }

    fun sameDate(date1: Calendar, date2: Calendar): Boolean {
        return date1.get(DAY_OF_YEAR) == date2.get(DAY_OF_YEAR) &&
                date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR)
    }

    // Finds num days since the given date (using current date)
    fun getNumDaysSince(date: Calendar): Int {
        return getNumDaysBetween(date, Calendar.getInstance())
    }
}