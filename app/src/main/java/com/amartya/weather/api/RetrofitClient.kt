package com.amartya.weather.api

import com.amartya.weather.utils.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    @Volatile
    private var INSTANCE: ApiService? = null

    @Synchronized
    fun getInstance(): ApiService {
        if (INSTANCE == null) {
            INSTANCE = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
        return INSTANCE!!
    }
}