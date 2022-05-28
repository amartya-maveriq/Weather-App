package com.amartya.weather.repositories

import android.location.Location
import com.amartya.weather.api.ApiService
import com.amartya.weather.db.LocationDao
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * All API access codes and DB access codes should be written here
 */
class WeatherRepository @Inject constructor(
    private val locationDao: LocationDao,
    private val apiService: ApiService
) {

    /**
     * Get the current weather forecast for a particular location
     *
     * params
     * `location`: `Location`
     */
    suspend fun getWeather(location: Location) = withContext(IO) {
        apiService.getForecast(
            latLng = "${location.latitude},${location.longitude}"
        )
    }

    /**
     * Get the list of cities marked as favorites
     */
    suspend fun getFavoriteCities() = withContext(IO) {
        locationDao.getAll()
    }

    /**
     * Search for a city
     */
    suspend fun searchCity(text: String) = withContext(IO) {
        apiService.searchLocation(text)
    }
}