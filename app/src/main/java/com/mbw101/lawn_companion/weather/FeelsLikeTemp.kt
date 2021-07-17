package com.mbw101.lawn_companion.weather

import com.google.gson.annotations.SerializedName

data class FeelsLikeTemp (
    @field:SerializedName("day")
    val feelsLikeDayTemp: Float? = null,

    @field:SerializedName("night")
    val feelsLikeNightTemp: Float? = null,

    @field:SerializedName("eve")
    val feelsLikeEveningTemp: Float? = null,

    @field:SerializedName("morn")
    val feelsLikeMorningTemp: Float? = null
)

fun isFeelsLikeSuitable(feelsLikeTemp: FeelsLikeTemp): Boolean {
    if (isNullArguments(feelsLikeTemp)) return false

    // TODO: Add range checks in the future
    return true
}

private fun isNullArguments(feelsLikeTemp: FeelsLikeTemp): Boolean {
    if (feelsLikeTemp.feelsLikeDayTemp == null || feelsLikeTemp.feelsLikeEveningTemp == null ||
        feelsLikeTemp.feelsLikeMorningTemp == null || feelsLikeTemp.feelsLikeNightTemp == null) {
        return true
    }
    return false
}