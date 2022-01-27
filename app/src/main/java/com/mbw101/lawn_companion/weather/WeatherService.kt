package com.mbw101.lawn_companion.weather

import com.mbw101.lawn_companion.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-17
 */

interface WeatherService {
    companion object {
        const val BASE_URL = "https://api.openweathermap.org"
    }

    @GET("/data/2.5/onecall") // /data/2.5/weather
    fun getWeather(@Query("lat") lat: Double, @Query("lon") lon: Double, @Query("appid") apiID: String = BuildConfig.OPEN_WEATHER_MAP_API_KEY,
        @Query("units") unitsOfMeasurement: String = "metric"): Call<WeatherResponse>
}

interface GetWeatherListener {
    fun onSuccess(response: WeatherResponse)
    fun onFailure(errorMessage: String)
}