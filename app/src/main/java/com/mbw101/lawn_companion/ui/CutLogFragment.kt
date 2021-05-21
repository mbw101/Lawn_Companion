package com.mbw101.lawn_companion.ui

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.database.CutEntry
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
        setupCutEntries()

        // initialize components
        mainRecyclerView = v.findViewById(R.id.main_recyclerview)

        // set up the adaptor
        mainRecyclerAdaptor = MainRecyclerAdaptor(monthSections)
        mainRecyclerView.adapter = mainRecyclerAdaptor
        val itemDecoration = DividerItemDecoration(mainRecyclerView.context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(ColorDrawable(resources.getColor(R.color.light_gray)))
        mainRecyclerView.addItemDecoration(itemDecoration)

        setupListeners()
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

    private fun refresh() {
        // TODO: Refresh the list of data with DAO whenever they navigate to the cut log fragment
    }


    /**
     * Will set up the list of data
     * which will be displayed in the recyclerview
     */
    private fun setupCutEntries() {
        // load the list of sections with data
        // TODO: Later, fill in with real data via DAO

        // fill all the entries for each month
        val janEntries: List<CutEntry> = listOf(
            CutEntry("4:36pm", 25, "January", 1),
            CutEntry("4:36pm", 25, "January", 1)
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

        // add each month section
        val cal: Calendar = Calendar.getInstance()
        val currentYear: Int = cal.get(Calendar.YEAR)
        Log.d(Constants.TAG, "Current year = $currentYear")

        monthSections = listOf(MonthSection("Jan $currentYear", janEntries), MonthSection("Feb $currentYear", febEntries),
            MonthSection("Mar $currentYear", marEntries), MonthSection("Apr $currentYear", aprilEntries),
            MonthSection("May $currentYear", mayEntries)
        )
    }

}