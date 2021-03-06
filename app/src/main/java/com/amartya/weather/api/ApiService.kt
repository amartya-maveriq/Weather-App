package com.amartya.weather.api

import com.amartya.weather.models.Location
import com.amartya.weather.models.Weather
import com.amartya.weather.utils.API_KEY
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("forecast.json?key=$API_KEY")
    fun getForecast(
        @Query("q") latLng: String,
        @Query("days") days: Int = 7
    ): Call<Weather>

    @GET("search.json?key=$API_KEY")
    fun searchLocation(@Query("q") text: String): Call<List<Location>>
}