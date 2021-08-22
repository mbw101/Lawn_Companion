package com.mbw101.lawn_companion.ui

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.database.LawnLocation
import com.mbw101.lawn_companion.database.LawnLocationRepository
import com.mbw101.lawn_companion.database.setupLawnLocationRepository
import com.mbw101.lawn_companion.utils.Constants
import com.mbw101.lawn_companion.utils.LocationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-08-15
 */

class SaveLocationActivity : AppCompatActivity() {

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
            createCoroutineForDB(this)
            launchMainActivity()
        }
    }

    private fun createCoroutineForDB(context: Context) = runBlocking {
        launch (Dispatchers.IO) {
            performDatabaseWork(context)
        }
    }

    private suspend fun performDatabaseWork(context: Context) {
        if (!lawnLocationRepository.hasALocationSaved()) {
            val newGpsLocation = LocationUtils.getLastKnownLocation(context)
            saveGpsLocationIfExists(newGpsLocation)
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
}