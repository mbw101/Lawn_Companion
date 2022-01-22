package com.mbw101.lawn_companion.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-30
 */

object LocationUtils {
    private lateinit var locationManager: LocationManager

    @SuppressLint("MissingPermission")
    fun requestLocation(context: Context, locationListener: LocationListener) {
        setupLocationManager(context)
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 0.0f, locationListener)
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