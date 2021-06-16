package com.mbw101.lawn_companion.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.database.CutEntry

/**
Lawn Companion
Created by Malcolm Wright
Date: May 16th, 2021
 */

class MainRecyclerAdaptor(): RecyclerView.Adapter<MainRecyclerAdaptor.CustomViewHolder>() {

    private var sectionList = mutableListOf<MonthSection>()

    class CustomViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var monthTextView: TextView
        var childRecyclerView: RecyclerView

        init {
            // initialize components of each month section
            monthTextView = v.findViewById(R.id.monthHeaderTextView)
            childRecyclerView = v.findViewById(R.id.childRecyclerView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.section_row, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
//        if (sectionList == null) return
        val section: MonthSection = sectionList!![position]
        val monthName: String = section.monthName
        val cutEntries: List<CutEntry> = section.items

        holder.monthTextView.text = monthName

        val childAdaptor = ChildRecyclerAdapter(cutEntries)

        holder.childRecyclerView.adapter = childAdaptor
    }

    override fun getItemCount(): Int = sectionList?.size ?: 0

    fun setSections(sectionList: List<MonthSection>) {
        this.sectionList = sectionList as MutableList<MonthSection>
        // loop through each month and set cuts for that month
//        for ()
        notifyDataSetChanged() // redraw the layout
    }

    fun setEntries(cutEntries: List<CutEntry>) {
        // TODO: Create the necessary months for each cuts that occur in the list
        var tempList: List<MonthSection> = listOf(MonthSection("Hello World", cutEntries))

        // once that work is done, set the section list inside the class to the newly made one
        sectionList = tempList as MutableList<MonthSection>
        notifyDataSetChanged()
    }
}