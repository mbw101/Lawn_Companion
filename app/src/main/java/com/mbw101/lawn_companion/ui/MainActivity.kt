package com.mbw101.lawn_companion.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mbw101.lawn_companion.R


/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-14
 */

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var settingsIcon: ImageView
    private lateinit var refreshIcon: ImageView
    private lateinit var titleTextView: TextView

    companion object {
        lateinit var addCutFAB: FloatingActionButton
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
        setListeners()
    }

    /***
     * Launches the HomeFragment and
     * gets anything else ready to go
     */
    private fun init() {
        // initialize components
        bottomNav = findViewById(R.id.bottomNav)
        settingsIcon = findViewById(R.id.settingsIcon)
        refreshIcon = findViewById(R.id.refreshIcon)
        titleTextView = findViewById(R.id.titleTextView)
        addCutFAB = findViewById(R.id.addCutFAB)

        setListeners()
    }

    private fun setListeners() {
        // bottom navigation listener
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    // navigate to the home fragment
                    findNavController(R.id.nav_host_fragment).navigate(R.id.nav_home)
                    titleTextView.text = getString(R.string.home)

                    // update the label if necessary (refer to conditions)
                    true
                }
                R.id.cutLog -> {
                    // navigate to the cut log fragment
                    findNavController(R.id.nav_host_fragment).navigate(R.id.nav_cutlog)
                    titleTextView.text = getString(R.string.cutLog)

                    // clear all entries and update them again
                    true
                }
                else -> false
            }
        }

        // toolbar button listeners
        settingsIcon.setOnClickListener {
            // TODO: Launch the settings activity
            Toast.makeText(this, "Settings was clicked!", Toast.LENGTH_SHORT).show()
        }

        refreshIcon.setOnClickListener {
            Toast.makeText(this, "Refresh was clicked!", Toast.LENGTH_SHORT).show()
            refresh()
        }

        // FAB listener
        addCutFAB.setOnClickListener {
            // TODO: Display the Add Cut activity here
            Toast.makeText(this, "FAB clicked!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun refresh() {
        // TODO: Refresh both the main screen and the cut log
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // TODO: Change text on main screen if allowed/disabled
    }
}