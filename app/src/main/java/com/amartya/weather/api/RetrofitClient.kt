package com.amartya.weather.api

import com.amartya.weather.utils.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient {

    @Volatile
    private var INSTANCE: ApiService? = null

    fun getInstance(): ApiService {
        if (INSTANCE == null) {
            INSTANCE = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
        return INSTANCE!!
    }
}