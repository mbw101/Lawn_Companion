package com.mbw101.lawn_companion.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mbw101.lawn_companion.R

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-14
 */

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var cutLogRecyclerView: RecyclerView

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
//        navController = findNavController(R.id.nav_host_fragment)

        setListeners()
    }

    private fun setListeners() {
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    // navigate to the home fragment
                    findNavController(R.id.nav_host_fragment).navigate(R.id.nav_home)

                    // update the label if necessary (refer to conditions)
                    true
                }
                R.id.cutLog -> {
                    // navigate to the cut log fragment
                    findNavController(R.id.nav_host_fragment).navigate(R.id.nav_cutlog)

                    // clear all entries and update them again
                    true
                }
                else -> false
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // TODO: Change text on main screen if allowed/disabled
    }
}