package com.mbw101.lawn_companion.weather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Hourly(
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

    @SerializedName("pop")
    @Expose
    val pop: Double,

    @SerializedName("pressure")
    @Expose
    val pressure: Int,

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