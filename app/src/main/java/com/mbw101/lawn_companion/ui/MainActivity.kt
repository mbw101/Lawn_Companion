package com.mbw101.lawn_companion.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mbw101.lawn_companion.R

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-14
 */

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // TODO: Change text on main screen if allowed/disabled
    }
}