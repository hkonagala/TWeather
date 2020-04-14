package com.twitter.challenge.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data models for TWeather response
 */
data class TWeather(
        var coord: Coord,
        var weather: Weather,
        var wind: Wind,
        var rain: Rain,
        var clouds: Clouds,
        var name: String
)

/******************* Helper data classes for TWeather *****************/

data class Coord(var lon: Double, var lat: Double)

data class Weather(var temp: Float, var pressure: Int, var humidity: Int)
data class Wind(var speed: Double, var deg: Int)
data class Rain(@SerializedName("3h") var _3h: Int)
data class Clouds(var cloudiness: Int)

