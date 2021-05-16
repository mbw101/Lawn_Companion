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

class ChildRecyclerAdapter(cutEntries: List<CutEntry>): RecyclerView.Adapter<ChildRecyclerAdapter.ChildCustomViewHolder>()  {
    val entries: List<CutEntry> = cutEntries

    class ChildCustomViewHolder(v: View): RecyclerView.ViewHolder(v), View.OnClickListener {
        var cutEntryDayTextView: TextView
        var cutEntryMessageTextView: TextView
        var cutTimeTextView: TextView

        init {
            v.setOnClickListener(this)

            // initialize components of each individual cut entry row
            cutEntryDayTextView = v.findViewById(R.id.cutEntryDayTextView)
            cutEntryMessageTextView = v.findViewById(R.id.cutEntryMessageTextView)
            cutTimeTextView = v.findViewById(R.id.cutTimeTextView)
        }

        // TODO: Can implement deleting a CutEntry through onClick event
        override fun onClick(p0: View?) {
            TODO("Not yet implemented")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildCustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.cut_entry_row, parent, false)
        return ChildCustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChildCustomViewHolder, position: Int) {
        holder.cutEntryDayTextView.text = entries[position].day.toString()
        holder.cutEntryMessageTextView.text = MyApplication.applicationContext().getString(R.string.completedCut)
        holder.cutTimeTextView.text = entries[position].time

    }

    override fun getItemCount(): Int = entries.size
}