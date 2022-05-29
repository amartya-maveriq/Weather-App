package com.amartya.weather.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.core.app.NotificationCompat
import com.amartya.weather.R
import com.amartya.weather.models.Weather
import com.amartya.weather.repositories.WeatherRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class WeatherBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var repository: WeatherRepository

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onReceive(context: Context, intent: Intent?) {
        sharedPreferences.getString(PREF_LOC_NAME, "")?.let {
            getWeatherAt(placeName = it, context)
        }
    }

    private fun getWeatherAt(placeName: String, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.getWeather(placeName).enqueue(object : Callback<Weather> {
                override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                    val weather = response.body()
                    weather?.let {
                        showNotification(it, context)
                    }
                }

                override fun onFailure(call: Call<Weather>, t: Throwable) {
                    Log.e("MSG", "err: ${t.message}")
                }
            })
        }
    }

    private fun showNotification(weather: Weather, context: Context) {
        NotificationCompat.Builder(context, CHANNEL_ID).also {
            it.setSmallIcon(R.drawable.ic_baseline_air_24)
            it.setContentTitle("Hello there")
            it.setContentText("Temperature now at ${weather.location?.name ?: "your location"} is ${weather.current?.tempC ?: 0.0} °C")
            notificationManager.notify(1, it.build())
        }
    }
}