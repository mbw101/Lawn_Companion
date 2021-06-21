package com.mbw101.lawn_companion.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.database.CutEntry
import com.mbw101.lawn_companion.utils.UtilFunctions
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var openPermissions: Button
    private lateinit var mainTextView: TextView
    private lateinit var salutationTextView: TextView

    companion object {
        // Note: When calling this function in this fragment class, check before calling it that
        // permissions are granted. If there isn't permission access, there's no sense in finding
        // the description message first

        // Determines the appropriate message to display on the home fragment
        // Takes a list of cuts sorted in ascending order
        fun getDescriptionMessage(entries: List<CutEntry>): String {
            // take into account the last cut (or if there even is an entry made)
            if (entries.isEmpty()) {
                return  MyApplication.applicationContext().getString(R.string.noCutMessage)
            }

            // get the current date
            val currentDate = Calendar.getInstance()
            val latestCut = entries.last()

            if (currentDate.get(Calendar.MONTH) == latestCut.month_num &&
                currentDate.get(Calendar.DAY_OF_MONTH) == latestCut.day_number) {
                return MyApplication.applicationContext().getString(R.string.alreadyCutMessage)
            }
            else {
                // determine the date of last cut using last entry in list
                val cal = Calendar.getInstance()
                cal.set(Calendar.MONTH, latestCut.month_num)
                cal.set(Calendar.DAY_OF_MONTH, latestCut.day_number)

                // TODO: Implement the user's preference for how long they require a cut (replace the 1 week value -> 7 days)
                val numDaysSince = UtilFunctions.getNumDaysSince(cal)
                if (numDaysSince > 7) {
                    return MyApplication.applicationContext().getString(R.string.passedIntervalMessage)
                }
                else {
                    return MyApplication.applicationContext().getString(R.string.daysSinceLastCut, numDaysSince)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        init(view)
        return view
    }

    private fun init(view: View) {
        openPermissions = view.findViewById(R.id.openPermissionsButton)
        mainTextView = view.findViewById(R.id.mainMessageTextView)
        salutationTextView = view.findViewById(R.id.salutationTextView)
        // shows the permissions button based on current permissions
        openPermissions.visibility = when (UtilFunctions.hasLocationPermissions()) {
            true -> {
                // TODO: Call getDescriptionMessage() using ViewModel sorted data for entries (just like cut entry fragment)
                mainTextView.text = getString(R.string.noCutMessage)
                View.INVISIBLE
            }
            false -> {
                mainTextView.text = getString(R.string.needsPermissionString)
                View.VISIBLE
            }
        }

        // set correct salutation
        salutationTextView.text = getSalutation()

        setupListeners()
    }

    private fun setupListeners() {
        openPermissions.setOnClickListener {
            // TODO: Show permissions screen
            Toast.makeText(MyApplication.applicationContext(), "Clicked permissions button!", Toast.LENGTH_SHORT).show()
        }
    }

    /***
     * Returns the appropriate salutation
     * based on the time of day
     */
    private fun getSalutation(): String {
        val cal: Calendar = Calendar.getInstance()
        Log.d("Lawn Companion", "Time: " + cal.get(Calendar.HOUR_OF_DAY))

        return when (cal.get(Calendar.HOUR_OF_DAY)) {
            5, 6, 7, 8, 9, 10, 11 -> { // good morning
                getString(R.string.goodMorning)
            }

            12, 13, 14, 15, 16, 17 -> { // good afternoon
                getString(R.string.goodAfternoon)
            }

            else -> { // good evening
                getString(R.string.goodNight)
            }
        }
    }
}