package com.mbw101.lawn_companion.ui

import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mbw101.lawn_companion.database.LawnLocation
import com.mbw101.lawn_companion.database.LawnLocationRepository
import com.mbw101.lawn_companion.database.setupLawnLocationRepository
import com.mbw101.lawn_companion.databinding.ActivitySaveLocationBinding
import com.mbw101.lawn_companion.utils.ApplicationPrefs
import com.mbw101.lawn_companion.utils.Constants
import com.mbw101.lawn_companion.utils.LocationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.concurrent.schedule

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-08-15
 */

class SaveLocationActivity : AppCompatActivity(), LocationListener {

    private lateinit var denySaveButton: Button
    private lateinit var acceptSaveButton: Button
    private lateinit var lawnLocationRepository: LawnLocationRepository
    var locationGps: Location? = null
    private lateinit var binding: ActivitySaveLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaveLocationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // request location update now since it takes some time
        LocationUtils.requestLocation(this, this)
        init()
    }

    private fun init() {
        createButtons()
        setupRepo()
        setupListeners()
    }

    private fun createButtons() {
        denySaveButton = binding.denySaveLocationButton
        acceptSaveButton = binding.acceptSaveLocationButton
    }

    private fun setupRepo() {
        lawnLocationRepository = setupLawnLocationRepository(this)
    }

    private fun setupListeners() {
        denySaveButton.setOnClickListener {
            launchMainActivity()
        }

        acceptSaveButton.setOnClickListener {
            // check for location permission
            Toast.makeText(this, "Saving lawn location...", Toast.LENGTH_LONG).show()
            val locationListener = this

            val pref = ApplicationPrefs()
            if (!pref.hasLocationSaved() || locationGps == null) {
                // TODO: Figure out what we should do here
                locationGps = LocationUtils.getLastKnownLocation(this)
                saveLocation()
            }
            else {
                saveLocation()
            }

            Timer().schedule(1500) {
                LocationUtils.stopLocationUpdates(locationListener) // stops the usage of gps when we are done with it
                launchMainActivity()
            }
        }
    }

    private fun saveLocation() {
        if (locationGps == null) {
            return
        }
        createCoroutineForDB(locationGps!!)
    }

    private fun createCoroutineForDB(location: Location) = runBlocking {
        launch (Dispatchers.IO) {
            performDatabaseWork(location)
        }
    }

    private suspend fun performDatabaseWork(location: Location) {
        if (!lawnLocationRepository.hasALocationSaved()) {
            saveGpsLocationIfExists(location)
        }
    }

    private suspend fun saveGpsLocationIfExists(newGpsLocation: Location?) {
        if (newGpsLocation != null) {
            // save location flag
            val preferences = ApplicationPrefs()
            preferences.setHasLocationSavedValue(true)

//            locationGps = newGpsLocation
            Log.d(
                Constants.TAG,
                "GPS: Long: ${locationGps!!.longitude}, Lat: ${locationGps!!.latitude}"
            )

            lawnLocationRepository.addLocation(
                LawnLocation(locationGps!!.latitude, locationGps!!.longitude)
            )
        }
        else {
            Log.d(Constants.TAG, "Here")
        }
    }

    private fun launchMainActivity() {
        val intent = Intent(this@SaveLocationActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onLocationChanged(location: Location) {
        Log.d(Constants.TAG, "Location = $location")
        locationGps = location
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
}