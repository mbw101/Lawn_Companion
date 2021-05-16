package com.mbw101.lawn_companion.ui

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.database.CutEntry


/**
Lawn Companion
Created by Malcolm Wright
Date: May 15th, 2021
 */


class CutLogFragment : Fragment() {
    companion object {
        lateinit var mainRecyclerView: RecyclerView
        lateinit var monthSections: List<MonthSection>
        lateinit var mainRecyclerAdaptor: MainRecyclerAdaptor
    }

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
//        mainRecyclerView.addItemDecoration(DividerItemDecoration(MyApplication.applicationContext(), DividerItemDecoration.VERTICAL))
    }

    /**
     * Will set up the list of data
     * which will be displayed in the recyclerview
     */
    private fun setupCutEntries() {
        // load the list of sections with data
        // TODO: Later, fill in with real data via DAO
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
        monthSections = listOf(MonthSection("Jan", janEntries), MonthSection("Feb", febEntries),
            MonthSection("Mar", marEntries), MonthSection("Apr", aprilEntries), MonthSection("May", mayEntries)
        )
    }

}