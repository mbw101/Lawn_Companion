package com.mbw101.lawn_companion.weather

import com.google.gson.annotations.SerializedName

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-13
 */

const val MIN_CUTTING_TEMPERATURE_CELSIUS = 10.0f
const val MAX_CUTTING_TEMPERATURE_CELSIUS = 28.0f

data class Temperature (
    @field:SerializedName("day")
    val dayTemp: Float? = null,

    @field:SerializedName("min")
    val minTemp: Float? = null,

    @field:SerializedName("max")
    val maxTemp: Float? = null,

    @field:SerializedName("night")
    val nightTemp: Float? = null,

    @field:SerializedName("eve")
    val eveningTemp: Float? = null,

    @field:SerializedName("morn")
    val morningTemp: Float? = null
)

fun isTemperatureSuitable(temperature: Temperature): Boolean {
    if (isNullArguments(temperature)) return false
    if (!isWithinDayTempRange(temperature.dayTemp!!)
        && !isWithinEveningTemp(temperature.eveningTemp!!)) return false

    return true
}

private fun isNullArguments(temperature: Temperature): Boolean {
    if (temperature.dayTemp == null || temperature.minTemp == null ||
        temperature.maxTemp == null || temperature.nightTemp == null ||
        temperature.eveningTemp == null || temperature.morningTemp == null) {
        return true
    }
    return false
}

fun isWithinDayTempRange(dayTemp: Float): Boolean {
    if (dayTemp < MIN_CUTTING_TEMPERATURE_CELSIUS
        || dayTemp > MAX_CUTTING_TEMPERATURE_CELSIUS) {
        return false
    }
    return true
}

fun isWithinEveningTemp(eveningTemp: Float): Boolean {
    if (eveningTemp < MIN_CUTTING_TEMPERATURE_CELSIUS
        || eveningTemp > MAX_CUTTING_TEMPERATURE_CELSIUS) {
        return false
    }
    return true
}