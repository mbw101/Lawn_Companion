package com.mbw101.lawn_companion.ui

import android.app.Application
import android.content.Context

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-13
 */
class MyApplication: Application() {
    init {
        myApp = this
    }

    companion object {
        private var myApp: MyApplication? = null

        fun applicationContext() : Context {
            // TODO: Returning null on testExpectedMessage
            return myApp!!.applicationContext
        }
    }
}