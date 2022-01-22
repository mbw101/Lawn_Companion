package com.mbw101.lawn_companion.ui

import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mbw101.lawn_companion.BuildConfig
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
    private var locationNetwork: Location? = null
    private lateinit var binding: ActivitySaveLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // request location update now since it takes some time
        LocationUtils.requestLocation(this, this)

        binding = ActivitySaveLocationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

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
            Toast.makeText(this, "Saving lawn location...", Toast.LENGTH_LONG).show()

            runBlocking {
                launch (Dispatchers.IO) {
                    saveNetworkLocationIfExists()
                }
            }

            Timer().schedule(2000) {
                launchMainActivity()
            }
        }
    }

    private fun launchMainActivity() {
        val intent = Intent(this@SaveLocationActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onLocationChanged(location: Location) {
        if (BuildConfig.DEBUG) {
            Log.e(Constants.TAG, "Network Location = $location")
        }
        createCoroutineForDB(location)
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    private fun createCoroutineForDB(location: Location) {
        saveNetworkLocation(location)
        LocationUtils.stopLocationUpdates(this) // stops the usage of network location since we are done with it
    }

    /***
     * Saves into class member, so we can save if they hit yes
     */
    private fun saveNetworkLocation(location: Location) {
        locationNetwork = location
    }

    private suspend fun saveNetworkLocationIfExists() {
        if (locationNetwork != null) {
            lawnLocationRepository.addLocation(LawnLocation(locationNetwork!!.latitude, locationNetwork!!.longitude))
            // save location flag
            val preferences = ApplicationPrefs()
            preferences.setHasLocationSavedValue(true)
            if (BuildConfig.DEBUG) {
                Log.e(
                    Constants.TAG,
                    "Network: Long: ${locationNetwork!!.longitude}, Lat: ${locationNetwork!!.latitude}"
                )
            }
        }
    }
}