package com.mbw101.lawn_companion

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mbw101.lawn_companion.database.LawnLocation
import com.mbw101.lawn_companion.database.LawnLocationRepository
import com.mbw101.lawn_companion.database.setupLawnLocationRepository
import com.mbw101.lawn_companion.notifications.AlarmReceiver
import com.mbw101.lawn_companion.ui.MyApplication
import com.mbw101.lawn_companion.weather.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.robolectric.annotation.Config
import retrofit2.Response

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class WeatherServiceTest {
    private lateinit var lawnLocationRepository: LawnLocationRepository

    @Test
    fun testWeatherServiceCall() {
        // insert coordiantes into DB
        lawnLocationRepository = setupLawnLocationRepository(MyApplication.applicationContext())
        runBlocking {
            lawnLocationRepository.addLocation(LawnLocation(42.3, 42.3))
        }

        // create weather service mock
        val weatherService = mock(WeatherService::class.java)
        val weatherResponse = WeatherResponse(alerts=null,
            current= Current(sunrise=1641789522, temp=15.11, visibility=10000, uvi=0.53, pressure=1007, clouds=0, feelsLike=0.0, windGust=0.0, dt=1641813843, sunset=1641823038, weather=listOf(WeatherItem(icon="01d", description="clear sky", main="Clear", id=800)), humidity=33, windSpeed=0.0), // end of Current weather
            timezone="Asia/Tbilisi", timezoneOffset=0,
            daily=
            listOf(
                DailyItem(rain=0.0, sunrise=1641789522, temp=Temp(min=9.32, max=15.11, eve=11.88, night=9.32, day=14.6, morn=9.58), uvi=0.9, pressure=1007, clouds=40, feelsLike=null, windGust=0.0, dt=1641805200, pop=0.18, windDeg=0, dewPoint=0.0, sunset=1641823038, weather= listOf(WeatherItem(icon="03d", description="scattered clouds", main="Clouds", id=802)), humidity=38, windSpeed=0.0),
                DailyItem(rain=0.0, sunrise=1641875905, temp=Temp(min=8.04, max=13.22, eve=11.3, night=9.76, day=12.39, morn=8.22), uvi=1.08, pressure=1014, clouds=99, feelsLike=null, windGust=0.0, dt=1641891600, pop=0.0, windDeg=0, dewPoint=0.0, sunset=1641909502, weather=listOf(WeatherItem(icon="04d", description="overcast clouds", main="Clouds", id=804)), humidity=54, windSpeed=0.0),
                DailyItem(rain=0.0, sunrise=1641962287, temp=Temp(min=8.31, max=12.31, eve=10.64, night=9.66, day=11.47, morn=8.62), uvi=0.64, pressure=1014, clouds=100, feelsLike=null, windGust=0.0, dt=1641978000, pop=0.25, windDeg=0, dewPoint=0.0, sunset=1641995968, weather=listOf(WeatherItem(icon="04d", description="overcast clouds", main="Clouds", id=804)), humidity=59, windSpeed=0.0),
                DailyItem(rain=16.47, sunrise=1642048665, temp=Temp(min=2.49, max=9.65, eve=3.49, night=2.49, day=3.7, morn=5.83), uvi=0.19, pressure=1024, clouds=100, feelsLike=null, windGust=0.0, dt=1642064400, pop=1.0, windDeg=0, dewPoint=0.0, sunset=1642082434, weather=listOf(WeatherItem(icon="10d", description="moderate rain", main="Rain", id=501)), humidity=97, windSpeed=0.0),
                DailyItem(rain=0.0, sunrise=1642135042, temp=Temp(min=1.07, max=5.69, eve=3.73, night=2.28, day=5.04, morn=1.07), uvi=1.4, pressure=1018, clouds=99, feelsLike=null, windGust=0.0, dt=1642150800, pop=0.0, windDeg=0, dewPoint=0.0, sunset=1642168902, weather=listOf(WeatherItem(icon="04d", description="overcast clouds", main="Clouds", id=804)), humidity=63, windSpeed=0.0),
                DailyItem(rain=6.23, sunrise=1642221417, temp=Temp(min=1.64, max=2.76, eve=2.75, night=2.72, day=2.32, morn=1.64), uvi=2.0, pressure=1017, clouds=100, feelsLike=null, windGust=0.0, dt=1642237200, pop=1.0, windDeg=0, dewPoint=0.0, sunset=1642255371, weather=listOf(WeatherItem(icon="13d", description="rain and snow", main="Snow", id=616)), humidity=99, windSpeed=0.0),
                DailyItem(rain=12.2, sunrise=1642307789, temp=Temp(min=1.07, max=2.45, eve=2.01, night=1.07, day=1.41, morn=2.23), uvi=2.0, pressure=1017, clouds=100, feelsLike=null, windGust=0.0, dt=1642323600, pop=0.99, windDeg=0, dewPoint=0.0, sunset=1642341841, weather=listOf(WeatherItem(icon="13d", description="rain and snow", main="Snow", id=616)), humidity=100, windSpeed=0.0),
                DailyItem(rain=0.0, sunrise=1642394159, temp=Temp(min=-0.63, max=2.99, eve=0.43, night=-0.63, day=2.46, morn=0.83), uvi=2.0, pressure=1024, clouds=88, feelsLike=null, windGust=0.0, dt=1642410000, pop=0.42, windDeg=0, dewPoint=0.0, sunset=1642428312, weather=listOf(WeatherItem(icon="13d", description="light snow", main="Snow", id=600)), humidity=92, windSpeed=0.0)
            ), // end of daily
            lon=42.3,
            hourly=null,
            minutely=null,
            lat=42.3)

        val httpResponse: Response<WeatherResponse> = Response.success(200, weatherResponse)
        val context = MyApplication.applicationContext()
        var returnData: Response<WeatherResponse>? = null
        runBlocking {
            launch (Dispatchers.IO) {
                Mockito.`when`(weatherService.getWeather(weatherResponse.lat, weatherResponse.lon)).thenReturn(httpResponse)
                returnData = AlarmReceiver.callWeatherAPI(context, weatherService)
            }
        }

        assertNotNull(returnData)
        assertEquals(returnData, httpResponse)
        assertEquals(returnData!!.isSuccessful, true)
        assertNotNull(returnData!!.body())
        assertEquals(returnData!!.body(), weatherResponse)
    }
}