package com.mbw101.lawn_companion.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.mbw101.lawn_companion.BuildConfig
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.database.setupLawnLocationRepository
import com.mbw101.lawn_companion.databinding.SettingsActivityBinding
import com.mbw101.lawn_companion.utils.ApplicationPrefs
import com.mbw101.lawn_companion.utils.Constants
import com.mbw101.lawn_companion.utils.UtilFunctions.allowReads
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-15
 */
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: SettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }

        init()
    }

    private fun init() {
        // theme the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // allows back button
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(MyApplication.applicationContext(), R.color.background_black))) // background black colour
        supportActionBar?.title = getString(R.string.settingsTitle)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        launchMainActivity()
        return true
    }

    private fun launchMainActivity() {
        val intent = Intent(MyApplication.applicationContext(), MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // allow back button to go back to main activity
    override fun onBackPressed() {
        launchMainActivity()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            if (BuildConfig.DEBUG) {
                // setPreferencesFromResource results in StrictMode policy violation
                allowReads {
                    addPreferencesFromResource(R.xml.root_preferences)

                    // add debug preference for clearing the intro completed flag
                    val preferenceScreen = this.preferenceScreen

                    val category = PreferenceCategory(preferenceScreen.context)
                    category.title = "Debug Options"
                    preferenceScreen.addPreference(category)

                    val clearIntroFlagPreference = Preference(preferenceScreen.context)
                    clearIntroFlagPreference.key = "clearIntroFlag"
                    clearIntroFlagPreference.title = "Clear Intro Flag (will show intro when the app opened up again)"
                    clearIntroFlagPreference.summary = "Clears the SharedPreferences flag"
                    preferenceScreen.addPreference(clearIntroFlagPreference)

                    // add debug preference for removing location from DB
                    val removeLocationPreference = Preference(preferenceScreen.context)
                    removeLocationPreference.key = "clearLocationDB"
                    removeLocationPreference.title = "Clear Lawn Location DB"
                    removeLocationPreference.summary = "Removes all entries from the lawn location table"
                    preferenceScreen.addPreference(removeLocationPreference)
                }
            }
            else {
                setPreferencesFromResource(R.xml.root_preferences, rootKey)
            }
        }

        override fun onPreferenceTreeClick(preference: Preference?): Boolean {
            val preferenceTitle: String = preference?.title as String
            Log.d(Constants.TAG, "Preference title = $preferenceTitle")
            if (preferenceTitle.contains("lawn location")) {
                if (ActivityCompat.checkSelfPermission(MyApplication.applicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    openSaveLocationActivity()
                }
                else {
                    Toast.makeText(MyApplication.applicationContext(), getString(R.string.toastNoLocationPermission), Toast.LENGTH_SHORT)
                        .show()
                }
            }
            else if (preferenceTitle.contains("Cutting Season Dates")) {
                openSetDatesActivity()
            }
            else if (preferenceTitle.contains("Clear Intro Flag")) { // debug preference
                // clear the intro flag, so it can be shown again once the app is opened
                Toast.makeText(context, "Clearing intro flag", Toast.LENGTH_SHORT).show()
                val applicationPrefs = ApplicationPrefs()
                applicationPrefs.setNotFirstTime(false)
            }
            else if (preferenceTitle.contains("Clear Lawn Location DB")) {
                Toast.makeText(context, "Clearing location DB", Toast.LENGTH_SHORT).show()
                val lawnLocationRepository = setupLawnLocationRepository(MyApplication.applicationContext())
                runBlocking {
                    launch(Dispatchers.IO) {
                        lawnLocationRepository.deleteAllLocations()
                    }
                }

                // we also need to set location flag to false
                val applicationPrefs = ApplicationPrefs()
                applicationPrefs.setHasLocationSavedValue(false)
            }

            return super.onPreferenceTreeClick(preference)
        }

        private fun openSaveLocationActivity() {
            val intent = Intent (activity, SaveLocationActivity::class.java)
            activity?.startActivity(intent)
        }

        private fun openSetDatesActivity() {
            val intent = Intent (activity, SetDatesActivity::class.java)
            activity?.startActivity(intent)
        }
    }
}