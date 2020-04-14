package com.twitter.challenge.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twitter.challenge.data.model.TWeather
import com.twitter.challenge.util.singleArgViewModelFactory
import kotlinx.coroutines.launch

/**
 * ViewModel implementation for handling user interactions.
 */
class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    // Livedata for loading current weather
    val weather: LiveData<TWeather>
        get() = repository.weather

    // Livedata for loading weather for next N days
    val tWeatherList: LiveData<List<TWeather>>
        get() = repository.tWeatherList

    // Livedata for calculating SD of temperatures
    val temperatureList: LiveData<List<Float>>
        get() = repository.temperatureList

    // Livedata for handling when to UI loading
    private val _spinner = MutableLiveData<Boolean>(false)
    val spinner: LiveData<Boolean>
        get() = _spinner

    // Livedata for error handling
    private val _apiError = MutableLiveData<String?>()
    val apiError: LiveData<String?>
        get() = _apiError

    fun getCurrentWeather() = launchDataLoad {
        repository.loadCurrentWeather()
    }

    fun getWeatherForNDays(days: Int) = launchDataLoad {
        repository.fetchWeatherForNextNDays(days)
    }

    // Helper function to handle common UI updates
    private fun launchDataLoad(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                _spinner.postValue(true)
                block()
            } catch (e: Exception) {
                _apiError.postValue("${e.message}: ${e.cause} ")
            } finally {
                _spinner.postValue(false)
            }
        }
    }

    fun onRetryShown() {
        _apiError.value = null
    }

    companion object {
        val FACTORY = singleArgViewModelFactory(::WeatherViewModel)
    }

}