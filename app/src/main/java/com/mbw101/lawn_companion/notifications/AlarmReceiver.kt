package com.mbw101.lawn_companion.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.database.*
import com.mbw101.lawn_companion.ui.MyApplication
import com.mbw101.lawn_companion.utils.ApplicationPrefs
import com.mbw101.lawn_companion.utils.Constants
import com.mbw101.lawn_companion.utils.UtilFunctions
import com.mbw101.lawn_companion.weather.WeatherResponse
import com.mbw101.lawn_companion.weather.WeatherService
import com.mbw101.lawn_companion.weather.isCurrentWeatherSuitable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-01
 */

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        // can start checking weather/cut suitability 2 days before there next desired cut frequency
        // ex: 2 weeks but would start checking on the 12th day
        const val DAYS_SINCE_DELTA = 2
    }

    override fun onReceive(context: Context, intent: Intent) {
        val preferences = ApplicationPrefs()
        performNotificationSetup(preferences, context)
    }

    private fun performNotificationSetup(preferences: ApplicationPrefs, context: Context) {
        if (!preferencesHaveHappyConditions(preferences)) return
        Log.d(Constants.TAG, "Meets happy path conditions!")

        // check if a location is saved in db
        if (!hasLocationSaved(context)) {
            return
        }
        Log.d(Constants.TAG, "Has a lawn location saved in the DB!")

        val repository =
            CutEntryRepository(AppDatabaseBuilder.getInstance(context).cutEntryDao())
        runNotificationCoroutineWork(repository, preferences, context)
    }

    private fun preferencesHaveHappyConditions(preferences: ApplicationPrefs): Boolean {
        if (!preferences.isInTimeOfDay()
            || !notificationsAreEnabled(preferences)
            || !isCuttingSeasonTurnedOff(preferences)
            || !isInCuttingSeason()
        ) {
            Log.d(Constants.TAG, "The happy conditions have not been met! Check settings configuration!")
            return false
        }
        return true
    }

    private fun hasLocationSaved(context: Context): Boolean {
        val repository = setupLawnLocationRepository(context)
        return checkIfLocationExists(repository)
    }

    private fun checkIfLocationExists(repository: LawnLocationRepository): Boolean {
        var locationExists = false

        runBlocking {
            launch (Dispatchers.IO) {
                locationExists = repository.hasALocationSaved()
            }
        }

        return locationExists
    }

    private fun notificationsAreEnabled(preferences: ApplicationPrefs): Boolean {
        return preferences.isNotificationsEnabled()
    }

    private fun isCuttingSeasonTurnedOff(preferences: ApplicationPrefs): Boolean {
        return preferences.isInCuttingSeason()
    }

    private fun isInCuttingSeason(): Boolean {
        var inCuttingSeason = false
        val db = AppDatabaseBuilder.getInstance(MyApplication.applicationContext())
        val cuttingSeasonDatesDao = db.cuttingSeasonDatesDao()

        // access result from DB function in a different coroutine scope
        runBlocking {
            launch (Dispatchers.IO) {
                inCuttingSeason = cuttingSeasonDatesDao.isInCuttingSeasonDates()
            }
        }

        return inCuttingSeason
    }

    private fun isDataUseEnabled(preferences: ApplicationPrefs): Boolean {
        return preferences.isDataUseEnabled()
    }

    private fun runNotificationCoroutineWork(repository: CutEntryRepository, preferences: ApplicationPrefs, context: Context)
    = runBlocking {
        // starts a new coroutine, so we can access the DB concurrently without blocking the current thread (UI)
        launch (Dispatchers.IO) {
            val lastCut: CutEntry? = retrieveLastCutFromDB(repository)

            // only run through the notification logic and call the api if we have right connection type
            if (connectionTypeMatchesPreferences(preferences, context)) {
                val weatherHttpResponse: Response<WeatherResponse> = callWeatherAPI(context)
                performNotificationLogic(lastCut, weatherHttpResponse, context)
            } else {
                Log.d(Constants.TAG, "Connection type does not match preferences!")
            }
        }
    }

    private fun connectionTypeMatchesPreferences(
        preferences: ApplicationPrefs,
        context: Context
    ): Boolean {
        if (!hasInternetConnection(context)) {
            Log.d(Constants.TAG, "Has no internet connection!")
            return false
        }
        if (!isDataUseEnabled(preferences)) {
            return isUsingWifiConnection(context)
        }

        Log.d(Constants.TAG, "Connection type matches preferences!")
        return true
    }

    private fun isUsingWifiConnection(context: Context): Boolean {
        if (isUsingDataConnection(context)) {
            Log.d(Constants.TAG, "Has data connection but preference says not to use data!")
            return false
        }

        Log.d(Constants.TAG, "Connection type matches preferences!")
        return true
    }

    private fun isUsingDataConnection(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.isActiveNetworkMetered
    }

    private fun hasInternetConnection(context: Context): Boolean {
        // determines whether the user has an internet connection
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    private suspend fun callWeatherAPI(context: Context): Response<WeatherResponse> {
        val retrofit = Retrofit.Builder()
            .baseUrl(WeatherService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val weatherService = retrofit.create(WeatherService::class.java)
        val (lat, long) = getCoordinates(context)
        return weatherService.getWeather(lat, long)
    }

    private fun getCoordinates(context: Context): Pair<Double, Double> {
        val lat: Double
        val long: Double

        val repository = setupLawnLocationRepository(context)

        val lawnLocation = repository.getLocation()
        lat = lawnLocation.latitude
        long = lawnLocation.longitude
        Log.d(Constants.TAG, "Got $lawnLocation from DB!")
        return Pair(lat, long)
    }

    private suspend fun retrieveLastCutFromDB(repository: CutEntryRepository): CutEntry? {
        return repository.getLastCutSync()
    }

    private fun performNotificationLogic(
        lastCut: CutEntry?,
        weatherHttpResponse: Response<WeatherResponse>,
        context: Context
    ) {
        if (!weatherHttpResponse.isSuccessful) {
            return
        }

        val weatherData = weatherHttpResponse.body()
        Log.d(Constants.TAG, weatherData.toString())
        val prefs = ApplicationPrefs()
        if (lastCut == null) {
            // just suggest an appropriate cut (given weather conditions) anytime since there is no cut registered
            createNotificationIfSuitableConditions(weatherData, prefs.getDesiredCutFrequency(), context, prefs)
        } else {
            // calculate the time since last cut until now
            val daysSince = findDaysSince(lastCut)
            createNotificationIfSuitableConditions(weatherData, daysSince, context, prefs)
        }
    }

    private fun createNotificationIfSuitableConditions(
        weatherData: WeatherResponse?,
        daysSince: Int,
        context: Context,
        preferences: ApplicationPrefs
    ) {
        if (hasSuitableConditionsForCutNotification(weatherData, daysSince, preferences)) {
            showNotification(context)
        }
    }

    private fun hasSuitableConditionsForCutNotification(
        weatherData: WeatherResponse?,
        daysSince: Int,
        preferences: ApplicationPrefs
    ): Boolean {
        val minDaysSince = preferences.getDesiredCutFrequency() - DAYS_SINCE_DELTA

        // go through many checks and include the weather for determining the right time for a cut
        if (daysSince < minDaysSince) {
            Log.d(Constants.TAG, "The minimum days since last cut has NOT been surpassed yet!")
            return false
        }

        if (weatherData == null) {
            Log.d(Constants.TAG, "The weather data is null!")
            return false
        }

        if (!isCurrentWeatherSuitable(weatherData.current)) {
            Log.d(Constants.TAG, "The Current weather data is NOT suitable for a cut!")
            return false
        }

        return true
    }

    private fun findDaysSince(lastCut: CutEntry): Int {
        val cutDate = Calendar.getInstance()
        cutDate.set(
            Calendar.MONTH,
            lastCut.month_number - 1
        ) // month values start at 0 for Calendar
        cutDate.set(Calendar.DAY_OF_MONTH, lastCut.day_number)
        return UtilFunctions.getNumDaysSince(cutDate)
    }

    private fun showNotification(context: Context) {
        NotificationHelper.createCutNotification(
            context, context.getString(R.string.app_name),
            context.getString(R.string.cutSuggestionMessage), true
        )
    }
}