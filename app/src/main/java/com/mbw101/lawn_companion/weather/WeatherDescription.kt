package com.mbw101.lawn_companion.weather

import com.google.gson.annotations.SerializedName

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-12
 */
const val LOWEST_ACCEPTABLE_WEATHER_ID = 800
const val HIGHEST_ACCEPTABLE_WEATHER_ID = 804

data class WeatherDescription (
    @field:SerializedName("main") // describes the main weather
    val mainWeatherDescription: String? = null,

    @field:SerializedName("description")  // a detailed description of the weather
    val detailedDescription: String? = null,

    @field:SerializedName("id") // the ID which has ranges based on the weather condition
    val weatherID: Int? = null
)

fun isWeatherDescriptionSuitable(weatherDescription: WeatherDescription): Boolean {
    // TODO: Add more conditions
    if (isNullArguments(weatherDescription)) return false

    if (!isWithinWeatherIdRange(weatherDescription.weatherID!!)) return false

    return true
}

private fun isWithinWeatherIdRange(weatherID: Int): Boolean {
    if (weatherID < LOWEST_ACCEPTABLE_WEATHER_ID
        || weatherID > HIGHEST_ACCEPTABLE_WEATHER_ID) return false
    return true
}

private fun isNullArguments(weatherDescription: WeatherDescription): Boolean {
    if (weatherDescription.mainWeatherDescription == null
        || weatherDescription.detailedDescription == null
        || weatherDescription.weatherID == null) return true
    return false
}