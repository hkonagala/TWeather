package com.twitter.challenge.data.api

import com.twitter.challenge.data.model.TWeather
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit: API communication setup
 */
interface TWeatherApi {

    @GET("current.json")
    suspend fun getCurrentWeather(): TWeather

    @GET("future_{day}.json")
    suspend fun fetchWeatherForNthDay(@Path("day") day: Int): TWeather

    companion object {

        // Api calls:
        // https://twitter-code-challenge.s3.amazonaws.com/current.json
        // https://twitter-code-challenge.s3.amazonaws.com/future_1.json

        private const val BASE_URL = "https://twitter-code-challenge.s3.amazonaws.com/"

        fun create(): TWeatherApi = buildRetrofitApi(BASE_URL.toHttpUrlOrNull()!!)

        private fun buildRetrofitApi(httpUrl: HttpUrl): TWeatherApi {
            val client = OkHttpClient
                    .Builder()
                    .addInterceptor(
                            HttpLoggingInterceptor().apply {
                                // Log http request & response
                                level = HttpLoggingInterceptor.Level.BODY
                            }
                    )
                    .build()
            return Retrofit
                    .Builder()
                    .baseUrl(httpUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(TWeatherApi::class.java)
        }

    }
}