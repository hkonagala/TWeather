package com.twitter.challenge.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.twitter.challenge.R
import com.twitter.challenge.data.WeatherRepository
import com.twitter.challenge.data.WeatherViewModel
import com.twitter.challenge.data.api.TWeatherApi
import com.twitter.challenge.util.TemperatureConverter.processTemperature
import kotlinx.android.synthetic.main.empty_container.*
import kotlinx.android.synthetic.main.fragment_current_weather.*
import kotlinx.android.synthetic.main.weather_container.*

/**
 * Parent fragment in Navigation graph that displays current weather on app launch.
 */
class CurrentWeatherFragment : Fragment() {

    private lateinit var repository: WeatherRepository
    private lateinit var viewModel: WeatherViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_current_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = WeatherRepository(TWeatherApi.create())
        viewModel = ViewModelProvider(
                requireActivity(),
                WeatherViewModel.FACTORY(repository)
        ).get(WeatherViewModel::class.java)

        // load weather on app launch
        viewModel.getCurrentWeather()

        setupObservers()
        setupClickListeners(view)
    }

    // Livedata observers for populating UI
    private fun setupObservers() {

        // load weather data if response = OK
        viewModel.weather.observe(viewLifecycleOwner, Observer { tWeather ->
            weatherContainer.visibility = View.VISIBLE
            emptyContainer.visibility = View.GONE

            temperature_label_tv.text = getString(R.string.current_temperature)
            windSpeed_label_tv.text = getString(R.string.wind_speed)
            // load data
            if (tWeather.clouds.cloudiness > 50) {
                cloudiness_image.setImageResource(R.drawable.ic_cloud_black_24dp)
            } else {
                cloudiness_image.setImageResource(R.drawable.ic_wb_sunny_black_24dp)
            }
            location_name_tv.text = tWeather.name
            temperature_tv.text = processTemperature(tWeather.weather.temp)
            wind_speed_tv.text = "${tWeather.wind.speed} meter/sec"
        })

        // show loading spinner
        viewModel.spinner.observe(viewLifecycleOwner, Observer { value ->
            value.let { show ->
                spinner.visibility = if (show) View.VISIBLE else View.GONE
            }
        })

        // show error message and retry button if api call fails
        viewModel.apiError.observe(viewLifecycleOwner, Observer { error ->
            weatherContainer.visibility = View.GONE
            emptyContainer.visibility = View.VISIBLE
            error_message_tv.text = error
        })
    }

    private fun setupClickListeners(view: View) {
        fetch_weather_button.setOnClickListener {
            viewModel.getWeatherForNDays(5)
            // Navigation component: redirect to child fragment on click event
            Navigation.findNavController(view).navigate(R.id.fetchWeatherAction)
        }

        // handle retry action  - fetch current weather
        retry_button.setOnClickListener {
            viewModel.getCurrentWeather()
            viewModel.onRetryShown()
            emptyContainer.visibility = View.GONE
        }
    }
}