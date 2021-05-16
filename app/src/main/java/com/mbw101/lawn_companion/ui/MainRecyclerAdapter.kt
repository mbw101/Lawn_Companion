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

class MainRecyclerAdaptor(list: List<MonthSection>): RecyclerView.Adapter<MainRecyclerAdaptor.CustomViewHolder>() {

    var sectionList: List<MonthSection> = list

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
        val section: MonthSection = sectionList[position]
        val monthName: String = section.monthName
        val cutEntries: List<CutEntry> = section.items

        holder.monthTextView.text = monthName

        val childAdapter = ChildRecyclerAdapter(cutEntries)
        holder.childRecyclerView.adapter = childAdapter
    }

    override fun getItemCount(): Int = sectionList.size
}