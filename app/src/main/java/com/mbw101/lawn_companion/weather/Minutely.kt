package com.mbw101.lawn_companion.weather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Minutely(
    @SerializedName("dt")
    @Expose
    val dt: Int,

    @SerializedName("precipitation")
    @Expose
    val precipitation: Int
)