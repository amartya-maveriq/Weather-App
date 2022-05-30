package com.amartya.weather.repositories

import android.location.Location
import com.amartya.weather.api.ApiService
import com.amartya.weather.db.HomePageDao
import com.amartya.weather.db.LocationDao
import com.amartya.weather.models.HomePage
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * All API access codes and DB access codes should be written here
 */
class WeatherRepository @Inject constructor(
    private val locationDao: LocationDao,
    private val homePageDao: HomePageDao,
    private val apiService: ApiService
) {

    /**
     * Get last saved home page data
     */
    suspend fun getWeatherData(name: String) = withContext(IO) {
        homePageDao.getAll(name)
    }

    /**
     * Save latest to DB
     */
    suspend fun saveWeatherData(homePage: HomePage) = withContext(IO) {
        homePageDao.insert(homePage)
    }

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
     * Get the current weather forecast for a particular location name
     *
     * params
     * `locationName`: `String`
     */
    suspend fun getWeather(locationName: String) = withContext(IO) {
        apiService.getForecast(
            latLng = locationName
        )
    }

    /**
     * Get the list of cities marked as favorites
     */
    suspend fun getFavoriteCities() = withContext(IO) {
        locationDao.getAll()
    }

    /**
     * Add to list of favorite cities
     */
    suspend fun addToFavoriteCities(location: com.amartya.weather.models.Location) = withContext(IO) {
        locationDao.addNew(location)
    }

    /**
     * Delete from fav cities
     */
    suspend fun deleteFromFavCities(location: com.amartya.weather.models.Location) = withContext(IO) {
        locationDao.delete(location)
    }

    /**
     * Search for a city
     */
    suspend fun searchCity(text: String) = withContext(IO) {
        apiService.searchLocation(text)
    }
}