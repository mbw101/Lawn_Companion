package com.mbw101.lawn_companion.ui

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-13
 */
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.database.AppDatabaseBuilder
import com.mbw101.lawn_companion.database.CuttingSeasonDatesDao
import com.mbw101.lawn_companion.databinding.FragmentLastIntroScreenBinding
import com.mbw101.lawn_companion.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

private const val MY_PERMISSIONS_REQUEST_LOCATION = 100

class LastIntroScreenFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentLastIntroScreenBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLastIntroScreenBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.getStartedButton.setOnClickListener(this)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        saveDefaultCuttingSeasonDates()
        askForLocationPermissions()
    }

    private fun saveDefaultCuttingSeasonDates(context: Context = MyApplication.applicationContext()) = runBlocking {
        // Ensure dates dont' exist before inserting default dates
        launch (Dispatchers.IO) {
            val dao = AppDatabaseBuilder.getInstance(context).cuttingSeasonDatesDao()
            if (hasNoDatesSetup(dao)) {
                // Insert default dates into Dates DB
                insertDefaultDates(dao)
            }
        }
    }

    private suspend fun insertDefaultDates(dao: CuttingSeasonDatesDao) {
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()

        startDate.set(Calendar.MONTH, Constants.DEFAULT_CUTTING_SEASON_START_MONTH - 1)
        endDate.set(Calendar.MONTH, Constants.DEFAULT_CUTTING_SEASON_END_MONTH - 1)
        startDate.set(Calendar.DAY_OF_MONTH, Constants.DEFAULT_CUTTING_SEASON_START_DAY)
        endDate.set(Calendar.DAY_OF_MONTH, Constants.DEFAULT_CUTTING_SEASON_END_DAY)

        dao.insertStartDate(startDate)
        dao.insertEndDate(endDate)
    }

    private suspend fun hasNoDatesSetup(dao: CuttingSeasonDatesDao) =
        !dao.hasStartDate() && !dao.hasEndDate()

    private fun askForLocationPermissions() {
        val result = activity?.let { thisActivity -> checkLocationPermissions(thisActivity) }
        Log.d(Constants.TAG, "Result from asking permission = ${result.toString()}")
    }

    private fun launchSaveLocationActivity() {
        IntroActivity.preferenceManager.setNotFirstTime(true) // finished the intro of App, so we can save that
        val intent = Intent(activity, SaveLocationActivity::class.java)
        startActivity(intent)
    }

    // check for background location permissions
    private fun checkLocationPermissions(activity: Activity): Boolean {
        Log.d(Constants.TAG, "Asking for permissions!")
        return when {
            // permission was not granted
            (ContextCompat.checkSelfPermission(activity.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) -> {
                requestLocationPermission(activity)
                true
            }

            else -> {
                Toast.makeText(activity, "Permission (already) Granted!", Toast.LENGTH_SHORT).show()
                launchSaveLocationActivity()
                true
            }
        }
    }

    private fun requestLocationPermission(activity: Activity) {
        // see if we need to show an explanation
        if (ActivityCompat.shouldShowRequestPermissionRationale
                (activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Show an explanation to the user
            showLocationPermissionDialog()
        } else {
            // No explanation needed, we can request the permission.
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    private fun showLocationPermissionDialog() {
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.permissionDialogTitle))
            .setMessage(getString(R.string.permissionDialogContent))
            .setPositiveButton(android.R.string.ok) { _, i -> //Prompt the user once explanation has been shown
                // request perms again
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
                Log.d(Constants.TAG, "ACCEPTED LOCATION PERMISSION")
                launchSaveLocationActivity()
            }
            .setNegativeButton(android.R.string.cancel) { _, i -> //Prompt the user once explanation has been shown
                launchMainActivity()
            }
            .create()
            .show()
    }

    private fun launchMainActivity() {
        IntroActivity.preferenceManager.setNotFirstTime(true) // finished the intro of App, so we can save that
        val intent = Intent(activity, MainActivity::class.java)
        activity?.startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (permissions[0] == Manifest.permission.ACCESS_COARSE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(Constants.TAG, "Location permission has been granted!")
                launchSaveLocationActivity()
            }
            else if (permissions[0] == Manifest.permission.ACCESS_COARSE_LOCATION && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Log.d(Constants.TAG, "Location permission has been denied!")
                launchMainActivity()
            }
        }
    }
}