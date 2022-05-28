package com.amartya.weather.viewmodels

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amartya.weather.repositories.WeatherRepository
import com.google.android.gms.location.FusedLocationProviderClient
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
            val api = WeatherRepository.getWeather(location)
            api.enqueue(object : Callback<String>{
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    response.body()?.let {
                        Log.d("MSG", it)
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("MSG", "err: ${t.message}")
                }
            })
        }
    }
}