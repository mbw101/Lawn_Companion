package com.mbw101.lawn_companion.ui

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.utils.Constants
import com.mbw101.lawn_companion.utils.UtilFunctions
import java.util.*


/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-18
 */

class AddCutActivity : AppCompatActivity() {

    private lateinit var monthDropdown: Spinner
    private lateinit var dayDropdown: Spinner
    private lateinit var backIcon: ImageView
    private lateinit var selectedTimeTextView: TextView
    private lateinit var addCutButton: Button
    private lateinit var cutTime: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cut)

        init()
        setListeners()
    }

    private fun init() {
        // set up dropdown menu with months
        monthDropdown = findViewById(R.id.monthDropdownMenu)
        dayDropdown = findViewById(R.id.dayDropdownMenu)
        backIcon = findViewById(R.id.backIcon)
        selectedTimeTextView = findViewById(R.id.selectedTimeTextView)
        addCutButton = findViewById(R.id.addCutButton)

        // Fill in the dropdown menus
        val cal: Calendar = Calendar.getInstance()
        val monthAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.months))
        monthAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        monthDropdown.adapter = monthAdapter
        val month: Int = cal.get(Calendar.MONTH)
        monthDropdown.setSelection(month)

        val curMonth = cal.get(Calendar.MONTH)
        Log.d(Constants.TAG, "Current month = $curMonth")
        cutTime = Calendar.getInstance()

        updateInputs()
    }

    /***
     * Sets up the correct day values in dropdown menu
     * based on the month selected and if it's a leap year
     */
    private fun setupDaysDropdown(monthValue: Int, cal: Calendar) {
        // set up the days dropdown values
        val isLeapYear: Boolean = UtilFunctions.isLeapYear(cal.get(Calendar.YEAR))
        val days = ArrayList<String>(31)
        for (i in 1..31) {
            days.add(i.toString())
        }
        Log.d(Constants.TAG, days.toString())

        val dayAdaptor: ArrayAdapter<String>
        // go through all the months
        when (monthValue) {

            Calendar.FEBRUARY -> {
                dayAdaptor = if (!isLeapYear) { // 28 days (non-leap year)
                    ArrayAdapter(this, android.R.layout.simple_spinner_item, days.subList(0, 28))
                } else { // leap year, so 29 days
                    ArrayAdapter(this, android.R.layout.simple_spinner_item, days.subList(0, 29))
                }
            }

            // 30 days (april, june, september, november)
            Calendar.APRIL, Calendar.JUNE, Calendar.SEPTEMBER, Calendar.NOVEMBER -> {
                dayAdaptor = ArrayAdapter(this, android.R.layout.simple_spinner_item, days.subList(0, 30))
            }

            // 31 days (January, march, may, july, august, october, december)
            Calendar.JANUARY, Calendar.MARCH, Calendar.MAY, Calendar.JULY, Calendar.AUGUST, Calendar.OCTOBER, Calendar.DECEMBER -> {
                dayAdaptor = ArrayAdapter(this, android.R.layout.simple_spinner_item, days.subList(0, 31))
            }

            else -> {
                dayAdaptor = ArrayAdapter(this, android.R.layout.simple_spinner_item, days.subList(0, 31))
            }
        }

        // fill in the day values based on current month
        dayAdaptor.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        dayDropdown.adapter = dayAdaptor
        dayDropdown.setSelection(cal.get(Calendar.DAY_OF_MONTH)-1) // we need to reset the default selection each time
    }

    private fun setListeners() {
        // set listener for back button
        backIcon.setOnClickListener {
            launchMainActivity()
        }

        selectedTimeTextView.setOnClickListener {
            openClockDialog()
        }

        addCutButton.setOnClickListener {
            // TODO: Add cut via DAO

            launchMainActivity()
        }

        monthDropdown.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val cal: Calendar = Calendar.getInstance()
                Log.d(Constants.TAG, "Month selected = $position")

                // update the days dropdown menu
                setupDaysDropdown(position, cal)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun launchMainActivity() {
        val intent = Intent(MyApplication.applicationContext(), MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    /***
     * Opens the dialog for choosing a time.
     * By default, the clock will be set to the current
     * time. Same with the text view.
     */
    private fun openClockDialog() {
        // Launch Time Picker Dialog
        // selectedTimeTextView.text =  DateUtils.formatDateTime(this,"HH:mm")
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
                selectedTimeTextView.text =  DateUtils.formatDateTime(this,cal.timeInMillis, DateUtils.FORMAT_SHOW_TIME)
            },
            cutTime.get(Calendar.HOUR_OF_DAY),
            cutTime.get(Calendar.MINUTE),
            false // TODO: Add if statement to support 24-hour locales
        )
        timePickerDialog.show()
    }

    /***
     * Will set the selected values and time
     * to match what the current time and date is
     */
    private fun updateInputs() {
        // set selected time to current time
        val cal: Calendar = Calendar.getInstance()
        selectedTimeTextView.text = DateUtils.formatDateTime(this, cal.timeInMillis, DateUtils.FORMAT_SHOW_TIME)

        // set the current month and day values in dropdown as selected
        val month: Int = cal.get(Calendar.MONTH)
        setupDaysDropdown(month, cal) // sets up the correct amount of days based on month value
        Log.d(Constants.TAG, "Month = $month")

        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
        dayDropdown.setSelection(day-1)
    }
}