package com.mbw101.lawn_companion.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-30
 */

object LocationUtils {
    lateinit var locationManager: LocationManager

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(context: Context): Location? {
        setupLocationManager(context)
        if (hasNoLocationPermissions(context)) {
            return null
        }

        if (hasGpsProvider()) {
           return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
        return null
    }

    private fun setupLocationManager(context: Context) {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private fun hasNoLocationPermissions(context: Context) =
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED

    private fun hasGpsProvider() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}