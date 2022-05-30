package com.amartya.weather.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.amartya.weather.models.HomePage
import com.amartya.weather.models.Location

@Database(entities = [Location::class, HomePage::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao

    abstract fun homepageDao(): HomePageDao
}