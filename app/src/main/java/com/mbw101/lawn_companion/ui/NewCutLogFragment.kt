package com.mbw101.lawn_companion.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.google.android.gms.internal.zzagr.runOnUiThread
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.database.CutEntryRepository
import com.mbw101.lawn_companion.database.setupCutEntryRepository
import com.mbw101.lawn_companion.databinding.FragmentNewCutLogBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class NewCutLogFragment : Fragment() {
    private lateinit var cutEntryRepository: CutEntryRepository
    private lateinit var yearDropdownMenu: Spinner
    private var _binding: FragmentNewCutLogBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNewCutLogBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        cutEntryRepository = setupCutEntryRepository(MyApplication.applicationContext())

        yearDropdownMenu = binding.yearDropdownMenu

        runBlocking {
            launch (Dispatchers.IO) {
                val yearDropdownArray = cutEntryRepository.getYearDropdownArray()

                runOnUiThread {
                    // populate spinner options
                    val adapter = ArrayAdapter(
                        MyApplication.applicationContext(),
                        R.layout.spinner_item,
                        yearDropdownArray
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    yearDropdownMenu.adapter = adapter
                }
            }
        }

        // TODO: Call the appropriate DAO method for the CalendarView when implemented
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}