package com.mbw101.lawn_companion.weather

import com.mbw101.lawn_companion.BuildConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-17
 */

const val LAT_LON_URL = "https://api.openweathermap.org/data/2.5/onecall?lat={lat_part}&lon={lon_part}&exclude={exclude_part}&appid={API_key}"

interface WeatherService {
    companion object {
        const val BASE_URL = "https://api.openweathermap.org"
    }

    @GET("/data/2.5/onecall") // /data/2.5/weather
    suspend fun getWeather(@Query("lat") lat: Float, @Query("lon") lon: Float, @Query("appid") apiID: String = BuildConfig.OPEN_WEATHER_MAP_API_KEY,
        @Query("units") unitsOfMeasurement: String = "metric"): Response<WeatherResponse> // Response<WeatherResponse>
}