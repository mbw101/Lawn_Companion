package com.mbw101.lawn_companion.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mbw101.lawn_companion.BuildConfig
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.database.CutEntryRepository
import com.mbw101.lawn_companion.database.setupCutEntryRepository
import com.mbw101.lawn_companion.databinding.ActivityMainBinding
import com.mbw101.lawn_companion.notifications.AlarmReceiver
import com.mbw101.lawn_companion.notifications.AlarmScheduler
import com.mbw101.lawn_companion.notifications.NotificationHelper
import com.mbw101.lawn_companion.utils.Constants
import com.mbw101.lawn_companion.utils.UtilFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*


/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-14
 */

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var settingsIcon: ImageView
    private lateinit var yearDropdown: Spinner
    private lateinit var titleTextView: TextView

    private lateinit var binding: ActivityMainBinding
    private lateinit var cutEntryRepository: CutEntryRepository
    var yearDropdownArray: List<String> = listOf()

    companion object {
        lateinit var addCutFAB: FloatingActionButton

        fun setupNotificationAlarmManager() {
            // Pass in the pending intent
            val intent = Intent(MyApplication.applicationContext(), AlarmReceiver::class.java)
            val pendingIntent =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    PendingIntent.getBroadcast(
                        MyApplication.applicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE
                    )
                } else {
                    PendingIntent.getBroadcast(
                        MyApplication.applicationContext(),
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }

            val alarmManager: AlarmManager =
                MyApplication.applicationContext()
                    .getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // set notification
            AlarmScheduler.scheduleAlarmManager(
                Calendar.getInstance().get(Calendar.DAY_OF_WEEK),
                pendingIntent, alarmManager
            )
        }

        fun createNotificationChannel(context: Context) {
            NotificationHelper.createNotificationChannel(
                context,
                NotificationManagerCompat.IMPORTANCE_HIGH, true,
                context.getString(R.string.app_name), "Notification channel"
            )
        }
    }

    fun updateYearDropdown() {
        retrieveYearsList()
        fillInYearDropdown()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        // get updated year dropdown values
        cutEntryRepository = setupCutEntryRepository(this)
        retrieveYearsList()
        init()
        setListeners()

        // create notification channel
        createNotificationChannel(this)

        setupNotificationAlarmManager()

//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // TODO: Cause of issue in espresso UI tests
////                requestPermissions(
////                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
////                    MY_PERMISSIONS_REQUEST_LOCATION
////                )
//            }
//            return
//        }
    }

    private fun retrieveYearsList() {
        runBlocking {
            launch(Dispatchers.IO) {
                yearDropdownArray = cutEntryRepository.getYearDropdownArray()
            }
        }
    }

    /***
     * Launches the HomeFragment and
     * gets anything else ready to go
     */
    private fun init() {
        // initialize components
        settingsIcon = binding.settingsIcon
        titleTextView = binding.titleTextView
        addCutFAB = binding.addCutFAB
        yearDropdown = binding.yearDropdown
        yearDropdown.visibility = View.INVISIBLE

        // set up bottom navigation
        navController = findNavController(R.id.nav_host_fragment)
        bottomNav = binding.bottomNav
        bottomNav.setupWithNavController(navController)

        fillInYearDropdown()

        if (BuildConfig.DEBUG) {
            val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyFlashScreen()
//                .penaltyDialog()
                .build()
            StrictMode.setThreadPolicy(policy)
        }
    }

    override fun onResume() {
        super.onResume()
        retrieveYearsList()
        fillInYearDropdown()
    }

    private fun fillInYearDropdown() {
        val currentYear = UtilFunctions.getCurrentYear().toString()

        // always have the current year be in there if it's not present
        val yearArray: List<String> = if (yearDropdownArray.isEmpty()) {
            listOf(currentYear)
        } else {
            if (!yearDropdownArray.contains(currentYear)) {
                listOf(currentYear) + yearDropdownArray
            } else {
                yearDropdownArray
            }
        }

        Log.d(Constants.TAG, "Year array: $yearArray")

        // testing purposes
//        yearDropdownArray = MyApplication.applicationContext().resources.getStringArray(R.array.year_dropdown_test_values)
//            .toList()
        yearDropdownArray = yearArray
        val yearAdaptor: ArrayAdapter<String> = ArrayAdapter(
            this, android.R.layout.simple_spinner_item,
            yearDropdownArray //MyApplication.applicationContext().resources.getStringArray(R.array.year_dropdown_test_values)
        )

        yearDropdown.adapter = yearAdaptor
        yearAdaptor.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        yearDropdown.setSelection(0) // always set to current year in dropdown menu
    }

    private fun setListeners() {
        // bottom navigation listener
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.home -> {
                    // navigate to the home fragment
                    titleTextView.text = getString(R.string.home)
                    yearDropdown.visibility = View.INVISIBLE
                }
                R.id.cutlog -> {
                    // navigate to the cut log fragment
                    titleTextView.text = getString(R.string.cutLog)
                    yearDropdown.visibility = View.VISIBLE
                    // switch year dropdown value to the most recent year
                    yearDropdown.setSelection(0)
                }
            }
        }

        // toolbar button listeners
        settingsIcon.setOnClickListener {
            launchSettings()
        }

        // FAB listener
        addCutFAB.setOnClickListener {
            launchAddCutScreen()
        }
    }

    private fun launchSettings() {
        val intent = Intent(this@MainActivity, SettingsActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun launchAddCutScreen() {
        val intent = Intent(this@MainActivity, AddCutActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        val homeItem: MenuItem = bottomNav.menu.getItem(0)

        if (bottomNav.selectedItemId != homeItem.itemId) {
            // Select home item
            bottomNav.selectedItemId = homeItem.itemId
        } else {
            finish()
        }
    }
}