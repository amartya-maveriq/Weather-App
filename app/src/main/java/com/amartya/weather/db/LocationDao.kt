package com.amartya.weather.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.amartya.weather.models.Location

@Dao
interface LocationDao {

    @Query("SELECT * FROM location")
    fun getAll(): List<Location>

    @Insert
    fun addNew(location: Location)
}