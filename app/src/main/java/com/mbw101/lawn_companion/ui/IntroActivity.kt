package com.mbw101.lawn_companion.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.utils.ApplicationPrefs


/**
Lawn Companion
Created by Malcolm Wright
Date: May 13th, 2021
*/

private const val NUM_SCREENS = 4

class IntroActivity : FragmentActivity() {

    private lateinit var nextButton: Button

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    companion object {
        lateinit var preferenceManager: ApplicationPrefs
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set intro activity to full screen
        setContentView(R.layout.activity_intro)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // start the introduction
        init()
        setupListeners()
    }

    /***
     * This function will check to see if
     * the Intro has been completed by using
     * NotificationPrefs
     */
    private fun init() {
        preferenceManager = ApplicationPrefs()
        // DEBUG STATEMENT, always ensures intro activity is ran
//        preferenceManager.setNotFirstTime(false)
        if (preferenceManager.isNotFirstTime()) { // we want to automatically go into Main
            launchMainActivity()
        }

        // load all the components
        nextButton = findViewById(R.id.nextButton)
        viewPager = findViewById(R.id.introViewPager)
        tabLayout = findViewById(R.id.tab_indicator)

        // set up adapter
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter

        setupTabLayout()
    }

    private fun setupListeners() {
        nextButton.setOnClickListener {
            // Navigate to the next screen
            viewPager.currentItem = viewPager.currentItem + 1
        }
        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback(){
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.e("Selected_Page", position.toString())

                // hide/show components based on the current screen
                if (position == 3) { // location part of intro
                    nextButton.visibility = View.INVISIBLE
                    tabLayout.visibility = View.INVISIBLE
                }
                else {
                    nextButton.visibility = View.VISIBLE
                    tabLayout.visibility = View.VISIBLE
                }
            }
        })
    }

    /***
     * Adds all the icons
     * to the TabLayout in the introduction
     * screen
     */
    private fun setupTabLayout() {
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            viewPager.setCurrentItem(tab.position, true)
        }.attach()
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
    }

    /***
     * Creates an intent to open main activity
     */
    private fun launchMainActivity() {
        val intent = Intent(MyApplication.applicationContext(), MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            // allow normal back behaviour when the user is
            // on the first screen of intro
            super.onBackPressed()
        }
        else { // otherwise, go back a screen
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_SCREENS

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> FirstIntroScreenFragment()
            1 -> SecondIntroScreenFragment()
            2 -> ThirdIntroScreenFragment()
            3 -> LastIntroScreenFragment()
            else -> FirstIntroScreenFragment()
        }
    }
}