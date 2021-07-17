package com.mbw101.lawn_companion.weather

import com.google.gson.annotations.SerializedName

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-12
 */

const val MIN_CUTTING_HUMIDITY = 45
const val MAX_CUTTING_HUMIDITY = 85

data class CurrentWeather(
    @field:SerializedName("dt") 
    val timeOfRetrievalInMillis: Long? = null, // Time of data calculation, unix, UTC

    @field:SerializedName("sunrise")
    val sunrise: Long? = null,

    @field:SerializedName("sunset")
    val sunset: Long? = null,

    @field:SerializedName("temp")
    val temperature: Int? = null,

    @field:SerializedName("feels_like")
    val feelsLikeTemp: Int? = null,

    @field:SerializedName("humidity")
    val humidity: Int? = null,

    @field:SerializedName("uvi") // UV index
    val uvIndex: Int? = null,

    @field:SerializedName("weather")
    val weatherDescription: WeatherDescription? = null
)

// assumes celsius temperature when calculating if it's suitable
fun isCurrentWeatherSuitable(currentWeather: CurrentWeather): Boolean {
    // TODO: Add and adjust these conditions to be more realistic
    // especially ones relating to temperature, humidity, and uvIndex
    if (currentWeather.weatherDescription == null ||
        currentWeather.humidity == null) return false
    if (!isWeatherDescriptionSuitable(currentWeather.weatherDescription)) return false
    //if (!isWithinDayTempRange((Float) currentWeather.temperature)) return false

    if (!isWithinHumidityRange(currentWeather.humidity)) return false

    // TODO: Implement UV Index range check

    return true
}

fun isWithinHumidityRange(humidity: Int): Boolean {
    if (humidity < MIN_CUTTING_HUMIDITY ||
        humidity > MAX_CUTTING_HUMIDITY) return false
    return true
}