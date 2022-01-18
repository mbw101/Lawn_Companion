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
import com.mbw101.lawn_companion.BuildConfig
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.database.AppDatabaseBuilder
import com.mbw101.lawn_companion.database.CutEntry
import com.mbw101.lawn_companion.databinding.FragmentHomeBinding
import com.mbw101.lawn_companion.notifications.AlarmReceiver
import com.mbw101.lawn_companion.notifications.AlarmReceiver.Companion.callWeatherAPI
import com.mbw101.lawn_companion.utils.ApplicationPrefs
import com.mbw101.lawn_companion.utils.Constants
import com.mbw101.lawn_companion.utils.UtilFunctions
import com.mbw101.lawn_companion.utils.UtilFunctions.allowReads
import com.mbw101.lawn_companion.weather.WeatherResponse
import com.mbw101.lawn_companion.weather.isCurrentWeatherSuitable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var openPermissions: Button
    private lateinit var createLawnLocationButton: Button
    private lateinit var mainTextView: TextView
    private lateinit var secondaryTextView: TextView
    private lateinit var salutationTextView: TextView
    private lateinit var weatherSuitabilityTextView: TextView
    private val viewModel: CutEntryViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private var withinCuttingSeason: Boolean = false
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        // Note: When calling this function in this fragment class, check before calling it that
        // permissions are granted. If there isn't permission access, there's no sense in finding
        // the description message first

        // Determines the appropriate message to display on the home fragment
        // Takes a list of cuts sorted in ascending order
        fun getDescriptionMessage(entries: List<CutEntry>): String {
            // take into account the last cut (or if there even is an entry made)
            if (entries.isEmpty()) {
                return MyApplication.applicationContext().getString(R.string.noCutMessage)
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
                val preferences = ApplicationPrefs()

                val numDaysSince = UtilFunctions.getNumDaysSince(cal)
                return if (numDaysSince > preferences.getDesiredCutFrequency() || numDaysSince < 0) { // 2nd condition accounts for cuts in previous year
                    MyApplication.applicationContext().getString(R.string.passedIntervalMessage)
                }
                else if (numDaysSince > 1) { // days will be multiple
                    MyApplication.applicationContext().getString(R.string.daysSinceLastCut, numDaysSince)
                }
                else { // a single day (so not "days")
                    MyApplication.applicationContext().getString(R.string.singleDaySinceLastCut, numDaysSince)
                }
            }
        }

        // used for tracking latest weather response
        private lateinit var weatherHttpResponse: WeatherResponse
        private lateinit var timeOfWeatherCall: Calendar
        private fun isWeatherCallInitialized() = ::timeOfWeatherCall.isInitialized
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        init()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun init() {
        val dbBuilder = AppDatabaseBuilder.getInstance(MyApplication.applicationContext())
        openPermissions = binding.openPermissionsButton
        createLawnLocationButton = binding.createLawnLocationButton
        mainTextView = binding.mainMessageTextView
        secondaryTextView = binding.secondaryTextView
        salutationTextView = binding.salutationTextView
        weatherSuitabilityTextView = binding.weatherSuitabilityTextView
        setupListeners()
        setCorrectSalutation()

        showProgressBar()
        runBlocking {
            launch (Dispatchers.IO) {
                // check if in cutting season
                withinCuttingSeason = dbBuilder.cuttingSeasonDatesDao().isInCuttingSeasonDates()
                val preferences = ApplicationPrefs()
                requireActivity().runOnUiThread {
                    checkPermissionsOrIfLocationSaved()
                    performSuitabilityTextViewWork(preferences)
                    hideProgressBar()
                }
            }
        }
    }

    private fun updateSuitabilityTextView() {
        // check if we've recently updated it
        if (!shouldCallAPI()) {
            // the suitability text gets reset when switching fragments, so we must update it based on the weather
            updateWeatherSuitabilityText(weatherHttpResponse)
            return
        }

        runBlocking {
            launch(Dispatchers.IO) {
                val httpWeatherResponse = callWeatherAPI(MyApplication.applicationContext())

                if (!httpWeatherResponse.isSuccessful) {
                    return@launch
                }

                val weatherData = httpWeatherResponse.body() ?: return@launch

                weatherHttpResponse = weatherData
                timeOfWeatherCall = Calendar.getInstance()
                Log.e(Constants.TAG, "Weather response = $weatherHttpResponse")

                // update the text view with correct string
                requireActivity().runOnUiThread {
                    updateWeatherSuitabilityText(weatherData)
                }
            }
        }
    }

    private fun updateWeatherSuitabilityText(weatherData: WeatherResponse) {
        if (isCurrentWeatherSuitable(weatherData.current)) {
            val appContext = MyApplication.applicationContext()
            val hasSkippedNotification = ApplicationPrefs().shouldSkipNotification()
            val areNotificationsEnabled = ApplicationPrefs().areNotificationsEnabled()

            weatherSuitabilityTextView.text =
                if (hasSkippedNotification) {
                    appContext.getString(R.string.suitableWeatherMessage) + " " + appContext.getString(R.string.skippedNotificationMessage)
                }
                else if (!areNotificationsEnabled) {
                    appContext.getString(R.string.suitableWeatherMessage) + " " + appContext.getString(R.string.disabledNotificationsMessage)
                }
                else {
                    appContext.getString(R.string.suitableWeatherMessage) + " " + appContext.getString(R.string.expectNotificationMessage)
                }

        } else {
            weatherSuitabilityTextView.text = MyApplication.applicationContext()
                .getString(R.string.unsuitableWeatherMessage)
        }
    }

    private val MILLIS_TO_HOURS = 3600000
    private val HOURS_UNTIL_UPDATE = 2
    private fun shouldCallAPI(): Boolean {
        if (!isWeatherCallInitialized()) {
            return true
        }

        val currentTime = Calendar.getInstance()
        val diff: Long = currentTime.timeInMillis - timeOfWeatherCall.timeInMillis
        val hours = diff / MILLIS_TO_HOURS

        return hours >= HOURS_UNTIL_UPDATE
    }

    private fun setCorrectSalutation() {
        salutationTextView.text = getSalutation()
    }

    private fun performSuitabilityTextViewWork(preferences: ApplicationPrefs) {
        if (!AlarmReceiver.preDownloadCriteriaCheckForWeatherSuitability(preferences)) {
            weatherSuitabilityTextView.visibility = View.INVISIBLE
        }
        else if (!AlarmReceiver.connectionTypeMatchesPreferences(preferences, MyApplication.applicationContext())) {
            weatherSuitabilityTextView.visibility = View.VISIBLE
            weatherSuitabilityTextView.text =
                getString(R.string.noConnectionAvailableWeatherMessage)
        }
        else {
            weatherSuitabilityTextView.visibility = View.VISIBLE
            updateSuitabilityTextView()
        }
    }

    private fun setupListeners() {
        openPermissions.setOnClickListener {
            openPermissionSettings()
        }
        createLawnLocationButton.setOnClickListener {
            openSaveLocationActivity()
        }
    }

    private fun openSaveLocationActivity() {
        val intent = Intent(activity, SaveLocationActivity::class.java)
        startActivity(intent)
    }

    /***
     * Shows the correct UI elements
     * based on permissions and preferences
     */
    private fun checkPermissionsOrIfLocationSaved() {
        val preferences = ApplicationPrefs()

        // look at if they turned on/off cutting season
        if (!preferences.isInCuttingSeason() || !withinCuttingSeason) {
            mainTextView.text = getString(R.string.cuttingSeasonOver)
            openPermissions.visibility = View.INVISIBLE
            createLawnLocationButton.visibility = View.INVISIBLE
            secondaryTextView.visibility = View.INVISIBLE
        }
        else {
            val hasLocationSavedInDB = preferences.hasLocationSaved()
            if (!UtilFunctions.hasLocationPermissions()) {
                secondaryTextView.text = getString(R.string.needsPermissionString)
                secondaryTextView.visibility = View.VISIBLE
                createLawnLocationButton.visibility = View.INVISIBLE
                openPermissions.visibility = View.VISIBLE
            }
            else if (hasLocationSavedInDB) {
                setupViewModel()
                secondaryTextView.visibility = View.INVISIBLE
                openPermissions.visibility = View.INVISIBLE
                createLawnLocationButton.visibility = View.INVISIBLE
                secondaryTextView.visibility = View.INVISIBLE
            }
            else if (!hasLocationSavedInDB) {
                // show button + text
                secondaryTextView.text = getString(R.string.noLawnLocationMessage)
                secondaryTextView.visibility = View.VISIBLE
                createLawnLocationButton.visibility = View.VISIBLE
            }
        }
    }

    /***
     * Returns the appropriate salutation
     * based on the time of day
     */
    private fun getSalutation(): String {
        val cal: Calendar = Calendar.getInstance()
        val hourOfDay = cal.get(Calendar.HOUR_OF_DAY)

         when (hourOfDay) {
            in Constants.MORNING_HOUR_START_TIME..Constants.MORNING_HOUR_END_TIME -> {
                return getString(R.string.goodMorning)
            }
            in Constants.AFTERNOON_HOUR_START_TIME..Constants.AFTERNOON_HOUR_END_TIME -> {
                return getString(R.string.goodAfternoon)
            }
            in Constants.EVENING_HOUR_START_TIME..Constants.EVENING_HOUR_END_TIME -> {
                return getString(R.string.goodEvening)
            }
            else -> { // between NIGHT_HOUR_START_TIME downTo Constants.NIGHT_HOUR_END_TIME
                return getString(R.string.goodNight)
            }
        }
    }

    // sets up ViewModel and calls getDescriptionMessage
    private fun setupViewModel() {
        // set up view model with fragment
        if (BuildConfig.DEBUG) {
            allowReads {
                viewModel.getSortedCuts()
                    .observe(viewLifecycleOwner, { entries -> //update RecyclerView later
                        // set up text on home frag depending on if a location is saved or not
                        mainTextView.text = getDescriptionMessage(entries)
                    })
            }
        }
        else {
            viewModel.getSortedCuts()
                .observe(viewLifecycleOwner, { entries -> //update RecyclerView later
                    // set up text on home frag depending on if a location is saved or not
                    mainTextView.text = getDescriptionMessage(entries)
                })
        }
    }

    // Show permissions screen for app in settings
    private fun openPermissionSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri: Uri = Uri.fromParts("package", MyApplication.applicationContext().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun showProgressBar() {
        if (binding != null) {
            val progressBar = binding.progressBar
            progressBar.visibility = View.VISIBLE
            binding.homeFragmentContent.visibility = View.INVISIBLE
        }
    }

    private fun hideProgressBar() {
        if (binding != null) {
            val progressBar = binding.progressBar
            progressBar.visibility = View.INVISIBLE
            binding.homeFragmentContent.visibility = View.VISIBLE
        }
    }
}