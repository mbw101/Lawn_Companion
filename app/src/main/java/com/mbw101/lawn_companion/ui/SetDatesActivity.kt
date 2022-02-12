package com.mbw101.lawn_companion.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.database.CuttingSeasonDateRepository
import com.mbw101.lawn_companion.database.setupCuttingSeasonDateRepository
import com.mbw101.lawn_companion.databinding.ActivitySetDatesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.concurrent.schedule


/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-09-24
 */
class SetDatesActivity : AppCompatActivity() {
    private lateinit var saveDatesButton: Button
    private lateinit var startDateSelector: TextView
    private lateinit var endDateSelector: TextView
    private lateinit var cuttingSeasonDateRepository: CuttingSeasonDateRepository
    private lateinit var binding: ActivitySetDatesBinding
    private var selectedStartDate: Calendar? = null
    private var selectedEndDate: Calendar? = null
    private var startDateFromDB: Calendar? = null
    private var endDateFromDB: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetDatesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
        setupDB()
        setupExistingDates()
        setListeners()
    }


    private fun setupExistingDates() {
        // Access the start and end dates from the DB
        val toast = Toast.makeText(this, "Accessing dates...", Toast.LENGTH_SHORT)
        toast.show()

        startDateFromDB = null
        endDateFromDB = null
        runBlocking {
            launch (Dispatchers.IO) {
                startDateFromDB = cuttingSeasonDateRepository.getStartDate()?.calendarValue
                endDateFromDB = cuttingSeasonDateRepository.getEndDate()?.calendarValue
                println("startDate $startDateFromDB")
                println("endDate $startDateFromDB")

                // timer runs on a different thread than UI, so we need this UI updating code to run on the UI thread
                runOnUiThread {
                    toast.cancel()

                    if (startDateFromDB != null && endDateFromDB != null) {
                        startDateSelector.text =
                            getString(
                                R.string.datePlaceholder,
                                startDateFromDB!!.get(Calendar.YEAR).toString(),
                                (startDateFromDB!!.get(Calendar.MONTH) + 1).toString(),
                                startDateFromDB!!.get(Calendar.DAY_OF_MONTH).toString(),

                            )

                        endDateSelector.text =
                            getString(
                                R.string.datePlaceholder,
                                endDateFromDB!!.get(Calendar.YEAR).toString(),
                                (endDateFromDB!!.get(Calendar.MONTH) + 1).toString(),
                                endDateFromDB!!.get(Calendar.DAY_OF_MONTH).toString()
                            )
                    }

                    startDateSelector.invalidate()
                    endDateSelector.invalidate()
                }
            }
        }
    }

    private fun init() {
        saveDatesButton = binding.saveDatesButton
        startDateSelector = binding.startDateSelector
        endDateSelector = binding.endDateSelector

        // set up back button icon
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // allows back button
        toolbar.setNavigationOnClickListener {
            launchSettingsActivity()
        }
        supportActionBar?.title = getString(R.string.setCuttingSeasonDatesTitle)
    }

    private fun setListeners() {
        startDateSelector.setOnClickListener {
            val cal = getAppropriateStartCalendar() ?: Calendar.getInstance()
            val day: Int = cal!!.get(Calendar.DAY_OF_MONTH)
            val month: Int = cal.get(Calendar.MONTH)
            val year: Int = cal.get(Calendar.YEAR)

            showDatePickerDialog(year, month, day, startDateSelector, true)
        }

        endDateSelector.setOnClickListener {
            val cal = getAppropriateEndCalendar() ?: Calendar.getInstance()
            val day: Int = cal.get(Calendar.DAY_OF_MONTH)
            val month: Int = cal.get(Calendar.MONTH)
            val year: Int = cal.get(Calendar.YEAR)

            // date picker dialog
            showDatePickerDialog(year, month, day, endDateSelector, false)
        }

        saveDatesButton.setOnClickListener {
            // save start date into DB
            runBlocking {
                launch (Dispatchers.IO) {
                    if (selectedStartDate != null) {
                        cuttingSeasonDateRepository.insertStartDate(selectedStartDate!!)
                    }
                    if (selectedEndDate != null) {
                        cuttingSeasonDateRepository.insertEndDate(selectedEndDate!!)
                    }
                }
            }

            Toast.makeText(this, "Saving dates...", Toast.LENGTH_LONG).show()
            Timer().schedule(1000) {
                runOnUiThread {
                    launchSettingsActivity()
                }
            }
        }
    }

    private fun showDatePickerDialog(year: Int, month: Int, day: Int, textView: TextView, isStartDate: Boolean) {
        val picker = DatePickerDialog(
            this,
            { _, yearPicked, monthOfYear, dayOfMonth ->
                textView.text =
                    getString(
                        R.string.datePlaceholder,
                        yearPicked.toString(),
                        (monthOfYear + 1).toString(),
                        dayOfMonth.toString()
                    )
                if (isStartDate) {
                    selectedStartDate = Calendar.getInstance()
                    selectedStartDate!!.set(Calendar.YEAR, yearPicked)
                    selectedStartDate!!.set(Calendar.MONTH, monthOfYear)
                    selectedStartDate!!.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                else {
                    selectedEndDate = Calendar.getInstance()
                    selectedEndDate!!.set(Calendar.YEAR, yearPicked)
                    selectedEndDate!!.set(Calendar.MONTH, monthOfYear)
                    selectedEndDate!!.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
            },
            year,
            month,
            day
        )
        // set min and max dates and first and last days of the current year
        // doesn't let user enter date outside of current year for cutting season
        val minDate = Calendar.getInstance()
        minDate.set(Calendar.MONTH, Calendar.JANUARY)
        minDate.set(Calendar.DAY_OF_MONTH, 1)
        val maxDate = Calendar.getInstance()
        maxDate.set(Calendar.MONTH, Calendar.DECEMBER)
        maxDate.set(Calendar.DAY_OF_MONTH, 31)

        picker.datePicker.minDate = minDate.timeInMillis
        picker.datePicker.maxDate = maxDate.timeInMillis
        picker.show()
    }

    // we want date on the DatePicker to be consistent with what's on the text view
    private fun getAppropriateStartCalendar(): Calendar? {
        return if (selectedStartDate == null) startDateFromDB else selectedStartDate
    }
    private fun getAppropriateEndCalendar(): Calendar? {
        return if (selectedEndDate == null) endDateFromDB else selectedEndDate
    }

    private fun setupDB() {
        cuttingSeasonDateRepository = setupCuttingSeasonDateRepository(this)
    }

    override fun onBackPressed() {
        launchSettingsActivity()
    }

    private fun launchSettingsActivity() {
        val intent = Intent(MyApplication.applicationContext(), SettingsActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun getStartDate(): Calendar? = startDateFromDB
    fun getEndDate(): Calendar? = endDateFromDB
}