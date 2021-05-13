package com.mbw101.lawn_companion.ui

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-13
 */
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mbw101.lawn_companion.R

class ThirdIntroScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_3rd_intro_screen, container, false)
}