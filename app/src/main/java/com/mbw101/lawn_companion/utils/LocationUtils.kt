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

        if (hasNetworkProvider()) {
           return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }
        Log.d(Constants.TAG, "Has no network provider!")
        return null
    }

    @SuppressLint("MissingPermission")
    fun requestLocation(context: Context, locationListener: LocationListener) {
        setupLocationManager(context)
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1000.0f, locationListener)
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
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED

    private fun hasNetworkProvider() = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}