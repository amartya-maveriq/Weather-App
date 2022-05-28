package com.amartya.weather.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Location(
    @PrimaryKey
    @SerializedName("name"            ) var name           : String,
    @SerializedName("region"          ) var region         : String? = null,
    @SerializedName("country"         ) var country        : String? = null,
    @SerializedName("lat"             ) var lat            : Double? = null,
    @SerializedName("lon"             ) var lon            : Double? = null,
    @SerializedName("tz_id"           ) var tzId           : String? = null,
    @SerializedName("localtime_epoch" ) var localtimeEpoch : Int?    = null,
    @SerializedName("localtime"       ) var localtime      : String? = null
)
