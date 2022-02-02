package com.mbw101.lawn_companion.weather

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AlertsItem(
    @SerializedName("start")
    @Expose
    val start: Int = 0,

    @SerializedName("description")
    @Expose
    val description: String = "",

    @SerializedName("senderName")
    @Expose
    val senderName: String = "",

    @SerializedName("end")
    @Expose
    val end: Int = 0,

    @SerializedName("event")
    @Expose
    val event: String = "",

    @SerializedName("tags")
    @Expose
    val tags: List<String>?)