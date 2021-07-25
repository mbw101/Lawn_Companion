package com.mbw101.lawn_companion.weather

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-13
 */

const val MIN_CUTTING_TEMPERATURE_CELSIUS = 10.0
const val MAX_CUTTING_TEMPERATURE_CELSIUS = 28.0

data class Temp(val min: Double = 0.0,
                val max: Double = 0.0,
                val eve: Double = 0.0,
                val night: Double = 0.0,
                val day: Double = 0.0,
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