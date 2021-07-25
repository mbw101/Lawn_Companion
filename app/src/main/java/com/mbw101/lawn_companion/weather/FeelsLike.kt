package com.mbw101.lawn_companion.weather

data class FeelsLike(val eve: Double = 0.0,
                     val night: Double = 0.0,
                     val day: Double = 0.0,
                     val morn: Double = 0.0)

fun isFeelsLikeSuitable(feelsLikeTemp: FeelsLike): Boolean {
    if (!isWithinRange(feelsLikeTemp.morn) || !isWithinRange(feelsLikeTemp.day) ||
            !isWithinRange(feelsLikeTemp.eve)) {
        return false
    }

    // TODO: Add range checks in the future
    return true
}

private fun isWithinRange(temp: Double): Boolean {
    if (temp < MIN_CUTTING_TEMPERATURE_CELSIUS || temp > MAX_CUTTING_TEMPERATURE_CELSIUS) {
        return false
    }

    return true
}