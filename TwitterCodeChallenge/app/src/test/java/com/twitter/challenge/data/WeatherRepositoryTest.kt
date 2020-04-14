package com.twitter.challenge.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.whenever
import com.twitter.challenge.data.api.TWeatherApi
import com.twitter.challenge.data.model.Clouds
import com.twitter.challenge.data.model.Coord
import com.twitter.challenge.data.model.Rain
import com.twitter.challenge.data.model.TWeather
import com.twitter.challenge.data.model.Weather
import com.twitter.challenge.data.model.Wind
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations


class WeatherRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `when loadCurrentWeather returns valid result`() = runBlockingTest {
        val testContext = TestContext()
        whenever(testContext.service.getCurrentWeather()).thenReturn(TEST_WEATHER)

        testContext.subject.loadCurrentWeather()

        assertEquals(TEST_WEATHER, testContext.subject.weather.value)
    }

    @Test(expected = ApiError::class)
    fun `when loadCurrentWeather fails`() = runBlockingTest {
        val testContext = TestContext()
        doAnswer { throw Exception("Test Exception") }.`when`(testContext.service).getCurrentWeather()
        testContext.subject.loadCurrentWeather()

        val apiError = assertThrows(ApiError::class.java) { runBlockingTest { testContext.subject.loadCurrentWeather() } }

        assertEquals("Unable to fetch data", apiError.message)
    }

    @Test
    fun `when fetchWeatherForNextNDays returns valid data`() = runBlockingTest {
        val testContext = TestContext()
        whenever(testContext.service.fetchWeatherForNthDay(anyInt())).thenReturn(TEST_WEATHER)

        testContext.subject.fetchWeatherForNextNDays(5)

        val weatherList = testContext.subject.tWeatherList.value
        assertEquals(5, weatherList?.size)
        verify(testContext.service, times(5)).fetchWeatherForNthDay(anyInt())
    }

    @Test(expected = ApiError::class)
    fun `when fetchWeatherForNextNDays fails`() = runBlockingTest {
        val testContext = TestContext()
        doAnswer { throw Exception("Test Exception") }.`when`(testContext.service).fetchWeatherForNthDay(anyInt())
        testContext.subject.fetchWeatherForNextNDays(5)

        val apiError = assertThrows(ApiError::class.java) { runBlockingTest { testContext.subject.fetchWeatherForNextNDays(5) } }

        assertEquals("Unable to fetch data", apiError.message)
    }

    class TestContext {

        @Mock
        lateinit var service: TWeatherApi

        var subject: WeatherRepository

        init {
            MockitoAnnotations.initMocks(this)
            subject = WeatherRepository(service)
        }
    }

    companion object {

        val TEST_WEATHER = TWeather(
                coord = Coord(lon = -122.42, lat = 37.77),
                weather = Weather(temp = 16.83F, pressure = 1012, humidity = 12),
                wind = Wind(speed = 0.29, deg = 56),
                rain = Rain(_3h = 0),
                clouds = Clouds(cloudiness = 14),
                name = "San Francisco")

        val ERROR_RESPONSE = ApiError("Unauthorised", null)
    }
}