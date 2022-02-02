package com.mbw101.lawn_companion.weather

import android.util.Log
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mbw101.lawn_companion.utils.Constants

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-12
 */

const val MIN_CUTTING_HUMIDITY = 0
const val MAX_CUTTING_HUMIDITY = 85

data class Current(
    @SerializedName("clouds")
    @Expose
    val clouds: Int,

    @SerializedName("dew_point")
    @Expose
    val dew_point: Double,

    @SerializedName("dt")
    @Expose
    val dt: Int,

    @SerializedName("feels_like")
    @Expose
    val feels_like: Double,

    @SerializedName("humidity")
    @Expose
    val humidity: Int,

    @SerializedName("pressure")
    @Expose
    val pressure: Int,

    @SerializedName("sunrise")
    @Expose
    val sunrise: Int,

    @SerializedName("sunset")
    @Expose
    val sunset: Int,

    @SerializedName("temp")
    @Expose
    val temp: Double,

    @SerializedName("uvi")
    @Expose
    val uvi: Double,

    @SerializedName("visibility")
    @Expose
    val visibility: Int,

    @SerializedName("weather")
    @Expose
    val weather: List<Weather>,

    @SerializedName("wind_deg")
    @Expose
    val wind_deg: Int,

    @SerializedName("wind_gust")
    @Expose
    val wind_gust: Double,

    @SerializedName("wind_speed")
    @Expose
    val wind_speed: Double
)

// assumes celsius temperature when calculating if it's suitable
fun isCurrentWeatherSuitable(current: Current?): Boolean {
    // especially ones relating to temperature, humidity, and uvIndex
    if (current == null) {
        Log.e(Constants.TAG, "Current object is null, weather is not suitable for cut!")
        return false
    }
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