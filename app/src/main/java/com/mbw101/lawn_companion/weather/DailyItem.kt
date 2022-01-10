package com.mbw101.lawn_companion.weather

const val HIGHEST_ACCEPTABLE_CHANCE_OF_RAIN = 25.0

data class DailyItem(val rain: Double = 0.0,
                     val sunrise: Int = 0,
                     val temp: Temp,
                     val uvi: Double = 0.0,
                     val pressure: Int = 0,
                     val clouds: Int = 0,
                     val feelsLike: FeelsLike?, // FeelsLike?
                     val windGust: Double = 0.0,
                     val dt: Int = 0,
                     val pop: Double = 0.0,
                     val windDeg: Int = 0,
                     val dewPoint: Double = 0.0,
                     val sunset: Int = 0,
                     val weather: List<WeatherItem>?,
                     val humidity: Int = 0,
                     val windSpeed: Double = 0.0)

fun isSingleDaySuitable(singleDayWeather: DailyItem): Boolean {
    if (isNullArguments(singleDayWeather)) return false

    if (!isWeatherDescriptionSuitable(singleDayWeather.weather!![0])) return false
    if (!isTemperatureSuitable(singleDayWeather.temp)) return false
    if (!isSuitableRainPercentage(singleDayWeather.pop)) return false

    return true
}

fun isSuitableRainPercentage(chanceOfRain: Double): Boolean {
    if (chanceOfRain > HIGHEST_ACCEPTABLE_CHANCE_OF_RAIN) return false
    return true
}

private fun isNullArguments(singleDayWeather: DailyItem): Boolean {
    if (singleDayWeather.weather == null) return true

    return false
}