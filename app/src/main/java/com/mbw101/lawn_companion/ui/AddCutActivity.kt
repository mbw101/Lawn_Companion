package com.mbw101.lawn_companion.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.mbw101.lawn_companion.R

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-18
 */

class AddCutActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var monthDropdown: Spinner
    private lateinit var backIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cut)

        init()
        setListeners()
    }

    private fun init() {
        // set up dropdown menu with months
        monthDropdown = findViewById(R.id.monthDropdownMenu)
        backIcon = findViewById(R.id.backIcon)
    }

    private fun setListeners() {
        // set listener for back button
        backIcon.setOnClickListener {
            launchMainActivity()
        }
    }

    private fun launchMainActivity() {
        val intent = Intent(MyApplication.applicationContext(), MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}