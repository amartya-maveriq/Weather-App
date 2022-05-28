package com.amartya.weather.viewmodels

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amartya.weather.UiState
import com.amartya.weather.models.Weather
import com.amartya.weather.repositories.WeatherRepository
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Shared viewModel for MainActivity and it's fragments
 */
class MainViewModel: ViewModel() {

    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> = _location

    private val _weatherFlow = MutableStateFlow<UiState>(UiState.Idle)
    val weatherFlow: StateFlow<UiState> = _weatherFlow

    @SuppressLint("MissingPermission")
    fun getUserLocation(fusedLocationProviderClient: FusedLocationProviderClient) {
        viewModelScope.launch {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener {
                    if (it == null) {
                        Log.d("MSG", "getUserLocation: null")
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
            val api = WeatherRepository.getWeather(location)
            api.enqueue(object : Callback<Weather>{
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
     * reset state
     */
    fun resetWeatherFlow() {
        _weatherFlow.value = UiState.Idle
    }
}