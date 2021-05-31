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

class ChildRecyclerAdapter(cutEntries: List<CutEntry>) :
    RecyclerView.Adapter<ChildRecyclerAdapter.ChildCustomViewHolder>() {
    var entries: List<CutEntry> = cutEntries

    class ChildCustomViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
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

        // TODO: Can implement deleting a CutEntry through onClick event or modifying an existing one
        override fun onClick(p0: View?) {
            TODO("Not yet implemented")
        }
    }

    class PlaceholderViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var cutEntryMessageTextView: TextView

        init {
            // initialize components of each individual cut entry row
            cutEntryMessageTextView = v.findViewById(R.id.cutEntryMessageTextView)
            cutEntryMessageTextView.text =
                MyApplication.applicationContext().getString(R.string.noCutMade)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildCustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.cut_entry_row, parent, false)

        return ChildCustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChildCustomViewHolder, position: Int) {
        Log.d(Constants.TAG, entries.toString())
        holder.cutEntryDayTextView.text = entries[position].day_number.toString()
        holder.cutEntryMessageTextView.text =
            MyApplication.applicationContext().getString(R.string.completedCut)
        holder.cutTimeTextView.text = entries[position].cut_time
    }

    override fun getItemCount(): Int = entries.size

    fun setCuts(entries: List<CutEntry>) {
        this.entries = entries
        notifyDataSetChanged() // redraw the layout
    }
}