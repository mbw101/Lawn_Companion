package com.mbw101.lawn_companion.weather

import com.google.gson.annotations.SerializedName

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-12
 */

data class WeatherDescription (
    @field:SerializedName("main") // describes the main weather
    val main: String? = null,

    @field:SerializedName("description")  // a detailed description of the weather
    val description: String? = null,

    @field:SerializedName("id") // the ID which has ranges based on the weather condition
    val weatherID: Int? = null
)

fun isWeatherDescriptionSuitable(weatherDescription: WeatherDescription): Boolean {
    // TODO: Add more conditions
    if (weatherDescription.main == null) return false
    if (weatherDescription.description == null) return false
    if (weatherDescription.weatherID == null) return false

    if (weatherDescription.weatherID < 800 || weatherDescription.weatherID > 804) return false

    return true
}