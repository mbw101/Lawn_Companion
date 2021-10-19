package com.mbw101.lawn_companion.weather

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-12
 */

const val MIN_CUTTING_HUMIDITY = 45
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
    if (current.weather == null) return false
    if (!isWeatherDescriptionSuitable(current.weather[0])) return false
    if (!isWithinDayTempRange(current.temp)) return false
    if (!isWithinHumidityRange(current.humidity)) return false

    // TODO: Implement UV Index range check

    return true
}

fun isWithinHumidityRange(humidity: Int): Boolean {
    if (humidity < MIN_CUTTING_HUMIDITY ||
        humidity > MAX_CUTTING_HUMIDITY) return false
    return true
}