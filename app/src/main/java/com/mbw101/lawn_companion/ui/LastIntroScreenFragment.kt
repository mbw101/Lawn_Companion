package com.mbw101.lawn_companion.ui

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-13
 */
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.mbw101.lawn_companion.R

class LastIntroScreenFragment : Fragment(), View.OnClickListener {
    private lateinit var getStartedButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_last_intro_screen, container, false)
        view.findViewById<Button>(R.id.getStartedButton).setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?) {
        // ask for location permissions


        // launch main activity
        launchMainActivity()
    }

    private fun askPermissions() {
        TODO("Request background location permissions")
    }

    private fun launchMainActivity() {
        IntroActivity.preferenceManager.setNotFirstTime(true) // finished the intro of App, so we can save that
        val intent = Intent (activity, MainActivity::class.java)
        activity?.startActivity(intent)
    }
}