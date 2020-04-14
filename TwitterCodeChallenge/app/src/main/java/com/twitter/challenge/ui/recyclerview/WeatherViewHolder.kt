package com.twitter.challenge.ui.recyclerview

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.twitter.challenge.R
import com.twitter.challenge.data.model.TWeather
import com.twitter.challenge.util.TemperatureConverter

class WeatherViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val icon: ImageView = view.findViewById(R.id.icon_imgv)
    private val name: TextView = view.findViewById(R.id.name_tv)
    private val temperatureLabel: TextView = view.findViewById(R.id.tempLabel_tv)
    private val temperature: TextView = view.findViewById(R.id.temp_tv)
    private val pressureLabel: TextView = view.findViewById(R.id.pressureLabel_tv)
    private val pressure: TextView = view.findViewById(R.id.pressure_tv)
    private val humidityLabel: TextView = view.findViewById(R.id.humidityLable_tv)
    private val humidity: TextView = view.findViewById(R.id.humidity_tv)

    private var weather: TWeather? = null

    fun bind(weather: TWeather?) {
        if (weather != null) {
            loadData(weather)
        }
    }

    private fun loadData(_weather: TWeather) {
        val resources = itemView.resources
        this.weather = _weather

        if (_weather.clouds.cloudiness > 50) {
            icon.setImageResource(R.drawable.ic_cloud_black_24dp)
        } else {
            icon.setImageResource(R.drawable.ic_wb_sunny_black_24dp)
        }
        name.text = _weather.name
        temperatureLabel.text = resources.getString(R.string.temperature)
        temperature.text = TemperatureConverter.processTemperature(_weather.weather.temp)
        pressureLabel.text = resources.getString(R.string.pressure)
        pressure.text = "${_weather.weather.pressure} hPa"
        humidityLabel.text = resources.getString(R.string.humidity)
        humidity.text = "${_weather.weather.humidity} %"
    }
}