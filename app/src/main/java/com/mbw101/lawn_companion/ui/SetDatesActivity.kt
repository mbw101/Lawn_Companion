package com.mbw101.lawn_companion.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
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
    private lateinit var backIcon: ImageView
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
        Toast.makeText(this, "Accessing dates...", Toast.LENGTH_SHORT).show()

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
                    if (startDateFromDB != null && endDateFromDB != null) {
                        startDateSelector.text =
                            getString(
                                R.string.datePlaceholder,
                                startDateFromDB!!.get(Calendar.DAY_OF_MONTH).toString(),
                                (startDateFromDB!!.get(Calendar.MONTH) + 1).toString(),
                                startDateFromDB!!.get(Calendar.YEAR).toString()
                            )

                        endDateSelector.text =
                            getString(
                                R.string.datePlaceholder,
                                endDateFromDB!!.get(Calendar.DAY_OF_MONTH).toString(),
                                (endDateFromDB!!.get(Calendar.MONTH) + 1).toString(),
                                endDateFromDB!!.get(Calendar.YEAR).toString()
                            )
                    }

                    startDateSelector.invalidate()
                    endDateSelector.invalidate()
                }
            }
        }
    }

    private fun init() {
        backIcon = binding.backIcon
        saveDatesButton = binding.saveDatesButton
        startDateSelector = binding.startDateSelector
        endDateSelector = binding.endDateSelector
    }

    private fun setListeners() {
        backIcon.setOnClickListener {
            launchSettingsActivity()
        }

        startDateSelector.setOnClickListener {
            val cal = Calendar.getInstance()
            val day: Int = cal.get(Calendar.DAY_OF_MONTH)
            val month: Int = cal.get(Calendar.MONTH)
            val year: Int = cal.get(Calendar.YEAR)
            // date picker dialog
            val picker = DatePickerDialog(this,
                { _, yearPicked, monthOfYear, dayOfMonth ->
                    startDateSelector.text =
                        getString(R.string.datePlaceholder, dayOfMonth.toString(), (monthOfYear + 1).toString(), yearPicked.toString())
                    selectedStartDate = Calendar.getInstance()
                    selectedStartDate!!.set(Calendar.YEAR, yearPicked)
                    selectedStartDate!!.set(Calendar.MONTH, monthOfYear)
                    selectedStartDate!!.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                },
                year,
                month,
                day
            )
            picker.show()
        }

        endDateSelector.setOnClickListener {
            val cal = Calendar.getInstance()
            val day: Int = cal.get(Calendar.DAY_OF_MONTH)
            val month: Int = cal.get(Calendar.MONTH)
            val year: Int = cal.get(Calendar.YEAR)
            // date picker dialog
            val picker = DatePickerDialog(this,
                { _, yearPicked, monthOfYear, dayOfMonth ->
                    endDateSelector.text =
                        getString(R.string.datePlaceholder, dayOfMonth.toString(), (monthOfYear + 1).toString(), yearPicked.toString())
                    selectedEndDate = Calendar.getInstance()
                    selectedEndDate!!.set(Calendar.YEAR, yearPicked)
                    selectedEndDate!!.set(Calendar.MONTH, monthOfYear)
                    selectedEndDate!!.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                },
                year,
                month,
                day
            )
            picker.show()
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