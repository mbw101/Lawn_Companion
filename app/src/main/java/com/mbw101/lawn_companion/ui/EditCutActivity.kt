package com.mbw101.lawn_companion.ui

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.database.CutEntry
import com.mbw101.lawn_companion.databinding.ActivityEditCutBinding
import com.mbw101.lawn_companion.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

/**
Lawn Companion
Created by Malcolm Wright
Date: 2022-01-14
 */

class EditCutActivity : AppCompatActivity()  {

    private lateinit var monthDropdown: Spinner
    private lateinit var dayDropdown: Spinner
    private lateinit var backIcon: ImageView
    private lateinit var selectedTimeTextView: TextView
    private lateinit var noteEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cutTime: Calendar
    private lateinit var binding: ActivityEditCutBinding
    private lateinit var cutEntryViewModel: CutEntryViewModel
    private lateinit var cutEntry: CutEntry // entry that's being edited

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Get CutEntry from intent passed in
        cutEntry = intent.getParcelableExtra(getString(R.string.cutEntryIntentKey))!!
        init()
        fillDropdownMenus(cutEntry)
        fillEntryData(cutEntry)
    }

    private fun init() {
        monthDropdown = binding.monthDropdownMenu
        dayDropdown = binding.dayDropdownMenu
        backIcon = binding.backIcon
        selectedTimeTextView = binding.selectedTimeTextView
        saveButton = binding.saveEditButton
        noteEditText = binding.noteEditText
        setListeners()
        cutTime = Calendar.getInstance()
    }

    private fun setListeners() {
        backIcon.setOnClickListener {
            launchMainActivity()
        }

        selectedTimeTextView.setOnClickListener {
            openClockDialog()
        }

        saveButton.setOnClickListener {
            // Check to make sure the date isn't past current date
            if (AddCutActivity.isValidDate(dayDropdown, monthDropdown)) {
                // Add cut to DB
                cutEntryViewModel = CutEntryViewModel(application)
                editCut()
            }
            else {
                Toast.makeText(this, getString(R.string.futureCutsToastMessage), Toast.LENGTH_LONG).
                show()
            }
        }
    }

    private fun editCut() {
        cutEntry.cut_time = DateUtils.formatDateTime(this, cutTime.timeInMillis, DateUtils.FORMAT_SHOW_TIME)
        cutEntry.day_number = dayDropdown.selectedItem.toString().toInt()
        cutEntry.month_name = Constants.months[monthDropdown.selectedItemPosition]
        cutEntry.month_number = monthDropdown.selectedItemPosition + 1
        cutEntry.note = if (noteEditText.text.toString().isNotEmpty() && noteEditText.text.isNotBlank()) noteEditText.text.toString() else null

        runBlocking {
            launch (Dispatchers.IO) {
                val hasEntry = cutEntryViewModel.hasCutEntry(cutEntry)

                runOnUiThread {
                    if (hasEntry) {
                        // show toast
                        Toast.makeText(this@EditCutActivity, getString(R.string.moreThanOneCutToastMsg), Toast.LENGTH_SHORT)
                            .show()
                    }
                    else { // go ahead with update of entry
                        cutEntryViewModel.updateEntries(cutEntry)
                        launchMainActivity()
                    }
                }
            }
        }
    }

    private fun openClockDialog() {
        // Launch Time Picker Dialog
        val timePickerDialog = TimePickerDialog(this,
            { _, hourOfDay, selectedMin ->
                // set up calendar with the same time in order to get millis
                val cal: Calendar = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, selectedMin)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)

                // save that time for later use
                cutTime = cal

                // set the text view to have the time they picked
                selectedTimeTextView.text = DateUtils.formatDateTime(this, cal.timeInMillis, DateUtils.FORMAT_SHOW_TIME)
            },
            cutTime.get(Calendar.HOUR_OF_DAY),
            cutTime.get(Calendar.MINUTE),
            DateFormat.is24HourFormat(this) // Shows a 24 hour time picker dialog based on the system settings for 24 hour format
        )
        timePickerDialog.show()
    }

    private fun launchMainActivity() {
        val intent = Intent(MyApplication.applicationContext(), MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun fillDropdownMenus(entry: CutEntry) {
        // month dropdown
        val monthAdapter: ArrayAdapter<String> = ArrayAdapter(
            this, android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.months)
        )
        monthAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        monthDropdown.adapter = monthAdapter

        // day dropdown
        dayDropdown.adapter = AddCutActivity.addDaysToDropdown(this, entry.month_number - 1)
    }

    private fun fillEntryData(entry: CutEntry) {
        monthDropdown.setSelection(entry.month_number - 1) // Starts at 0
        dayDropdown.setSelection(entry.day_number - 1)
        if (entry.note != null) {
            noteEditText.setText(entry.note)
        }
        selectedTimeTextView.text = entry.cut_time
    }
}