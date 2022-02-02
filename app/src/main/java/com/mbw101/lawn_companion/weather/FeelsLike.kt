package com.mbw101.lawn_companion.weather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FeelsLike(
    @SerializedName("day")
    @Expose
    val day: Double,

    @SerializedName("eve")
    @Expose
    val eve: Double,

    @SerializedName("morn")
    @Expose
    val morn: Double,

    @SerializedName("night")
    @Expose
    val night: Double
)

fun isFeelsLikeSuitable(feelsLikeTemp: FeelsLike): Boolean {
    if (!isWithinRange(feelsLikeTemp.morn) || !isWithinRange(feelsLikeTemp.day) ||
            !isWithinRange(feelsLikeTemp.eve)) {
        return false
    }

    return true
}

private fun isWithinRange(temp: Double): Boolean {
    if (temp < MIN_CUTTING_TEMPERATURE_CELSIUS || temp > MAX_CUTTING_TEMPERATURE_CELSIUS) {
        return false
    }

    return true
}