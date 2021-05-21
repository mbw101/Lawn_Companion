package com.mbw101.lawn_companion.utils

import android.Manifest
import android.R
import android.app.Activity
import android.app.AlertDialog.Builder
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mbw101.lawn_companion.ui.MyApplication
import java.util.*
import java.util.Calendar.DAY_OF_YEAR

object UtilFunctions {
   private const val MY_PERMISSIONS_REQUEST_LOCATION = 99

    // TODO: Fix this function, so the dialog actually pops up
    // check for background location permissions
    fun checkLocationPermissions(activity: Activity): Boolean {
        Log.d(Constants.TAG, "Asking for permissions!")
        return if (ContextCompat.checkSelfPermission(MyApplication.applicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // see if we need to show an explanation
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {

                // Show an explanation to the user
                Builder(MyApplication.applicationContext())
                    .setTitle("Title")
                    .setMessage("Test")
                    .setPositiveButton(R.string.ok,
                        DialogInterface.OnClickListener { dialogInterface, i -> //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(
                                activity,
                                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                                MY_PERMISSIONS_REQUEST_LOCATION
                            )
                        })
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
            false
        } else {
            true
        }
    }

    /***
     * Avoids having to deal with GregorianCalendar
     */
    fun isLeapYear(year: Int): Boolean {
        val cal: Calendar = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        return cal.getActualMaximum(DAY_OF_YEAR) > 365
    }
}