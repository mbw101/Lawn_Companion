package com.mbw101.lawn_companion.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-30
 */

object LocationUtils {
    private lateinit var locationManager: LocationManager

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(context: Context): Location? {
        setupLocationManager(context)
        if (hasNoLocationPermissions(context)) {
            Log.d(Constants.TAG, "No location permissions!")
            return null
        }

        if (hasGpsProvider()) {
           return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
        Log.d(Constants.TAG, "Has no gps provider!")
        return null
    }

    @SuppressLint("MissingPermission")
    fun requestLocation(context: Context, locationListener: LocationListener) {
        setupLocationManager(context)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5.0f, locationListener);
    }

    fun stopLocationUpdates(locationListener: LocationListener) {
        locationManager.removeUpdates(locationListener)
    }

    private fun setupLocationManager(context: Context) {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private fun hasNoLocationPermissions(context: Context) =
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION // gps location provider requires fine location
        ) != PackageManager.PERMISSION_GRANTED

    private fun hasGpsProvider() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}