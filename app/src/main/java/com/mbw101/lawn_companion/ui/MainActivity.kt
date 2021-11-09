package com.mbw101.lawn_companion.ui

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.databinding.ActivityMainBinding
import com.mbw101.lawn_companion.notifications.AlarmReceiver
import com.mbw101.lawn_companion.notifications.AlarmScheduler
import com.mbw101.lawn_companion.notifications.NotificationHelper
import java.util.*


/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-14
 */

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var settingsIcon: ImageView
    private lateinit var refreshIcon: ImageView
    private lateinit var titleTextView: TextView

    private lateinit var binding: ActivityMainBinding

    companion object {
        lateinit var addCutFAB: FloatingActionButton

        fun setupNotificationAlarmManager() {
            // Pass in the pending intent
            val intent = Intent(MyApplication.applicationContext(), AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                MyApplication.applicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager: AlarmManager =
                MyApplication.applicationContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // set notification
            AlarmScheduler.scheduleAlarmManager(
                Calendar.getInstance().get(Calendar.DAY_OF_WEEK),
                pendingIntent, alarmManager)
        }

        fun createNotificationChannel(context: Context) {
            NotificationHelper.createNotificationChannel(
                context,
                NotificationManagerCompat.IMPORTANCE_DEFAULT, true,
                context.getString(R.string.app_name), "Notification channel"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
        setListeners()

        // create notification channel
        createNotificationChannel(this)

        setupNotificationAlarmManager()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return
        }
    }

    /***
     * Launches the HomeFragment and
     * gets anything else ready to go
     */
    private fun init() {
        // initialize components
        bottomNav = binding.bottomNav
        settingsIcon = binding.settingsIcon
        refreshIcon = binding.refreshIcon
        titleTextView = binding.titleTextView
        addCutFAB = binding.addCutFAB
    }

    private fun setListeners() {
        // bottom navigation listener
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    // navigate to the home fragment
                    findNavController(R.id.nav_host_fragment).navigate(R.id.nav_home)
                    titleTextView.text = getString(R.string.home)

                    // update the label if necessary (refer to conditions)
                    true
                }
                R.id.cutLog -> {
                    // navigate to the cut log fragment
                    findNavController(R.id.nav_host_fragment).navigate(R.id.nav_cutlog)
                    titleTextView.text = getString(R.string.cutLog)

                    // clear all entries and update them again
                    true
                }
                else -> false
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

        refreshIcon.setOnClickListener {
            refreshActivity()
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

    private fun refreshActivity() {
        Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show()
        findViewById<ViewGroup>(android.R.id.content).invalidate()
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