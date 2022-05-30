package com.amartya.weather.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amartya.weather.models.HomePage

@Dao
interface HomePageDao {
    @Query("SELECT * FROM homepage WHERE name = :name")
    fun getAll(name: String): List<HomePage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(homePage: HomePage)
}