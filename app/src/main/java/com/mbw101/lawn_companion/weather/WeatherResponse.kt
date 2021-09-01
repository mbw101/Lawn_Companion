package com.mbw101.lawn_companion.weather

data class WeatherResponse(val alerts: List<AlertsItem>?,
                           val current: Current,
                           val timezone: String = "",
                           val timezoneOffset: Int = 0,
                           val daily: List<DailyItem>?,
                           val lon: Double = 0.0,
                           val hourly: List<HourlyItem>?,
                           val minutely: List<MinutelyItem>?,
                           val lat: Double = 0.0)