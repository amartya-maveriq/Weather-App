package com.amartya.weather.viewmodels

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amartya.weather.models.Weather
import com.amartya.weather.repositories.WeatherRepository
import com.amartya.weather.sealed.UiState
import com.amartya.weather.utils.ERR_GENERIC
import com.amartya.weather.utils.logDebug
import com.amartya.weather.utils.logError
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

/**
 * Shared viewModel for MainActivity and it's fragments
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> = _location

    private val _exists = MutableLiveData<Boolean>()
    val exists: LiveData<Boolean> = _exists

    private val _weatherFlow = MutableStateFlow<UiState>(UiState.Idle)
    val weatherFlow: StateFlow<UiState> = _weatherFlow

    private val _citiesFlow = MutableStateFlow<UiState>(UiState.Idle)
    val citiesFlow: StateFlow<UiState> = _citiesFlow

    private val _searchResults = MutableLiveData<List<com.amartya.weather.models.Location>?>()
    val searchResults: LiveData<List<com.amartya.weather.models.Location>?> = _searchResults

    var selectedLocation = com.amartya.weather.models.Location(name = "")

    @SuppressLint("MissingPermission")
    fun getUserLocation(fusedLocationProviderClient: FusedLocationProviderClient) {
        viewModelScope.launch {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener {
                    if (it == null) {
                        logDebug("getUserLocation: null")
                    }
                    _location.postValue(it)
                }
        }
    }

    /**
     * Get the current weather forecast for a particular location
     */
    fun fetchWeather(location: Location) {
        viewModelScope.launch {
            _weatherFlow.value = UiState.Loading
            val api = weatherRepository.getWeather(location)
            api.enqueue(object : Callback<Weather> {
                override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                    _weatherFlow.value = UiState.Success(response.body())
                }

                override fun onFailure(call: Call<Weather>, t: Throwable) {
                    Log.e("MSG", "err: ${t.message}")
                    _weatherFlow.value = UiState.Error(t)
                }
            })
        }
    }

    /**
     * Get the current weather forecast for a particular location name
     */
    fun fetchWeather(locationName: String) {
        viewModelScope.launch {
            _weatherFlow.value = UiState.Loading
            val api = weatherRepository.getWeather(locationName)
            api.enqueue(object : Callback<Weather> {
                override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                    _weatherFlow.value = UiState.Success(response.body())
                }

                override fun onFailure(call: Call<Weather>, t: Throwable) {
                    Log.e("MSG", "err: ${t.message}")
                    _weatherFlow.value = UiState.Error(t)
                }
            })
        }
    }

    /**
     * Fetch list of favorite cities
     */
    fun getFavoriteCities() {
        viewModelScope.launch {
            runCatching {
                _citiesFlow.value = UiState.Success(weatherRepository.getFavoriteCities())
            }.onFailure {
                _citiesFlow.value = UiState.Error(it)
            }
        }
    }

    /**
     * Add to list of favorite cities
     */
    fun addToFavoriteCities(location: com.amartya.weather.models.Location) {
        viewModelScope.launch {
            runCatching {
                weatherRepository.addToFavoriteCities(location)
            }.onFailure {
                logError(it.message ?: ERR_GENERIC)
            }
        }
    }

    /**
     * Delete from fav cities
     */
    fun deleteFromFavCities(location: com.amartya.weather.models.Location) {
        viewModelScope.launch {
            runCatching {
                weatherRepository.deleteFromFavCities(location)
            }.onFailure {
                logError(it.message ?: ERR_GENERIC)
            }
        }
    }

    /**
     * Check if city exists in DB
     */
    fun checkForCity(locationName: String) {
        viewModelScope.launch {
            runCatching {
                _exists.postValue(
                    weatherRepository.getFavoriteCities().firstOrNull { it.name == locationName } != null)
            }
        }
    }

    /**
     * Search for a typed location
     */
    fun searchCity(text: String) {
        viewModelScope.launch {
            val api = weatherRepository.searchCity(text)
            api.enqueue(object : Callback<List<com.amartya.weather.models.Location>> {
                override fun onResponse(
                    call: Call<List<com.amartya.weather.models.Location>>,
                    response: Response<List<com.amartya.weather.models.Location>>
                ) {
                    _searchResults.postValue(response.body())
                }

                override fun onFailure(
                    call: Call<List<com.amartya.weather.models.Location>>,
                    t: Throwable
                ) {
                    _searchResults.postValue(null)
                    logError(t.message ?: ERR_GENERIC)
                }
            })
        }
    }

    /**
     * reset state
     */
    fun resetWeatherFlow() {
        _weatherFlow.value = UiState.Idle
    }

    fun resetCityFlow() {
        _citiesFlow.value = UiState.Idle
    }
}