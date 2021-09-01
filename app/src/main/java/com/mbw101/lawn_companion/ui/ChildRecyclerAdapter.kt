package com.mbw101.lawn_companion.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.database.CutEntry
import com.mbw101.lawn_companion.utils.Constants


/**
Lawn Companion
Created by Malcolm Wright
Date: May 16th, 2021
 */

class ChildRecyclerAdapter(cutEntries: List<CutEntry>,
                           var onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<ChildRecyclerAdapter.ChildCustomViewHolder>() {
    var entries: List<CutEntry> = cutEntries

    class ChildCustomViewHolder(v: View) : RecyclerView.ViewHolder(v){
        // initialize components of each individual cut entry row
        var cutEntryDayTextView: TextView = v.findViewById(R.id.cutEntryDayTextView)
        var cutEntryMessageTextView: TextView = v.findViewById(R.id.cutEntryMessageTextView)
        var cutTimeTextView: TextView = v.findViewById(R.id.cutTimeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildCustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.cut_entry_row, parent, false)

        return ChildCustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChildCustomViewHolder, position: Int) {
        Log.d(Constants.TAG, entries.toString())
        val entry: CutEntry = entries[position]
        holder.cutEntryDayTextView.text = entries[position].day_number.toString()
        holder.cutEntryMessageTextView.text =
            MyApplication.applicationContext().getString(R.string.completedCut)
        holder.cutTimeTextView.text = entries[position].cut_time

        // set on click listener, so we can call our custom interface to pass the
        // CutEntry data to our fragment
        holder.itemView.setOnClickListener(View.OnClickListener {
            Log.d(Constants.TAG, "onClick: $position")
            // call the interface method
            onItemClickListener.onItemClick(entry)
        })

    }

    override fun getItemCount(): Int = entries.size

    fun setCuts(entries: List<CutEntry>) {
        this.entries = entries
        notifyDataSetChanged() // redraw the layout
    }
}

interface OnItemClickListener {
    fun onItemClick(entry: CutEntry)
}
