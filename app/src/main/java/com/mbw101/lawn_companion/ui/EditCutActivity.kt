package com.mbw101.lawn_companion.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.mbw101.lawn_companion.databinding.ActivityAddCutBinding
import java.util.*

/**
Lawn Companion
Created by Malcolm Wright
Date: 2022-01-14
 */

class EditCutActivity : AppCompatActivity()  {
    // TODO: Pre-fill all the details for the spinner, time text view, and edit text
    // The CutEntry can be passed to this activity from the CutLogFragment through an intent.
    // Since we will already have the CutEntry from the click event.

    private lateinit var monthDropdown: Spinner
    private lateinit var dayDropdown: Spinner
    private lateinit var backIcon: ImageView
    private lateinit var selectedTimeTextView: TextView
    private lateinit var noteEditText: EditText
    private lateinit var addCutButton: Button
    private lateinit var cutTime: Calendar
    private lateinit var binding: ActivityAddCutBinding
    private lateinit var cutEntryViewModel: CutEntryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // TODO: Get CutEntry from intent passed in

        init()
    }

    private fun init() {
        monthDropdown = binding.monthDropdownMenu
        dayDropdown = binding.dayDropdownMenu
        backIcon = binding.backIcon
        selectedTimeTextView = binding.selectedTimeTextView
        addCutButton = binding.addCutButton
        noteEditText = binding.noteEditText

        fillEntryData()
    }

    private fun fillEntryData() {
        TODO("Not yet implemented")
    }
}