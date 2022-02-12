package com.mbw101.lawn_companion.ui

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.database.CutEntry
import com.mbw101.lawn_companion.databinding.ActivityAddCutBinding
import com.mbw101.lawn_companion.utils.Constants
import com.mbw101.lawn_companion.utils.UtilFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*


/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-18
 */

class AddCutActivity : AppCompatActivity() {

    private lateinit var monthDropdown: Spinner
    private lateinit var dayDropdown: Spinner
    private lateinit var selectedTimeTextView: TextView
    private lateinit var noteEditText: EditText
    private lateinit var addCutButton: Button
    private lateinit var cutTime: Calendar
    private lateinit var binding: ActivityAddCutBinding
    private lateinit var cutEntryViewModel: CutEntryViewModel

    companion object {
        // ensures the date of the cut is not past the current date
        fun checkDateValidity(desiredDate: Calendar): Boolean {
            val currentDate = Calendar.getInstance()
            return currentDate.after(desiredDate) // check to see if the desired comes before the current date
        }

        fun addDaysToDropdown(context: Context, monthValue: Int): ArrayAdapter<String> {
            val days = ArrayList<String>(31)
            for (i in 1..31) {
                days.add(i.toString())
            }
            Log.d(Constants.TAG, days.toString())

            return getCorrectDayAdapter(context, monthValue, days)
        }

        private fun getCorrectDayAdapter(context: Context, monthValue: Int, days: ArrayList<String>): ArrayAdapter<String> {
            val cal = Calendar.getInstance()
            val isLeapYear: Boolean = UtilFunctions.isLeapYear(cal.get(Calendar.YEAR))
            val dayAdaptor: ArrayAdapter<String>

            when (monthValue) {

                Calendar.FEBRUARY -> {
                    dayAdaptor = if (!isLeapYear) { // 28 days (non-leap year)
                        ArrayAdapter(context, android.R.layout.simple_spinner_item, days.subList(0, 28))
                    } else { // leap year, so 29 days
                        ArrayAdapter(context, android.R.layout.simple_spinner_item, days.subList(0, 29))
                    }
                }

                // 30 days (april, june, september, november)
                Calendar.APRIL, Calendar.JUNE, Calendar.SEPTEMBER, Calendar.NOVEMBER -> {
                    dayAdaptor =
                        ArrayAdapter(context, android.R.layout.simple_spinner_item, days.subList(0, 30))
                }

                // 31 days (January, march, may, july, august, october, december)
                Calendar.JANUARY, Calendar.MARCH, Calendar.MAY, Calendar.JULY, Calendar.AUGUST, Calendar.OCTOBER, Calendar.DECEMBER -> {
                    dayAdaptor =
                        ArrayAdapter(context, android.R.layout.simple_spinner_item, days.subList(0, 31))
                }

                else -> {
                    dayAdaptor =
                        ArrayAdapter(context, android.R.layout.simple_spinner_item, days.subList(0, 31))
                }
            }

            // fill in the day values based on current month
            dayAdaptor.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
            return dayAdaptor
        }

        /***
         * Calls checkDateValidity with a created calendar
         * object from the dropdown menus
         */
        fun isValidDate(dayDropdown: Spinner, monthDropdown: Spinner): Boolean {
            val desiredDate = Calendar.getInstance()
            desiredDate.set(Calendar.DAY_OF_MONTH, dayDropdown.selectedItemPosition+1)
            desiredDate.set(Calendar.MONTH, monthDropdown.selectedItemPosition) // months are zero based
            // set to 00:00 or 12:00 start of the day to avoid any conflicts with validity checking
            desiredDate.set(Calendar.HOUR_OF_DAY, 0)
            desiredDate.set(Calendar.MINUTE, 0)
            Log.d(Constants.TAG, desiredDate.toString())

            return checkDateValidity(desiredDate)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
        setListeners()
    }

    private fun init() {
        monthDropdown = binding.monthDropdownMenu
        dayDropdown = binding.dayDropdownMenu
        selectedTimeTextView = binding.selectedTimeTextView
        addCutButton = binding.addCutButton
        noteEditText = binding.noteEditText

        fillDropdownMenus()
        setMonthSelection()
        updateInputs()

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            launchMainActivity()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.addCutTitle)
    }

    /**
     * Adds the month values into
     * the month dropdown
     */
    private fun fillDropdownMenus() {
        val monthAdapter: ArrayAdapter<String> = ArrayAdapter(
            this, android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.months)
        )
        monthAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        monthDropdown.adapter = monthAdapter
    }

    /**
     * Sets the current month in the dropdown menu
     */
    private fun setMonthSelection() {
        val cal: Calendar = Calendar.getInstance()
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
        dayDropdown.adapter = addDaysToDropdown(this, monthValue)
        // set the default day value selected in the dropdown
        setDayDropdownSelection(cal)
    }

    private fun setDayDropdownSelection(cal: Calendar) {
        val maxNumDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        Log.d(Constants.TAG, "Max number of days = $maxNumDays")
        Log.d(Constants.TAG, "Current month = ${cal.get(Calendar.MONTH)}")

        val previousDay = cal.get(Calendar.DAY_OF_MONTH) - 1

        if (maxNumDays < previousDay) {
            dayDropdown.setSelection(0)
        } else {
            dayDropdown.setSelection(previousDay) // we need to reset the default selection each time
        }
    }

    private fun setListeners() {
        selectedTimeTextView.setOnClickListener {
            openClockDialog()
        }

        addCutButton.setOnClickListener {
            // Check to make sure the date isn't past current date
            if (isValidDate(dayDropdown, monthDropdown)) {
                // Add cut to DB
                cutEntryViewModel = CutEntryViewModel(application)
                addCut()
            }
            else {
                Toast.makeText(this, getString(R.string.futureCutsToastMessage), Toast.LENGTH_LONG).
                        show()
            }
        }

        monthDropdown.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (view == null) {
                    return
                }
                val cal: Calendar = Calendar.getInstance()
                cal.set(Calendar.MONTH, position) // set the month to what they picked
                Log.d(Constants.TAG, "Month selected = $position")

                // update the days dropdown menu
                setupDaysDropdown(position, cal)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun addCut() {
        // build cutEntry object and add to database
        // access the correct time from the selected time text view
        val cutTimeString = DateUtils.formatDateTime(this, cutTime.timeInMillis, DateUtils.FORMAT_SHOW_TIME)
        Log.d(Constants.TAG, "Time of cut: $cutTimeString")
        val note: String? = if (noteEditText.text.isNotEmpty() && noteEditText.text.isNotBlank()) noteEditText.text.toString() else null
        val cutEntry = CutEntry(
            cutTimeString,
            dayDropdown.selectedItemPosition+1,
            Constants.months[monthDropdown.selectedItemPosition],
            monthDropdown.selectedItemPosition+1, UtilFunctions.getCurrentYear(),
            note
        )

        runBlocking {
            launch (Dispatchers.IO) {
                val hasEntry = cutEntryViewModel.hasCutEntry(cutEntry)

                runOnUiThread {
                    if (hasEntry) {
                        // show toast
                        Toast.makeText(this@AddCutActivity, getString(R.string.moreThanOneCutToastMsg), Toast.LENGTH_SHORT)
                            .show()
                    }
                    else { // go ahead with adding
                        cutEntryViewModel.addEntry(cutEntry)
                        launchMainActivity()
                    }
                }
            }
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

        dayDropdown.setSelection(0)
    }

    override fun onBackPressed() {
        launchMainActivity()
    }
}
