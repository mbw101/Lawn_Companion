package com.mbw101.lawn_companion.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mbw101.lawn_companion.R


class HomeFragment : Fragment() {

    private lateinit var openPermissions: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        init(view)
        return view
    }

    private fun init(view: View) {
        openPermissions = view.findViewById(R.id.openPermissionsButton)
        setupListeners()
    }

    private fun setupListeners() {
        openPermissions.setOnClickListener {
            // TODO: Show permissions screen
            Toast.makeText(MyApplication.applicationContext(), "Clicked permissions button!", Toast.LENGTH_SHORT).show()
        }
    }
}