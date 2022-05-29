package com.amartya.weather.di

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.room.Room
import com.amartya.weather.R
import com.amartya.weather.api.ApiService
import com.amartya.weather.db.AppDatabase
import com.amartya.weather.utils.BASE_URL
import com.amartya.weather.utils.CHANNEL_DESC
import com.amartya.weather.utils.CHANNEL_ID
import com.amartya.weather.utils.CHANNEL_NAME
import com.amartya.weather.utils.DB_NAME
import com.amartya.weather.utils.PREF_UNIT
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun getRequestManager(@ApplicationContext context: Context): RequestManager = Glide.with(context)

    @Singleton
    @Provides
    fun getFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        DB_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRetrofitClient(): ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    @Singleton
    @Provides
    fun provideLocationDao(db: AppDatabase) = db.locationDao()

    @Singleton
    @Provides
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(
            context.getString(R.string.shared_pref_file),
            Context.MODE_PRIVATE
        )

    @Singleton
    @Provides
    fun getUnit(sharedPreferences: SharedPreferences): String {
        return sharedPreferences.getString(PREF_UNIT, "") ?: ""
    }

    @Singleton
    @Provides
    fun getNotificationManager(@ApplicationContext context: Context): NotificationManager {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).also {
                it.description = CHANNEL_DESC
                notificationManager.createNotificationChannel(it)
            }
        }
        return notificationManager
    }
}