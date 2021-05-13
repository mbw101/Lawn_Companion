package com.mbw101.lawn_companion.ui

import android.app.Application

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-13
 */
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        myApp = this

        // Initialization here gets done before any activity gets created
        TODO("Implement creating the notification channel here")
    }

    companion object {
        private var myApp: MyApplication? = null

        fun self(): MyApplication {
            return myApp!!
        }
    }
}