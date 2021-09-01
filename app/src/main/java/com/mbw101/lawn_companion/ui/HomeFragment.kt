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
import com.mbw101.lawn_companion.database.setupLawnLocationRepository
import com.mbw101.lawn_companion.utils.ApplicationPrefs
import com.mbw101.lawn_companion.utils.Constants
import com.mbw101.lawn_companion.utils.UtilFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var openPermissions: Button
    private lateinit var createLawnLocationButton: Button
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
                }
                else if (numDaysSince > 1) { // days will be multiple
                    MyApplication.applicationContext().getString(R.string.daysSinceLastCut, numDaysSince)
                }
                else { // a single day (so not "days")
                    MyApplication.applicationContext().getString(R.string.singleDaySinceLastCut, numDaysSince)
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
        createLawnLocationButton = view.findViewById(R.id.createLawnLocationButton)
        mainTextView = view.findViewById(R.id.mainMessageTextView)
        salutationTextView = view.findViewById(R.id.salutationTextView)

        checkPermissionsOrIfLocationSaved()
        setCorrectSalutation()
        setupListeners()
    }

    private fun setCorrectSalutation() {
        salutationTextView.text = getSalutation()
    }

    override fun onResume() {
        super.onResume()
        checkPermissionsOrIfLocationSaved() // check if permissions have been updated when app is reopened
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
        if (!preferences.isInCuttingSeason()) {
            mainTextView.text = getString(R.string.cuttingSeasonOver)
            openPermissions.visibility = View.INVISIBLE
            createLawnLocationButton.visibility = View.INVISIBLE
        }
        else {
            // this might be causing the bug with app crashing since it's in a different thread than UI but
            // will be used for updating the UI
            val hasLocationSavedInDB = createCoroutineToCheckIfLocationIsSaved()
            if (hasLocationSavedInDB) {
                setupViewModel()
                openPermissions.visibility = View.INVISIBLE
                createLawnLocationButton.visibility = View.INVISIBLE
            }
            else if (!UtilFunctions.hasLocationPermissions() && !hasLocationSavedInDB) {
                mainTextView.text = getString(R.string.needsPermissionString)
                createLawnLocationButton.visibility = View.INVISIBLE
                openPermissions.visibility = View.VISIBLE
            }
            else if (!hasLocationSavedInDB) {
                // show button + text
                mainTextView.text = getString(R.string.noLawnLocationMessage)
                createLawnLocationButton.visibility = View.VISIBLE
            }
        }
    }

    private fun createCoroutineToCheckIfLocationIsSaved(): Boolean {
        var hasLocationSaved = false

        runBlocking {
            launch (Dispatchers.IO) {
                hasLocationSaved = checkIfLocationSaved()
            }
        }

        return hasLocationSaved
    }

    private suspend fun checkIfLocationSaved(): Boolean {
        val lawnLocationRepository = setupLawnLocationRepository(MyApplication.applicationContext())
        return lawnLocationRepository.hasALocationSaved()
    }

    /***
     * Returns the appropriate salutation
     * based on the time of day
     */
    private fun getSalutation(): String {
        val cal: Calendar = Calendar.getInstance()
        Log.d("Lawn Companion", "Time: " + cal.get(Calendar.HOUR_OF_DAY))
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
        viewModel.getSortedCuts().observe(viewLifecycleOwner, { entries -> //update RecyclerView later
            // set up text on home frag depending on if a location is saved or not
            mainTextView.text = getDescriptionMessage(entries)
        })
    }

    // Show permissions screen for app in settings
    private fun openPermissionSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri: Uri = Uri.fromParts("package", MyApplication.applicationContext().packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}
