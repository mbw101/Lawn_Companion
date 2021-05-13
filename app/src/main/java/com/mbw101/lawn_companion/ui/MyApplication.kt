package com.mbw101.lawn_companion.ui

import android.app.Application
import android.content.Context
import android.util.Log
import com.mbw101.lawn_companion.utils.Constants

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-13
 */
class MyApplication: Application() {
    init {
        myApp = this
        Log.d(Constants.TAG, "It has finished!")
    }

    override fun onCreate() {
        super.onCreate()

        // Initialization here gets done before any activity gets created
//        TODO("Implement creating the notification channel here")
    }

    companion object {
        private var myApp: MyApplication? = null

        fun applicationContext() : Context {
            return myApp!!.applicationContext
        }
    }
}