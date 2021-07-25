package com.mbw101.lawn_companion.weather

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-12
 */
const val LOWEST_ACCEPTABLE_WEATHER_ID = 800
const val HIGHEST_ACCEPTABLE_WEATHER_ID = 804

data class WeatherItem(val icon: String = "",
                       val description: String = "",
                       val main: String = "",
                       val id: Int = 0)

fun isWeatherDescriptionSuitable(weatherDescription: WeatherItem): Boolean {
    // TODO: Add more conditions
    if (!isWithinWeatherIdRange(weatherDescription.id)) return false

    return true
}

private fun isWithinWeatherIdRange(weatherID: Int): Boolean {
    if (weatherID < LOWEST_ACCEPTABLE_WEATHER_ID
        || weatherID > HIGHEST_ACCEPTABLE_WEATHER_ID) return false
    return true
}
