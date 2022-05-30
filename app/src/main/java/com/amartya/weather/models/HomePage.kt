package com.amartya.weather.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HomePage(
    @PrimaryKey @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "temp_c") val tempC: String,
    @ColumnInfo(name = "temp_f") val tempF: String,
    @ColumnInfo(name = "icon") val icon: String,
    @ColumnInfo(name = "short_forecast") val shortForecast: String,
    @ColumnInfo(name = "feels_like_c") val feelsLikeC: String,
    @ColumnInfo(name = "feels_like_f") val feelsLikeF: String,
    @ColumnInfo(name = "day_1_date") val day1Date: String = "",
    @ColumnInfo(name = "day_1_icon") val day1Icon: String = "",
    @ColumnInfo(name = "day_1_hi") val day1Hi: String = "",
    @ColumnInfo(name = "day_1_hi_f") val day1HiF: String = "",
    @ColumnInfo(name = "day_1_lo") val day1Lo: String = "",
    @ColumnInfo(name = "day_1_lo_f") val day1LoF: String = "",
    @ColumnInfo(name = "day_2_date") val day2Date: String = "",
    @ColumnInfo(name = "day_2_icon") val day2Icon: String = "",
    @ColumnInfo(name = "day_2_hi") val day2Hi: String = "",
    @ColumnInfo(name = "day_2_hi_f") val day2HiF: String = "",
    @ColumnInfo(name = "day_2_lo") val day2Lo: String = "",
    @ColumnInfo(name = "day_2_lo_f") val day2LoF: String = "",
    @ColumnInfo(name = "day_3_date") val day3Date: String = "",
    @ColumnInfo(name = "day_3_icon") val day3Icon: String = "",
    @ColumnInfo(name = "day_3_hi") val day3Hi: String = "",
    @ColumnInfo(name = "day_3_hi_f") val day3HiF: String = "",
    @ColumnInfo(name = "day_3_lo") val day3Lo: String = "",
    @ColumnInfo(name = "day_3_lo_f") val day3LoF: String = "",
    @ColumnInfo(name = "uv_index") val uvIndex: String,
    @ColumnInfo(name = "humidity") val humidity: String,
    @ColumnInfo(name = "visibility_km") val visibilityKm: String,
    @ColumnInfo(name = "visibility_mi") val visibilityMi: String,
    @ColumnInfo(name = "wind_kph") val windKph: String,
    @ColumnInfo(name = "wind_mph") val windMph: String,
) {
    companion object {
        fun mapFromWeather(weather: Weather) = HomePage(
            name = weather.location?.name.toString(),
            country = weather.location?.country.toString(),
            tempC = weather.current?.tempC.toString(),
            tempF = weather.current?.tempF.toString(),
            icon = weather.current?.condition?.icon.toString(),
            shortForecast = weather.current?.condition?.text.toString(),
            feelsLikeF = weather.current?.feelslikeF.toString(),
            feelsLikeC = weather.current?.feelslikeC.toString(),
            uvIndex = weather.current?.uv.toString(),
            humidity = weather.current?.humidity.toString(),
            visibilityKm = weather.current?.visKm.toString(),
            visibilityMi = weather.current?.visMiles.toString(),
            windKph = weather.current?.windKph.toString(),
            windMph = weather.current?.windMph.toString(),
        )
    }
}
