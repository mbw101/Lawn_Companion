package com.mbw101.lawn_companion.ui

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
import com.mbw101.lawn_companion.database.*
import com.mbw101.lawn_companion.utils.Constants
import java.util.*


/**
Lawn Companion
Created by Malcolm Wright
Date: May 15th, 2021
 */


class CutLogFragment : Fragment() {
    private lateinit var mainRecyclerView: RecyclerView
    private lateinit var monthSections: List<MonthSection>
    private lateinit var mainRecyclerAdaptor: MainRecyclerAdaptor

    private lateinit var db: CutEntryDatabase
    private val viewModel: CutEntryViewModel by viewModels()
    private lateinit var cutEntryDAO: CutEntryDAO
    private lateinit var cutEntryRepository: CutEntryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_cut_log, container, false)
        init(view)
        return view
    }

    private fun init(v: View) {
        setupDB()

        // initialize components
        mainRecyclerView = v.findViewById(R.id.main_recyclerview)

        // set up the adaptor
        mainRecyclerAdaptor = MainRecyclerAdaptor()
        mainRecyclerView.adapter = mainRecyclerAdaptor

        // set up entries in database



        val itemDecoration = DividerItemDecoration(mainRecyclerView.context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(ColorDrawable(resources.getColor(R.color.light_gray)))
        mainRecyclerView.addItemDecoration(itemDecoration)

        setupListeners()
        setupViewmodel()
    }

    private fun setupDB() {
        // set up Room
        db = DatabaseBuilder.getInstance(MyApplication.applicationContext())
        cutEntryDAO = db.cutEntryDao()
        cutEntryRepository = CutEntryRepository(cutEntryDAO)
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

    private fun setupViewmodel() {
        // set up view model with fragment
        viewModel.getCuts().observe(viewLifecycleOwner, { entries -> //update RecyclerView later
            // get each months data using the repository
//            setupCutEntries(entries)

            // call setSections
            mainRecyclerAdaptor.setEntries(entries)
        })
    }

    private fun refresh() {
        // TODO: Refresh the list of data with DAO whenever they navigate to the cut log fragment

    }

    /**
     * Will set up the list of data
     * which will be displayed in the recyclerview
     */
    private fun setupCutEntries(entries: List<CutEntry>) {
        // load the list of sections with data
        // TODO: Later, fill in with real data via DAO
        // TODO: Figure out how to display "No cuts made part"

        // fill all the entries for each month
//        val janEntries: List<CutEntry> = cutRepository.findByMonthName(1)
        val janEntries: List<CutEntry> = listOf(
//            CutEntry("4:36pm", 25, "January", 1),
//            CutEntry("4:36pm", 25, "January", 1)
        )
        val febEntries: List<CutEntry> = listOf(
            CutEntry("4:36pm", 25, "February", 1),
            CutEntry("4:36pm", 25, "February", 1)
        )
        val marEntries: List<CutEntry> = listOf(
            CutEntry("4:36pm", 25, "March", 1),
            CutEntry("4:36pm", 25, "March", 1)
        )
        val aprilEntries: List<CutEntry> = listOf(
            CutEntry("4:36pm", 25, "april", 1),
            CutEntry("4:36pm", 25, "april", 1)
        )
        val mayEntries: List<CutEntry> = listOf(
            CutEntry("4:36pm", 25, "may", 1),
            CutEntry("4:36pm", 25, "may", 1)
        )
        val juneEntries: List<CutEntry> = listOf()

        // add each month section
        val cal: Calendar = Calendar.getInstance()
        val currentYear: Int = cal.get(Calendar.YEAR)
        Log.d(Constants.TAG, "Current year = $currentYear")

//        monthSections = listOf(MonthSection("Jan $currentYear", janEntries), MonthSection("Feb $currentYear", febEntries),
//            MonthSection("Mar $currentYear", marEntries), MonthSection("Apr $currentYear", aprilEntries),
//            MonthSection("May $currentYear", mayEntries), MonthSection("June $currentYear", juneEntries),
//            MonthSection("June $currentYear", juneEntries), MonthSection("July $currentYear", emptyList()),
//            MonthSection("Aug $currentYear", emptyList()), MonthSection("Sept $currentYear", emptyList()),
//            MonthSection("Oct $currentYear", emptyList()), MonthSection("Nov $currentYear", emptyList()),
//            MonthSection("Dec $currentYear", emptyList())
//        )
        monthSections = createMonthSections(entries)?.toList() ?: listOf(MonthSection("Jan $currentYear", janEntries), MonthSection("Feb $currentYear", febEntries),
            MonthSection("Mar $currentYear", marEntries), MonthSection("Apr $currentYear", aprilEntries),
            MonthSection("May $currentYear", mayEntries), MonthSection("June $currentYear", juneEntries),
            MonthSection("June $currentYear", juneEntries), MonthSection("July $currentYear", emptyList()),
            MonthSection("Aug $currentYear", emptyList()), MonthSection("Sept $currentYear", emptyList()),
            MonthSection("Oct $currentYear", emptyList()), MonthSection("Nov $currentYear", emptyList()),
            MonthSection("Dec $currentYear", emptyList())
        )
    }

    /**
     * Goes through all the entries and creates a monthSection list
     *
     */
    private fun createMonthSections(entries: List<CutEntry>): ArrayList<MonthSection>? {
        var tempMonthSections: ArrayList<MonthSection>? = null

        for (i in 1..12) {
            getEntriesFromSpecificMonth(entries, i)?.let {
                MonthSection(
                    "${Constants.months[i]} ${getCurrentYear()}",
                    it.toList()
                )
            }?.let { tempMonthSections?.add(it) }
        }
        return tempMonthSections
    }

    private fun getCurrentYear(): Int {
        // add each month section
        val cal: Calendar = Calendar.getInstance()
        val currentYear: Int = cal.get(Calendar.YEAR)
        Log.d(Constants.TAG, "Current year = $currentYear")
        return currentYear
    }

    private fun getEntriesFromSpecificMonth(entries: List<CutEntry>, month: Int): ArrayList<CutEntry>? {
        val returnEntries: ArrayList<CutEntry>? = null

        // loop through entries and add any entries
        val iterator = entries.iterator()
        iterator.forEach { entry ->
            if (entry.month_num == month) returnEntries?.add(entry)
        }

        return returnEntries
    }
}