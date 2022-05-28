package com.amartya.weather.repositories

import android.location.Location
import com.amartya.weather.api.RetrofitClient
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

/**
 * All API access codes and DB access codes should be written here
 */
object WeatherRepository {

    /**
     * Get the current weather forecast for a particular location
     *
     * params
     * `location`: `Location`
     */
    suspend fun getWeather(location: Location) = withContext(IO) {
        RetrofitClient.getInstance().getForecast(
            latLng = "${location.latitude},${location.longitude}"
        )
    }
}