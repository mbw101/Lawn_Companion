package com.mbw101.lawn_companion.utils

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.mbw101.lawn_companion.ui.MyApplication
import java.util.*
import java.util.Calendar.DAY_OF_YEAR

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
}