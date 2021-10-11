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
import com.mbw101.lawn_companion.database.CuttingSeasonDatesDao
import com.mbw101.lawn_companion.database.setupCuttingSeasonDateRepository
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
    private lateinit var currentStartDate: Calendar
    private lateinit var currentEndDate: Calendar
    private lateinit var cuttingSeasonDatesDao: CuttingSeasonDatesDao
    private lateinit var cuttingSeasonDateRepository: CuttingSeasonDateRepository
    var startDate: Calendar? = null
    var endDate: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_dates)
        init()
        setupDB()
        setupExistingDates()
        setListeners()
    }


    private fun setupExistingDates() {
        // Access the start and end dates from the DB
        Toast.makeText(this, "Accessing dates...", Toast.LENGTH_SHORT).show()

        startDate = null
        endDate = null
        runBlocking {
            launch (Dispatchers.IO) { // lifecycleScope.launch(Dispatchers.Default)
//                startDate = cuttingSeasonDatesDao.getStartDate()?.calendarValue
//                endDate = cuttingSeasonDatesDao.getEndDate()?.calendarValue
                startDate = cuttingSeasonDateRepository.getStartDate()?.calendarValue
                endDate = cuttingSeasonDateRepository.getEndDate()?.calendarValue
            }
        }

        Timer().schedule(1500) {
            if (startDate != null && endDate != null) {
//                startDateSelector.text =
//                    getString(
//                        R.string.datePlaceholder,
//                        startDate!!.get(Calendar.DAY_OF_MONTH),
//                        startDate!!.get(Calendar.MONTH) + 1,
//                        startDate!!.get(Calendar.YEAR)
//                    )
//
//                endDateSelector.text =
//                    getString(
//                        R.string.datePlaceholder,
//                        endDate!!.get(Calendar.DAY_OF_MONTH),
//                        endDate!!.get(Calendar.MONTH) + 1,
//                        endDate!!.get(Calendar.YEAR)
//                    )
                endDateSelector.text = "31/12/2021"
            }
            else {
                endDateSelector.text = "31/12/2021"
            }

            startDateSelector.invalidate()
            endDateSelector.invalidate()
        }
    }

    private fun init() {
        backIcon = findViewById(R.id.backIcon)
        saveDatesButton = findViewById(R.id.saveDatesButton)
        startDateSelector = findViewById(R.id.startDateSelector)
        endDateSelector = findViewById(R.id.endDateSelector)
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
                { view, year, monthOfYear, dayOfMonth ->
                    startDateSelector.text =
                        getString(R.string.datePlaceholder, dayOfMonth, monthOfYear + 1, year)
                    currentStartDate = Calendar.getInstance()
                    currentStartDate.set(Calendar.YEAR, year)
                    currentStartDate.set(Calendar.MONTH, monthOfYear)
                    currentStartDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
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
                { view, year, monthOfYear, dayOfMonth ->
                    endDateSelector.text =
                        getString(R.string.datePlaceholder, dayOfMonth, monthOfYear + 1, year)
                    currentEndDate = Calendar.getInstance()
                    currentEndDate.set(Calendar.YEAR, year)
                    currentEndDate.set(Calendar.MONTH, monthOfYear)
                    currentEndDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
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
                    cuttingSeasonDatesDao.insertStartDate(currentStartDate)
                    cuttingSeasonDatesDao.insertEndDate(currentEndDate)
                }
            }

            Toast.makeText(this, "Saving dates...", Toast.LENGTH_LONG).show()
            Timer().schedule(1000) {
                launchSettingsActivity()
            }
        }
    }

    private fun setupDB() {
        cuttingSeasonDateRepository = setupCuttingSeasonDateRepository(this)
//        val db = AppDatabaseBuilder.getInstance(MyApplication.applicationContext())
//        cuttingSeasonDatesDao = db.cuttingSeasonDatesDao()
    }

    override fun onBackPressed() {
        launchSettingsActivity()
    }

    private fun launchSettingsActivity() {
        val intent = Intent(MyApplication.applicationContext(), SettingsActivity::class.java)
        startActivity(intent)
        finish()
    }
}