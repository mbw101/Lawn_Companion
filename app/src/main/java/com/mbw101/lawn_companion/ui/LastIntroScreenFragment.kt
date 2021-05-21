package com.mbw101.lawn_companion.ui

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-13
 */
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.utils.Constants
import com.mbw101.lawn_companion.utils.UtilFunctions.checkLocationPermissions

class LastIntroScreenFragment : Fragment(), View.OnClickListener {
    private lateinit var getStartedButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_last_intro_screen, container, false)
        view.findViewById<Button>(R.id.getStartedButton).setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?) {
        // ask for location permissions
        //askPermissions()

        // launch main activity
        launchMainActivity()
    }

    // TODO: Fix asking location permissions properly
    private fun askPermissions() {
        val result = activity?.let { thisActivity -> checkLocationPermissions(thisActivity) }
        Log.d(Constants.TAG, result.toString())
    }

    private fun launchMainActivity() {
        IntroActivity.preferenceManager.setNotFirstTime(true) // finished the intro of App, so we can save that
        val intent = Intent (activity, MainActivity::class.java)
        activity?.startActivity(intent)
    }
}