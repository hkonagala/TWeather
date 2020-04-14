package com.twitter.challenge.data

import androidx.lifecycle.MutableLiveData
import com.twitter.challenge.data.api.TWeatherApi
import com.twitter.challenge.data.model.TWeather
import kotlinx.coroutines.supervisorScope

/**
 * Repository implementation that returns current weather, weather for next N days and
 * standard deviation of the temperatures from network using kotlin coroutines and handles Exceptions.
 */
class WeatherRepository(var api: TWeatherApi) {

    val weather = MutableLiveData<TWeather>()
    val tWeatherList = MutableLiveData<List<TWeather>>()
    val temperatureList = MutableLiveData<List<Float>>()

    // Note: Retrofit automatically makes suspend functions main-safe so you can call them directly from Dispatchers.Main

    // error handling: https://proandroiddev.com/managing-exceptions-in-nested-coroutine-scopes-9f23fd85e61
    suspend fun loadCurrentWeather() {
        supervisorScope {
            try {
                weather.value = api.getCurrentWeather()
            } catch (error: Exception) {
                throw ApiError("Unable to fetch data", error)
            }
        }
    }

    suspend fun fetchWeatherForNextNDays(days: Int) {
        val list = mutableListOf<TWeather>()
        supervisorScope {
            try {
                for (x in 1..days) {
                    list.add(api.fetchWeatherForNthDay(x))
                }
                storeTemperatureList(list)
                tWeatherList.value = list
            } catch (error: Exception) {
                throw ApiError("Unable to fetch data", error)
            }
        }
    }

    private fun storeTemperatureList(list: MutableList<TWeather>) {
        val filteredTempList = mutableListOf<Float>()
        for (i in 0 until list.size) {
            filteredTempList.add(list[i].weather.temp)
        }
        temperatureList.value = filteredTempList
    }
}

class ApiError(message: String, cause: Exception?) : Exception(message, cause)