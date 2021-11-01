package com.mbw101.lawn_companion.ui

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.databinding.SettingsActivityBinding
import com.mbw101.lawn_companion.utils.Constants

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
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onPreferenceTreeClick(preference: Preference?): Boolean {
            val preferenceTitle: String = preference?.title as String
            Log.d(Constants.TAG, "Preference title = $preferenceTitle")
            if (preferenceTitle.contains("lawn location")) { // getString(R.string.
                openSaveLocationActivity()
            }
            else if (preferenceTitle.contains("Cutting Season Dates")) {
                openSetDatesActivity()
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