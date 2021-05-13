package com.mbw101.lawn_companion.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.databinding.ActivityFullscreenBinding
import com.mbw101.lawn_companion.utils.ApplicationPrefs

/**
Lawn Companion
Created by Malcolm Wright
Date: May 13th, 2021
*/

class FullscreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreenBinding
    private lateinit var introTitleTextView: TextView
    private lateinit var mainIntroTextView: TextView
    private lateinit var nextButton: Button
    private lateinit var getStartedButton: Button
    private var introViewPagerAdapter: IntroViewPagerAdapter? = null
    private var viewPager: ViewPager? = null

    private lateinit var preferenceManager: ApplicationPrefs

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // start the introduction
        init()
        setupListeners()
    }

    /***
     * This function will check to see if
     * the Intro has been completed by using
     * NotificationPrefs
     */
    private fun init() {
        preferenceManager = ApplicationPrefs()
        if (preferenceManager.isNotFirstTime()) { // we want to automatically go into Main
            launchMainActivity()
        }

        // load all the components
        nextButton = findViewById<Button>(R.id.nextButton)
        getStartedButton = findViewById<Button>(R.id.getStartedButton)
        introTitleTextView = findViewById<TextView>(R.id.introTitleTextView)
        mainIntroTextView = findViewById<TextView>(R.id.mainIntroTextView)
    }

    private fun setupListeners() {
        nextButton.setOnClickListener {
            // TODO: Navigate to the next screen
        }

        getStartedButton.setOnClickListener {
            // ask location permission

            launchMainActivity()
        }
    }

    /***
     * Will be called when the user hits "Get Started" on the
     * last intro screen
     */
    private fun askLocationPermission() {
        TODO("Complete")
    }

    /***
     * Creates an intent to open main activity
     */
    private fun launchMainActivity() {
        preferenceManager.setNotFirstTime(true) // finished the intro of App, so we can save that
        // TODO: Fire an intent to launch MainActivity
//        val intent = Intent(this@IntroActivity, MainActivity::class.java)
//        startActivity(intent)
        finish()
    }
}