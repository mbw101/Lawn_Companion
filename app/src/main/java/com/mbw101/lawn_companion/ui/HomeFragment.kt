package com.mbw101.lawn_companion.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.database.CutEntry
import com.mbw101.lawn_companion.utils.ApplicationPrefs
import com.mbw101.lawn_companion.utils.Constants
import com.mbw101.lawn_companion.utils.UtilFunctions
import com.mbw101.lawn_companion.weather.WeatherService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var openPermissions: Button
    private lateinit var mainTextView: TextView
    private lateinit var salutationTextView: TextView
    private val viewModel: CutEntryViewModel by viewModels()

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

            // month numbers in Calendar start at 0
            if (currentDate.get(Calendar.MONTH) == (latestCut.month_number - 1) &&
                currentDate.get(Calendar.DAY_OF_MONTH) == latestCut.day_number) {
                return MyApplication.applicationContext().getString(R.string.alreadyCutMessage)
            }
            else {
                // determine the date of last cut using last entry in list
                val cal = Calendar.getInstance()
                cal.set(Calendar.MONTH, latestCut.month_number - 1) // month numbers in Calendar start at 0
                cal.set(Calendar.DAY_OF_MONTH, latestCut.day_number)

                // TODO: Implement the user's preference for how long they require a cut (replace the 1 week value -> 7 days)
                val numDaysSince = UtilFunctions.getNumDaysSince(cal)
                return if (numDaysSince > 7) {
                    MyApplication.applicationContext().getString(R.string.passedIntervalMessage)

                } else {
                    MyApplication.applicationContext().getString(R.string.daysSinceLastCut, numDaysSince)
                }
            }
        }
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
        checkPermissions()

        // set correct salutation
        salutationTextView.text = getSalutation()
        setupListeners()
        getWeather()
    }

    private fun getWeather() {
        val retrofit = Retrofit.Builder()
            .baseUrl(WeatherService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherService = retrofit.create(WeatherService::class.java)
        var coroutineJob = CoroutineScope(Dispatchers.IO).launch {
            val httpResponse = weatherService.getWeather(43.531054f, -80.230215f)

            withContext(Dispatchers.Main) {
                if (httpResponse.isSuccessful) {
                    val weatherData = httpResponse.body()
                    if (weatherData != null) {
                        Log.d(Constants.TAG, "Current weather = ${weatherData.current}")
                        Log.d(Constants.TAG, "Daily forecast list = ${weatherData.daily}")

                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissions() // check if permissions have been updated when app is reopened
    }

    private fun setupListeners() {
        openPermissions.setOnClickListener {
            openPermissions()
        }
    }

    /***
     * Shows the correct UI elements
     * based on permissions and preferences
     */
    private fun checkPermissions() {
        val preferences = ApplicationPrefs()

        // look at if they turned on/off cutting season
        if (!preferences.isInCuttingSeason()) {
            mainTextView.text = getString(R.string.cuttingSeasonOver)
        }
        else {
            // shows the permissions button based on current permissions
            openPermissions.visibility = when (UtilFunctions.hasLocationPermissions()) {
                true -> {
                    setupViewModel()
                    View.INVISIBLE
                }
                false -> {
                    mainTextView.text = getString(R.string.needsPermissionString)
                    View.VISIBLE
                }
            }
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

    // sets up ViewModel and calls getDescriptionMessage
    private fun setupViewModel() {
        // set up view model with fragment
        viewModel.getSortedCuts().observe(viewLifecycleOwner, { entries -> //update RecyclerView later
            // set up text on home frag
            mainTextView.text = getDescriptionMessage(entries)
        })
    }

    // Show permissions screen for app in settings
    private fun openPermissions() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri: Uri = Uri.fromParts("package", MyApplication.applicationContext().packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}