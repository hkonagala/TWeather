package com.twitter.challenge.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.twitter.challenge.R
import com.twitter.challenge.data.WeatherRepository
import com.twitter.challenge.data.WeatherViewModel
import com.twitter.challenge.data.api.TWeatherApi
import com.twitter.challenge.data.model.TWeather
import com.twitter.challenge.ui.recyclerview.WeatherAdapter
import com.twitter.challenge.util.TemperatureConverter
import kotlinx.android.synthetic.main.fragment_future_weather.*

/**
 * Child fragment that displays weather for next N days and SD for temperatures
 */
class FutureWeatherFragment : Fragment() {

    private lateinit var repository: WeatherRepository
    private lateinit var viewModel: WeatherViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_future_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = WeatherRepository(TWeatherApi.create())
        viewModel = ViewModelProvider(
                requireActivity(),
                WeatherViewModel.FACTORY(repository)
        ).get(WeatherViewModel::class.java)

        setupObservers()
    }

    private fun setupObservers() {
        // livedata observer for displaying weather for next N days if response = OK
        viewModel.tWeatherList.observe(viewLifecycleOwner, Observer { weatherList ->
            Log.d("FutureWeatherFragment", "list: ${weatherList?.size}")

            // set up recyclerview adapter
            initAdapter(weatherList)

            emptyList.visibility = View.GONE
            weatherList_rv.visibility = View.VISIBLE
        })

        // livedata observer for calculating / showing SD
        viewModel.temperatureList.observe(viewLifecycleOwner, Observer {
            standard_deviation_label_tv.visibility = View.VISIBLE
            standard_deviation_tv.visibility = View.VISIBLE
            standard_deviation_label_tv.text = getString(R.string.standard_deviation_of_temperatures)
            standard_deviation_tv.text = TemperatureConverter.calculateSD(it.toFloatArray()).toString()
        })

        // livedata for loading UI
        viewModel.spinner.observe(viewLifecycleOwner, Observer { value ->
            value.let { show ->
                spinner.visibility = if (show) View.VISIBLE else View.GONE
            }
        })

        // livedata for error handling
        viewModel.apiError.observe(viewLifecycleOwner, Observer { msg ->
            emptyList.visibility = View.VISIBLE
            weatherList_rv.visibility = View.GONE
            standard_deviation_label_tv.visibility = View.GONE
            standard_deviation_tv.visibility = View.GONE
            emptyList.text = getString(R.string.no_results) + " - " + msg
        })
    }

    private fun initAdapter(weatherList: List<TWeather>) {
        weatherList_rv.layoutManager = LinearLayoutManager(requireContext())
        weatherList_rv.adapter = WeatherAdapter(requireContext(), weatherList)
    }
}
