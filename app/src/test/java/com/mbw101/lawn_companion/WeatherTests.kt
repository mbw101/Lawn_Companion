package com.mbw101.lawn_companion

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mbw101.lawn_companion.weather.*
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

    @Test
    fun testSuitableForTemp() {
        var temperature = Temperature(null, null, null, null, null, null)
        assertEquals(isTemperatureSuitable(temperature), false)

        temperature = Temperature(12.0f, 12.0f, 26.0f, 8.0f, 8.0f, 8.0f)
        assertEquals(isTemperatureSuitable(temperature), true)
    }

    @Test
    fun testSuitableForFeelsLikeTemp() {
        var feelsLikeTemp = FeelsLikeTemp(null, null, null, null)
        assertEquals(isFeelsLikeSuitable(feelsLikeTemp), false)
    }

    @Test
    fun testHumidityRange() {
        assertEquals(isWithinHumidityRange(MIN_CUTTING_HUMIDITY-1), false)
        assertEquals(isWithinHumidityRange(MIN_CUTTING_HUMIDITY), true)
        assertEquals(isWithinHumidityRange(MIN_CUTTING_HUMIDITY+1), true)
        assertEquals(isWithinHumidityRange(MAX_CUTTING_HUMIDITY-1), true)
        assertEquals(isWithinHumidityRange(MAX_CUTTING_HUMIDITY), true)
        assertEquals(isWithinHumidityRange(MAX_CUTTING_HUMIDITY+1), false)
    }

    @Test
    fun testDayTempRange() {
        assertEquals(isWithinDayTempRange((MIN_CUTTING_TEMPERATURE_CELSIUS - 1)), false)
        assertEquals(isWithinDayTempRange((MIN_CUTTING_TEMPERATURE_CELSIUS)), true)
        assertEquals(isWithinDayTempRange((MIN_CUTTING_TEMPERATURE_CELSIUS + 1)), true)
        assertEquals(isWithinDayTempRange((MAX_CUTTING_TEMPERATURE_CELSIUS - 1)), true)
        assertEquals(isWithinDayTempRange((MAX_CUTTING_TEMPERATURE_CELSIUS)), true)
        assertEquals(isWithinDayTempRange((MAX_CUTTING_TEMPERATURE_CELSIUS + 1)), false)
    }

    @Test
    fun testSuitableRainPercentage() {
        assertEquals(isSuitableRainPercentage(HIGHEST_ACCEPTABLE_CHANCE_OF_RAIN - 1), true)
        assertEquals(isSuitableRainPercentage(HIGHEST_ACCEPTABLE_CHANCE_OF_RAIN), true)
        assertEquals(isSuitableRainPercentage(HIGHEST_ACCEPTABLE_CHANCE_OF_RAIN + 1), false)
        assertEquals(isSuitableRainPercentage(0.0f), true)
    }

    @Test
    fun testSuitableSingleDayWeather() {
        var weatherDescription = WeatherDescription("Rain", "light rain", 500)
        var temperature = Temperature(null, null, null, null, null)
        var feelsLikeTemp = FeelsLikeTemp(null, null, null, null)
        var singleDayWeather = SingleDayWeather(0, 10000, 10000, temperature,
            feelsLikeTemp, 16, weatherDescription, 0.0f, 0)

        // test bad case
        assertEquals(isSingleDaySuitable(singleDayWeather), false)

        weatherDescription = WeatherDescription("Rain", "light rain", 500)
        temperature = Temperature(16.0f, 12.0f, 12.0f, 12.0f, 12.0f)
        feelsLikeTemp = FeelsLikeTemp(16.0f, 12.0f, 12.0f, 12.0f)
        singleDayWeather = SingleDayWeather(0, 10000, 10000, temperature,
            feelsLikeTemp, 16, weatherDescription, 0.0f, 0)

        assertEquals(isSingleDaySuitable(singleDayWeather), false)

        weatherDescription = WeatherDescription("Sunny", "clear", 800)
        assertEquals(isWeatherDescriptionSuitable(weatherDescription), true)
        temperature = Temperature(16.0f, 12.0f, 12.0f, 12.0f, 12.0f, 12.0f)
        assertEquals(isTemperatureSuitable(temperature), true)
        feelsLikeTemp = FeelsLikeTemp(16.0f, 16.0f, 16.0f, 16.0f)
        assertEquals(isFeelsLikeSuitable(feelsLikeTemp), true)
        singleDayWeather = SingleDayWeather(0, 10000, 10000, temperature,
            feelsLikeTemp, MIN_CUTTING_HUMIDITY, weatherDescription, 0.0f, 0)
        assertEquals(isSingleDaySuitable(singleDayWeather), true)


//        singleDayWeather = SingleDayWeather(0, 10000, 10000, temperature,
//            feelsLikeTemp, 16, weatherDescription, HIGHEST_ACCEPTABLE_CHANCE_OF_RAIN, 0)
    }
}