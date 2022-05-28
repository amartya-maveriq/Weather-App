package com.amartya.weather.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.amartya.weather.R
import com.amartya.weather.api.ApiService
import com.amartya.weather.db.AppDatabase
import com.amartya.weather.utils.BASE_URL
import com.amartya.weather.utils.PREF_UNIT
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
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "app_database"
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
}