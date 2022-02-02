package com.mbw101.lawn_companion.weather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("alerts")
    @Expose
    val alerts: List<AlertsItem>?,

    @SerializedName("lat")
    @Expose
    val lat: Double,

    @SerializedName("lon")
    @Expose
    val lon: Double,

    @SerializedName("timezone")
    @Expose
    val timezone: String,

    @SerializedName("timezone_offset")
    @Expose
    val timezone_offset: Int,

    @SerializedName("current")
    @Expose
    val current: Current,

    @SerializedName("minutely")
    @Expose
    val minutely: List<Minutely>,

    @SerializedName("hourly")
    @Expose
    val hourly: List<Hourly>,

    @SerializedName("daily")
    @Expose
    val daily: List<Daily>
)