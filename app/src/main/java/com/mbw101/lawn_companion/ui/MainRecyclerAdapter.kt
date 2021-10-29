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

class MainRecyclerAdaptor(clickListener: OnItemClickListener): RecyclerView.Adapter<MainRecyclerAdaptor.CustomViewHolder>() {

    // add custom interface
    private var onItemClickListener: OnItemClickListener = clickListener
    private var sectionList = mutableListOf<MonthSection>()

    class CustomViewHolder(v: View) : RecyclerView.ViewHolder(v){
        // initialize components of each month section
        var monthTextView: TextView = v.findViewById(R.id.monthHeaderTextView)
        var childRecyclerView: RecyclerView = v.findViewById(R.id.childRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.section_row, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        // set the month text for the month section
        val section: MonthSection = sectionList[position]
        val monthName: String = section.monthName
        val cutEntries: List<CutEntry> = section.items
        holder.monthTextView.text = monthName

        // create adaptor and pass in the list of entries
        val childAdaptor = ChildRecyclerAdapter(cutEntries, onItemClickListener)
        holder.childRecyclerView.adapter = childAdaptor
//        holder.childRecyclerView.adapter.notifyItemRangeChanged(0, cutEntries.size)
    }

    override fun getItemCount(): Int = sectionList.size

    fun setSections(sectionList: List<MonthSection>) {
        this.sectionList = sectionList as MutableList<MonthSection>
        notifyItemRangeChanged(0, sectionList.size) // redraw the layout
    }
}