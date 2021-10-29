package com.mbw101.lawn_companion.ui

import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.database.LawnLocation
import com.mbw101.lawn_companion.database.LawnLocationRepository
import com.mbw101.lawn_companion.database.setupLawnLocationRepository
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_location)

        init()
    }

    private fun init() {
        createButtons()
        setupRepo()
        setupListeners()
    }

    private fun createButtons() {
        denySaveButton = findViewById(R.id.denySaveLocationButton)
        acceptSaveButton = findViewById(R.id.acceptSaveLocationButton)
    }

    private fun setupRepo() {
        lawnLocationRepository = setupLawnLocationRepository(this)
    }

    private fun setupListeners() {
        denySaveButton.setOnClickListener {
            launchMainActivity()
        }

        acceptSaveButton.setOnClickListener {
            LocationUtils.requestLocation(this, this)
            Toast.makeText(this, "Saving lawn location...", Toast.LENGTH_LONG).show()

            // save location flag
            val preferences = ApplicationPrefs()
            preferences.setHasLocationSavedValue(true)
            val locationListener = this

            Timer().schedule(1500) {
                LocationUtils.stopLocationUpdates(locationListener) // stops the usage of gps when we are done with it
                launchMainActivity()
            }
        }
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
            locationGps = newGpsLocation
            Log.d(
                Constants.TAG,
                "GPS: Long: ${locationGps!!.longitude}, Lat: ${locationGps!!.latitude}"
            )

            lawnLocationRepository.addLocation(
                LawnLocation(locationGps!!.latitude, locationGps!!.longitude)
            )
        }
    }

    private fun launchMainActivity() {
        val intent = Intent(this@SaveLocationActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onLocationChanged(location: Location) {
        Log.d(Constants.TAG, "Location = $location")
        createCoroutineForDB(location)
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
}