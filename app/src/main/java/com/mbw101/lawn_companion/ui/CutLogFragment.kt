package com.mbw101.lawn_companion.ui

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.internal.zzagr.runOnUiThread
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.database.CutEntry
import com.mbw101.lawn_companion.databinding.FragmentCutLogBinding
import com.mbw101.lawn_companion.utils.Constants
import com.mbw101.lawn_companion.utils.UtilFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*


/**
Lawn Companion
Created by Malcolm Wright
Date: May 15th, 2021
 */


class CutLogFragment : Fragment(), OnItemClickListener, AdapterView.OnItemSelectedListener {
    private lateinit var mainRecyclerView: RecyclerView
    private lateinit var monthSections: List<MonthSection>
    private lateinit var mainRecyclerAdaptor: MainRecyclerAdaptor

    private val viewModel: CutEntryViewModel by viewModels()
    private var _binding: FragmentCutLogBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var currentYear = UtilFunctions.getCurrentYear()

    companion object {
        // sets up the hashmap containing all entries for each respective month
        fun setupHashmap(entries: List<CutEntry>): HashMap<Int, List<CutEntry>> {
            // fill all the entries for each month
            val tempList = mutableListOf<CutEntry>()
            var previousMonth = 1
            var currentMonth: Int
            val hashMap: HashMap<Int, List<CutEntry>> = createEmptyMonthHashMap()

            for (cut in entries) {
                currentMonth = cut.month_number

                // map all those entries to previous month and clear tempList
                if (currentMonth != previousMonth) {
                    hashMap[previousMonth] = tempList.toList()
                    tempList.clear()
                }
                tempList.add(cut)

                // set previous
                previousMonth = currentMonth
            }

            // add last list to hash map
            hashMap[previousMonth] = tempList.toList()

            return hashMap
        }

        private fun createEmptyMonthHashMap(): HashMap<Int, List<CutEntry>> {
            val hashMap: HashMap<Int, List<CutEntry>> = HashMap()

            // map each month to empty list to start
            for (i in 1..12) {
                hashMap[i] = emptyList()
            }
            return hashMap
        }

        fun setupMonthSections(hashMap: HashMap<Int, List<CutEntry>>, year: Int): List<MonthSection> {
            println(hashMap)
            // add each month section
            return listOf(
                MonthSection("Jan $year", hashMap[Constants.Month.JANUARY.monthNum] ?: emptyList()),
                MonthSection("Feb $year", hashMap[Constants.Month.FEBRUARY.monthNum] ?: emptyList()),
                MonthSection("Mar $year", hashMap[Constants.Month.MARCH.monthNum] ?: emptyList()),
                MonthSection("Apr $year", hashMap[Constants.Month.APRIL.monthNum] ?: emptyList()),
                MonthSection("May $year", hashMap[Constants.Month.MAY.monthNum] ?: emptyList()),
                MonthSection("June $year", hashMap[Constants.Month.JUNE.monthNum] ?: emptyList()),
                MonthSection("July $year", hashMap[Constants.Month.JULY.monthNum] ?: emptyList()),
                MonthSection("Aug $year", hashMap[Constants.Month.AUGUST.monthNum] ?: emptyList()),
                MonthSection("Sept $year", hashMap[Constants.Month.SEPTEMBER.monthNum] ?: emptyList()),
                MonthSection("Oct $year", hashMap[Constants.Month.OCTOBER.monthNum] ?: emptyList()),
                MonthSection("Nov $year", hashMap[Constants.Month.NOVEMBER.monthNum] ?: emptyList()),
                MonthSection("Dec $year", hashMap[Constants.Month.DECEMBER.monthNum] ?: emptyList())
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCutLogBinding.inflate(inflater, container, false)
        val view = binding.root
        init()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun init() {
        // initialize components
        mainRecyclerView = binding.cutlogRecyclerview

        // set up the adaptor
        mainRecyclerAdaptor = MainRecyclerAdaptor(this) // pass in our click listener method
        mainRecyclerView.adapter = mainRecyclerAdaptor

        val itemDecoration = DividerItemDecoration(mainRecyclerView.context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(ColorDrawable(ContextCompat.getColor(MyApplication.applicationContext(), R.color.light_gray)))
        mainRecyclerView.addItemDecoration(itemDecoration)

        // set up listener for year dropdown
        val yearDropdown = requireActivity().findViewById<Spinner>(R.id.yearDropdown)
        yearDropdown.onItemSelectedListener = this

        setupListeners()
        setupViewModel(UtilFunctions.getCurrentYear())
    }

    private fun setupListeners() {
        // recyclerview listener (check for scrolling, so the FAB can be hidden)
        mainRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && MainActivity.addCutFAB.isShown) MainActivity.addCutFAB.hide()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) MainActivity.addCutFAB.show()
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    fun setupViewModel(year: Int) {
        runBlocking {
            launch (Dispatchers.IO) {
                val sortedEntriesFromCurrentYear = viewModel.getEntriesFromSpecificYearSorted(year)
                setupCutEntries(sortedEntriesFromCurrentYear, year)
                runOnUiThread {
                    mainRecyclerAdaptor.setSections(monthSections)
                }
            }
        }
    }

    /**
     * Will set up the list of data
     * which will be displayed in the recyclerview
     */
    private fun setupCutEntries(entries: List<CutEntry>, year: Int) {
        // set up hashmap with entries for each month and create month sections
        val hashMap: HashMap<Int, List<CutEntry>> = setupHashmap(entries)
        monthSections = setupMonthSections(hashMap, year)
    }

    override fun onItemClick(entry: CutEntry): Unit = runBlocking{
        Log.d(Constants.TAG, "onItemClick: $entry")
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.alertTitle)
            .setPositiveButton(R.string.deleteOption) { _, _ ->
                // remove cut entry and update the list in the main recyclerview
                deleteCut(entry)
                setupViewModel(currentYear)
                val myActivity = activity as MainActivity
                myActivity.updateYearDropdown()
            }
            .setNegativeButton(R.string.cancelOption) { _, _ ->
                // User cancelled the dialog
            }
            .setMessage(R.string.alertMessage)

        // Create the AlertDialog object and set properties
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun deleteCut(entry: CutEntry) = runBlocking {
        viewModel.deleteCuts(entry)
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
        Log.d(Constants.TAG, "Position: $position")

        val mainActivity = activity as MainActivity
        val selectedYear = mainActivity.yearDropdownArray[position].toInt()
        Log.d(Constants.TAG, "Selected year: $selectedYear")
        setupViewModel(selectedYear)
        currentYear = selectedYear
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
}