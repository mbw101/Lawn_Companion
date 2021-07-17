package com.mbw101.lawn_companion.weather

import com.google.gson.annotations.SerializedName

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-13
 */

const val HIGHEST_ACCEPTABLE_CHANCE_OF_RAIN = 25.0f

data class SingleDayWeather (
    @field:SerializedName("dt")
    val timeOfRetrievalInMillis: Long? = null, // Time of data calculation, unix, UTC

    @field:SerializedName("sunrise")
    val sunrise: Long? = null,

    @field:SerializedName("sunset")
    val sunset: Long? = null,

    @field:SerializedName("temp")
    val temperature: Temperature? = null,

    @field:SerializedName("feels_like")
    val feelsLikeTemp: FeelsLikeTemp? = null,

    @field:SerializedName("humidity")
    val humidityPercentage: Int? = null,

    @field:SerializedName("weather")
    val weatherDescription: WeatherDescription? = null,

    @field:SerializedName("pop")
    val chanceOfRain: Float? = null,

    @field:SerializedName("uvi") // UV index
    val uvIndex: Int? = null,
)

fun isSingleDaySuitable(singleDayWeather: SingleDayWeather): Boolean {
    if (isNullArguments(singleDayWeather)) return false

    if (!isWeatherDescriptionSuitable(singleDayWeather.weatherDescription!!)) return false
    if (!isTemperatureSuitable(singleDayWeather.temperature!!)) return false
    if (!isFeelsLikeSuitable(singleDayWeather.feelsLikeTemp!!)) return false
    if (!isSuitableRainPercentage(singleDayWeather.chanceOfRain!!)) return false

    return true
}

fun isSuitableRainPercentage(chanceOfRain: Float): Boolean {
    if (chanceOfRain > HIGHEST_ACCEPTABLE_CHANCE_OF_RAIN) return false
    return true
}

private fun isNullArguments(singleDayWeather: SingleDayWeather): Boolean {
    if (singleDayWeather.weatherDescription == null
        || singleDayWeather.temperature == null
        || singleDayWeather.feelsLikeTemp == null
        || singleDayWeather.chanceOfRain == null) return true
    return false
}