package com.mbw101.lawn_companion.weather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-13
 */

const val MIN_CUTTING_TEMPERATURE_CELSIUS = 10.0
const val MAX_CUTTING_TEMPERATURE_CELSIUS = 28.0

data class Temp(
    @SerializedName("min")
    @Expose
    val min: Double = 0.0,

    @SerializedName("max")
    @Expose
    val max: Double = 0.0,

    @SerializedName("eve")
    @Expose
    val eve: Double = 0.0,

    @SerializedName("night")
    @Expose
    val night: Double = 0.0,

    @SerializedName("day")
    @Expose
    val day: Double = 0.0,

    @SerializedName("morn")
    @Expose
    val morn: Double = 0.0)

fun isTemperatureSuitable(temperature: Temp): Boolean {
    if (!isWithinDayTempRange(temperature.day)
        && !isWithinEveningTemp(temperature.eve)) return false

    return true
}

fun isWithinDayTempRange(dayTemp: Double): Boolean {
    if (dayTemp < MIN_CUTTING_TEMPERATURE_CELSIUS
        || dayTemp > MAX_CUTTING_TEMPERATURE_CELSIUS) {
        return false
    }
    return true
}

fun isWithinEveningTemp(eveningTemp: Double): Boolean {
    if (eveningTemp < MIN_CUTTING_TEMPERATURE_CELSIUS
        || eveningTemp > MAX_CUTTING_TEMPERATURE_CELSIUS) {
        return false
    }
    return true
}