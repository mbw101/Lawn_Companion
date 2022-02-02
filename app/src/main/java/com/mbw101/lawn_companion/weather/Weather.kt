package com.mbw101.lawn_companion.weather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-12
 */
val acceptableIds = listOf(721, 741, 800, 801, 802, 803, 804)

data class Weather(
    @SerializedName("description")
    @Expose
    val description: String,

    @SerializedName("icon")
    @Expose
    val icon: String,

    @SerializedName("id")
    @Expose
    val id: Int?,

    @SerializedName("main")
    @Expose
    val main: String
)

fun isWeatherDescriptionSuitable(weatherDescription: Weather?): Boolean {
    // TODO: Add more conditions
//    Firebase.crashlytics.log("Weather Item = $weatherDescription")
    if (weatherDescription == null) return false
    if (!isWithinWeatherIdRange(weatherDescription.id)) return false

    return true
}

private fun isWithinWeatherIdRange(weatherID: Int?): Boolean {
    if (!acceptableIds.contains(weatherID)) return false
    return true
}
