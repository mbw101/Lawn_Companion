package com.mbw101.lawn_companion

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mbw101.lawn_companion.weather.CurrentWeather
import com.mbw101.lawn_companion.weather.WeatherDescription
import com.mbw101.lawn_companion.weather.isCurrentWeatherSuitable
import com.mbw101.lawn_companion.weather.isWeatherDescriptionSuitable
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-07-01
 */

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class WeatherTests {

    @Test
    fun testSuitableFunctionForCurrentWeather() {
        var weatherDescription = WeatherDescription("Rain", "light rain", 500)
        var currentWeather = CurrentWeather(null, 10000, 10000, 17,
            17, 92, 5, weatherDescription)

        assertEquals(isWeatherDescriptionSuitable(weatherDescription), false)
        assertEquals(isCurrentWeatherSuitable(currentWeather), false)

        // test good weather
        weatherDescription = WeatherDescription("Clouds", "few clouds: 11-25%", 801)
        currentWeather = CurrentWeather(null, 10000, 10000, 17,
            17, 63, 5, weatherDescription)

        assertEquals(isWeatherDescriptionSuitable(weatherDescription), true)
        assertEquals(isCurrentWeatherSuitable(currentWeather), true)
    }
}