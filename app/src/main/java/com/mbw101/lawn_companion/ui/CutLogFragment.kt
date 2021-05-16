package com.mbw101.lawn_companion.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mbw101.lawn_companion.R

/**
Lawn Companion
Created by Malcolm Wright
Date: May 15th, 2021
 */


class CutLogFragment : Fragment() {
    lateinit var months: List<MonthSection>
    lateinit var mainRecyclerAdapter: MainRecyclerAdaptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cut_log, container, false)
    }


    private fun init() {
        // set up adaptors
//        mainRecyclerAdapter = findViewById(R.id.main_recyclerview)
    }

}