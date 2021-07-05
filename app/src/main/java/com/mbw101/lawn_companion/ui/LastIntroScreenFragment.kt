package com.mbw101.lawn_companion.ui

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-13
 */
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.utils.Constants


private const val MY_PERMISSIONS_REQUEST_LOCATION = 100

class LastIntroScreenFragment : Fragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_last_intro_screen, container, false)
        view.findViewById<Button>(R.id.getStartedButton).setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?) {
        // ask for location permissions
        askPermissions()
    }

    private fun askPermissions() {
        val result = activity?.let { thisActivity -> checkLocationPermissions(thisActivity) }
        Log.d(Constants.TAG, "Result from asking permission = ${result.toString()}")
    }

    // check for background location permissions
    private fun checkLocationPermissions(activity: Activity): Boolean {
        Log.d(Constants.TAG, "Asking for permissions!")
        return when {
            // permission was not granted
            (ContextCompat.checkSelfPermission(activity.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) -> {
                requestLocationPermission(activity)
                false
            }

            else -> {
                Toast.makeText(activity, "Permission (already) Granted!", Toast.LENGTH_SHORT).show()
                launchMainActivity()
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
            }
            .setNegativeButton(android.R.string.cancel) { _, i -> //Prompt the user once explanation has been shown
                // launch main activity
                launchMainActivity()
            }
            .create()
            .show()
    }

    private fun launchMainActivity() {
        IntroActivity.preferenceManager.setNotFirstTime(true) // finished the intro of App, so we can save that
        val intent = Intent (activity, MainActivity::class.java)
        activity?.startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (permissions[0] == Manifest.permission.ACCESS_COARSE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(Constants.TAG, "Location permission has been granted!")
                launchMainActivity()
            }
            else if (permissions[0] == Manifest.permission.ACCESS_COARSE_LOCATION && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Log.d(Constants.TAG, "Location permission has been denied!")
                launchMainActivity()
            }
        }
    }
}