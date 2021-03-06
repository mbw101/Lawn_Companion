package com.mbw101.lawn_companion.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.mbw101.lawn_companion.BuildConfig
import com.mbw101.lawn_companion.R
import com.mbw101.lawn_companion.database.*
import com.mbw101.lawn_companion.ui.MyApplication
import com.mbw101.lawn_companion.utils.ApplicationPrefs
import com.mbw101.lawn_companion.utils.Constants
import com.mbw101.lawn_companion.utils.UtilFunctions
import com.mbw101.lawn_companion.weather.GetWeatherListener
import com.mbw101.lawn_companion.weather.WeatherResponse
import com.mbw101.lawn_companion.weather.WeatherService
import com.mbw101.lawn_companion.weather.isCurrentWeatherSuitable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit


/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-01
 */

class AlarmReceiver : BroadcastReceiver(), GetWeatherListener {
    companion object {
        // can start checking weather/cut suitability 2 days before there next desired cut frequency
        // ex: 2 weeks but would start checking on the 12th day
        const val DAYS_SINCE_DELTA = 2

        // used for weather text view on home fragment
        fun preDownloadCriteriaCheckForWeatherSuitability(preferences: ApplicationPrefs): Boolean {
            if (!isCuttingSeasonTurnedOn(preferences)
                || !isInCuttingSeason()
                || !preferences.hasLocationSaved()
            ) {
                Log.e(Constants.TAG, "Won't show weather suitability text view on home screen")
                return false
            }

            return true
        }

        fun notificationsAreEnabled(preferences: ApplicationPrefs): Boolean {
            return preferences.areNotificationsEnabled()
        }

        fun isCuttingSeasonTurnedOn(preferences: ApplicationPrefs): Boolean {
            return preferences.isInCuttingSeason()
        }

        fun isInCuttingSeason(): Boolean {
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

        private fun buildWeatherAPI(): WeatherService {
            val client: OkHttpClient = OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(WeatherService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(WeatherService::class.java)
        }

        // Gets the retrofit Call and gets a response from the weather API
        // Calls the respective GetWeatherListener function based on success or failure
        suspend fun callWeatherAPI(context: Context, weatherListener: GetWeatherListener, weatherService: WeatherService = buildWeatherAPI()) {
            val (lat, long) = getCoordinates(context)
            val weatherCall = weatherService.getWeather(lat, long)

            weatherCall.enqueue(object : Callback<WeatherResponse?> {
                override fun onResponse(
                    call: Call<WeatherResponse?>,
                    response: Response<WeatherResponse?>
                ) {
                    Log.e(Constants.TAG, "LOGGING FROM BROADCAST RECEIVER")
                    if (!response.isSuccessful) {
                        Log.e(Constants.TAG, "Response wasn't success. Code: " + response.code())
                        return
                    }
                    val weatherResponse: WeatherResponse = response.body() ?: return

                    Log.e(Constants.TAG, "response.body() = $weatherResponse")
                    Log.e(Constants.TAG, "response = $response")

                    weatherListener.onSuccess(weatherResponse)
                }

                override fun onFailure(call: Call<WeatherResponse?>, error: Throwable) {
                    Log.e(Constants.TAG, error.toString())
                    weatherListener.onFailure(error.toString())
                }
            })
        }

        private suspend fun getCoordinates(context: Context): Pair<Double, Double> {
            val lat: Double
            val long: Double

            val repository = setupLawnLocationRepository(context)

            val lawnLocation = repository.getLocation()
            lat = lawnLocation.latitude
            long = lawnLocation.longitude
            if (BuildConfig.DEBUG) {
                Log.d(Constants.TAG, "Got $lawnLocation from DB!")
            }
            return Pair(lat, long)
        }

        fun connectionTypeMatchesPreferences(
            preferences: ApplicationPrefs,
            context: Context
        ): Boolean {
            if (!hasInternetConnection(context)) {
                Log.e(Constants.TAG, "Has no internet connection!")
                return false
            }
            if (!isDataUseEnabled(preferences)) {
                return isUsingWifiConnection(context)
            }

            Log.e(Constants.TAG, "Connection type matches preferences!")
            return true
        }

        private fun hasInternetConnection(context: Context): Boolean {
            // determines whether the user has an internet connection
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork ?: return false
                val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

                // include Wi-Fi or data in this check
                return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            }
            else {
                return connectivityManager.activeNetworkInfo?.isConnectedOrConnecting ?: false
            }
        }

        private fun isDataUseEnabled(preferences: ApplicationPrefs): Boolean {
            return preferences.isDataUseEnabled()
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
            Log.d(Constants.TAG, "No location is saved in the DB!")
            return
        }

        Log.d(Constants.TAG, "Has a lawn location saved in the DB!")
        runNotificationCoroutineWork(preferences, context)
    }

    private fun preferencesHaveHappyConditions(preferences: ApplicationPrefs): Boolean {
        if (!preferences.isInTimeOfDay()
            || !notificationsAreEnabled(preferences)
            || !isCuttingSeasonTurnedOn(preferences)
            || !isInCuttingSeason()
            || preferences.shouldSkipNotification()
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

    private fun runNotificationCoroutineWork(preferences: ApplicationPrefs, context: Context) {
        val weatherListener = this
        // only run through the notification logic and call the api if we have right connection type
        if (connectionTypeMatchesPreferences(preferences, context)) {
            Log.d(Constants.TAG, "Connection type matches preferences!")
            runBlocking {
                launch (Dispatchers.IO) {
                    callWeatherAPI(context, weatherListener)
                }
            }
        }
        else {
            Log.d(Constants.TAG, "Connection type does not match preferences!")
        }
    }

    private suspend fun retrieveLastCutFromDB(repository: CutEntryRepository): CutEntry? {
        return repository.getLastCutSync()
    }

    private fun performNotificationLogic(
        lastCut: CutEntry?,
        weatherData: WeatherResponse,
        context: Context
    ) {
        Log.d(Constants.TAG, "Current weather: " + weatherData.current.toString())
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
        Firebase.crashlytics.log("Weather data = $weatherData")
        if (hasSuitableConditionsForCutNotification(weatherData, daysSince, preferences)) {
            Log.d(Constants.TAG, "The conditions are suitable for a cut!")
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
        if (daysSince in 0 until minDaysSince) {
            Log.e(Constants.TAG, "The minimum days since last cut has NOT been surpassed yet!")
            return false
        }

        if (weatherData == null) {
            Log.e(Constants.TAG, "The weather data is null!")
            return false
        }

        Log.e(Constants.TAG, "Checking if current weather object of weather data is suitable")
        if (!isCurrentWeatherSuitable(weatherData.current)) {
            Log.e(Constants.TAG, "The Current weather data is NOT suitable for a cut!")
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

    override fun onSuccess(response: WeatherResponse?) {
        Log.e(Constants.TAG, "Response: $response")
        if (response == null) {
            return
        }

        runBlocking {
            launch (Dispatchers.IO) {
                val repository = setupCutEntryRepository(MyApplication.applicationContext())
                val lastCut: CutEntry? = retrieveLastCutFromDB(repository)
                performNotificationLogic(lastCut, response, MyApplication.applicationContext())
            }
        }
    }

    override fun onFailure(errorMessage: String) {
        Log.e(Constants.TAG, "Error message = $errorMessage")
    }
}
