package com.mbw101.lawn_companion.weather

import android.util.Log
import com.mbw101.lawn_companion.utils.Constants

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-12
 */

const val MIN_CUTTING_HUMIDITY = 0
const val MAX_CUTTING_HUMIDITY = 85

data class Current(val sunrise: Int = 0,
                   val temp: Double = 0.0,
                   val visibility: Int = 0,
                   val uvi: Double = 0.0,
                   val pressure: Int = 0,
                   val clouds: Int = 0,
                   val feelsLike: Double = 0.0,
                   val windGust: Double = 0.0,
                   val dt: Int = 0,
                   val sunset: Int = 0,
                   val weather: List<WeatherItem>?,
                   val humidity: Int = 0,
                   val windSpeed: Double = 0.0)

// assumes celsius temperature when calculating if it's suitable
fun isCurrentWeatherSuitable(current: Current): Boolean {
    // especially ones relating to temperature, humidity, and uvIndex
    if (current.weather == null) {
        Log.e(Constants.TAG, "Current weather is null, weather is not suitable for cut!")
        return false
    }
    if (!isWeatherDescriptionSuitable(current.weather[0])) {
        Log.e(Constants.TAG, "Current weather is not suitable for a cut!")
        return false
    }
    if (!isWithinDayTempRange(current.temp)) {
        Log.e(Constants.TAG, "Current temperature is not within the temp range, so the weather is not suitable for cut!")
        return false
    }
    if (!isWithinHumidityRange(current.humidity)) {
        Log.e(Constants.TAG, "The current humidity is not within range, so the weather is not suitable for cut!")
        return false
    }

    // TODO: Implement UV Index range check in future

    return true
}

fun isWithinHumidityRange(humidity: Int): Boolean {
    if (humidity < MIN_CUTTING_HUMIDITY ||
        humidity > MAX_CUTTING_HUMIDITY) return false
    return true
}