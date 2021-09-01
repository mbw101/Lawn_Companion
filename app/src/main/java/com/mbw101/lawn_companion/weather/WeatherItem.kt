package com.mbw101.lawn_companion.weather

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-12
 */
val acceptableIds = listOf(721, 741, 800, 801, 802, 803, 804)

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
    if (!acceptableIds.contains(weatherID)) return false
    return true
}
