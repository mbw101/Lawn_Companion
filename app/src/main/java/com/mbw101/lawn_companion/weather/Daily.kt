package com.mbw101.lawn_companion.weather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

const val HIGHEST_ACCEPTABLE_CHANCE_OF_RAIN = 25.0

data class Daily(
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
    val feels_like: FeelsLike?,

    @SerializedName("humidity")
    @Expose
    val humidity: Int,

    @SerializedName("moon_phase")
    @Expose
    val moon_phase: Double,

    @SerializedName("moonrise")
    @Expose
    val moonrise: Int,

    @SerializedName("moonset")
    @Expose
    val moonset: Int,

    @SerializedName("pop")
    @Expose
    val pop: Double,

    @SerializedName("pressure")
    @Expose
    val pressure: Int,

    @SerializedName("rain")
    @Expose
    val rain: Double,

    @SerializedName("snow")
    @Expose
    val snow: Double,

    @SerializedName("sunrise")
    @Expose
    val sunrise: Int,

    @SerializedName("sunset")
    @Expose
    val sunset: Int,

    @SerializedName("temp")
    @Expose
    val temp: Temp,

    @SerializedName("uvi")
    @Expose
    val uvi: Double,

    @SerializedName("weather")
    @Expose
    val weather: List<Weather>?,

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


fun isSingleDaySuitable(singleDayWeather: Daily): Boolean {
    if (isNullArguments(singleDayWeather)) return false

    if (!isWeatherDescriptionSuitable(singleDayWeather.weather!![0])) return false
    if (!isTemperatureSuitable(singleDayWeather.temp)) return false
    if (!isSuitableRainPercentage(singleDayWeather.pop)) return false

    return true
}

fun isSuitableRainPercentage(chanceOfRain: Double): Boolean {
    if (chanceOfRain > HIGHEST_ACCEPTABLE_CHANCE_OF_RAIN) return false
    return true
}

private fun isNullArguments(singleDayWeather: Daily): Boolean {
    if (singleDayWeather.weather == null) return true

    return false
}