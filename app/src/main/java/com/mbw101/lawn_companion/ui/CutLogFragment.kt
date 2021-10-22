package com.mbw101.lawn_companion.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.database.CutEntry
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


class CutLogFragment : Fragment(), OnItemClickListener {
    private lateinit var mainRecyclerView: RecyclerView
    private lateinit var monthSections: List<MonthSection>
    private lateinit var mainRecyclerAdaptor: MainRecyclerAdaptor

    private val viewModel: CutEntryViewModel by viewModels()

    companion object {
        // sets up the hashmap containing all entries for each respective month
        fun setupHashmap(entries: List<CutEntry>): HashMap<Int, List<CutEntry>> {
            // fill all the entries for each month
            val tempList = mutableListOf<CutEntry>()
            var previousMonth = 1
            var currentMonth: Int
            val hashMap: java.util.HashMap<Int, List<CutEntry>> = createEmptyMonthHashMap()

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
            val hashMap: java.util.HashMap<Int, List<CutEntry>> = HashMap()

            // map each month to empty list to start
            for (i in 1..12) {
                hashMap[i] = emptyList()
            }
            return hashMap
        }

        fun setupMonthSections(hashMap: HashMap<Int, List<CutEntry>>): List<MonthSection> {
            val currentYear = UtilFunctions.getCurrentYear()
            println(hashMap)
            // add each month section
            return listOf(
                MonthSection("Jan $currentYear", hashMap[Constants.Month.JANUARY.monthNum] ?: emptyList()),
                MonthSection("Feb $currentYear", hashMap[Constants.Month.FEBRUARY.monthNum] ?: emptyList()),
                MonthSection("Mar $currentYear", hashMap[Constants.Month.MARCH.monthNum] ?: emptyList()),
                MonthSection("Apr $currentYear", hashMap[Constants.Month.APRIL.monthNum] ?: emptyList()),
                MonthSection("May $currentYear", hashMap[Constants.Month.MAY.monthNum] ?: emptyList()),
                MonthSection("June $currentYear", hashMap[Constants.Month.JUNE.monthNum] ?: emptyList()),
                MonthSection("July $currentYear", hashMap[Constants.Month.JULY.monthNum] ?: emptyList()),
                MonthSection("Aug $currentYear", hashMap[Constants.Month.AUGUST.monthNum] ?: emptyList()),
                MonthSection("Sept $currentYear", hashMap[Constants.Month.SEPTEMBER.monthNum] ?: emptyList()),
                MonthSection("Oct $currentYear", hashMap[Constants.Month.OCTOBER.monthNum] ?: emptyList()),
                MonthSection("Nov $currentYear", hashMap[Constants.Month.NOVEMBER.monthNum] ?: emptyList()),
                MonthSection("Dec $currentYear", hashMap[Constants.Month.DECEMBER.monthNum] ?: emptyList())
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_cut_log, container, false)
        init(view)
        return view
    }

    private fun init(v: View) {
        // initialize components
        mainRecyclerView = v.findViewById(R.id.main_recyclerview)

        // set up the adaptor
        mainRecyclerAdaptor = MainRecyclerAdaptor(this) // pass in our click listener method
        mainRecyclerView.adapter = mainRecyclerAdaptor

        val itemDecoration = DividerItemDecoration(mainRecyclerView.context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(ColorDrawable(resources.getColor(R.color.light_gray)))
        mainRecyclerView.addItemDecoration(itemDecoration)

        setupListeners()
        setupViewModel()
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

    private fun setupViewModel() {
        runBlocking {
            launch (Dispatchers.IO) {
                val sortedEntriesFromCurrentYear = viewModel.getEntriesFromSpecificYearSorted(UtilFunctions.getCurrentYear())
                setupCutEntries(sortedEntriesFromCurrentYear)
                mainRecyclerAdaptor.setSections(monthSections)
            }
        }
    }

    /**
     * Will set up the list of data
     * which will be displayed in the recyclerview
     */
    private fun setupCutEntries(entries: List<CutEntry>) {
        // set up hashmap with entries for each month and create month sections
        val hashMap: HashMap<Int, List<CutEntry>> = setupHashmap(entries)
        monthSections = setupMonthSections(hashMap)
    }

    override fun onItemClick(entry: CutEntry): Unit = runBlocking{
        Log.d(Constants.TAG, "onItemClick: $entry")
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.alertTitle)
            .setPositiveButton(R.string.deleteOption,
                DialogInterface.OnClickListener { dialog, id ->
                    deleteCut(entry)
//                    mainRecyclerAdaptor.notifyDataSetChanged()
                })
            .setNegativeButton(R.string.cancelOption,
                DialogInterface.OnClickListener { dialog, id ->
                    // User cancelled the dialog
                })
            .setMessage(R.string.alertMessage)

        // Create the AlertDialog object and set properties
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun deleteCut(entry: CutEntry) = runBlocking {
        viewModel.deleteCuts(entry)
    }
}