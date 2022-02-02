package com.mbw101.lawn_companion

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mbw101.lawn_companion.weather.*
import org.junit.Assert.assertEquals
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
        var weatherDescription = Weather("05d", "Rain", 500, "light rain")
        var currentWeather = Current(0, 0.1, 1000, 12.0, 45, 0, 0, 0, 12.3,
        12.1, 0, listOf(weatherDescription), 0, 0.0, 0.0)

        assertEquals(isWeatherDescriptionSuitable(weatherDescription), false)
        assertEquals(isCurrentWeatherSuitable(currentWeather), false)

        // test good weather
        weatherDescription = Weather("05d", "Clouds", 801, "few clouds: 11-25%")
        currentWeather = Current(0, 0.1, 1000, 12.0, 45, 0, 0, 0, 16.0,
            12.1, 0, listOf(weatherDescription), 0, 0.0, 0.0)

        assertEquals(isWeatherDescriptionSuitable(weatherDescription), true)
        assertEquals(isCurrentWeatherSuitable(currentWeather), true)
    }

    @Test
    fun testSuitableForTemp() {
        var temperature = Temp(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        assertEquals(isTemperatureSuitable(temperature), false)

        temperature = Temp(12.0, 12.0, 26.0, 8.0, 8.0, 8.0)
        assertEquals(isTemperatureSuitable(temperature), true)
    }

    @Test
    fun testSuitableForFeelsLikeTemp() {
        val feelsLikeTemp = FeelsLike(0.0, 0.0, 0.0, 0.0)
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
        assertEquals(isSuitableRainPercentage(0.0), true)
    }

    @Test
    fun testSuitableSingleDayWeather() {
        var weatherDescription = Weather("05d", "Rain", 500, "light rain")
        var temperature = Temp(0.0, 0.0, 0.0, 0.0, 0.0)
        var feelsLikeTemp = FeelsLike(0.0, 0.0, 0.0, 0.0)
        var singleDayWeather = Daily(0, 0.1, 1000, feelsLikeTemp, MIN_CUTTING_HUMIDITY,
            0.5, 0, 0, 0.0, 0, 0.0, 0.0, 0, 0, temperature,
        0.0, listOf(weatherDescription), 0, 0.0, 0.0)

        // test bad case
        assertEquals(isSingleDaySuitable(singleDayWeather), false)

        weatherDescription = Weather("05d","Rain", 500, "light rain")
        temperature = Temp(16.0, 12.0, 12.0, 12.0, 12.0)
        feelsLikeTemp = FeelsLike(16.0, 12.0, 12.0, 12.0)
        singleDayWeather = Daily(0, 0.1, 1000, feelsLikeTemp, MIN_CUTTING_HUMIDITY,
            0.5, 0, 0, 0.0, 0, 0.0, 0.0, 0, 0, temperature,
            0.0, listOf(weatherDescription), 0, 0.0, 0.0)

        assertEquals(isSingleDaySuitable(singleDayWeather), false)

        weatherDescription = Weather("05d","Sunny", 800, "clear")
        assertEquals(isWeatherDescriptionSuitable(weatherDescription), true)
        temperature = Temp(16.0, 12.0, 12.0, 12.0, 12.0, 12.0)
        assertEquals(isTemperatureSuitable(temperature), true)
        feelsLikeTemp = FeelsLike(16.0, 16.0, 16.0, 16.0)
        assertEquals(isFeelsLikeSuitable(feelsLikeTemp), true)
        singleDayWeather = Daily(0, 0.1, 1000, feelsLikeTemp, MIN_CUTTING_HUMIDITY,
            0.5, 0, 0, 0.0, 0, 0.0, 0.0, 0, 0, temperature,
            0.0, listOf(weatherDescription), 0, 0.0, 0.0)
        assertEquals(isSingleDaySuitable(singleDayWeather), true)
    }
}