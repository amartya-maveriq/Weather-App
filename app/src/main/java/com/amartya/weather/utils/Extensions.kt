package com.amartya.weather.utils

import android.content.Context
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.amartya.weather.BuildConfig
import com.amartya.weather.R
import com.amartya.weather.models.Current
import com.amartya.weather.models.Day
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.util.*


// list of constants used in the app
private const val CLICK_DEBOUNCE_DELAY = 1000L
private const val TAG = "MyWeatherAppLog"
const val API_KEY = "c39bba18a6e9481f80f163931222705"
const val BASE_URL = "https://api.weatherapi.com/v1/"
const val ERR_GENERIC = "An unknown error has occurred"
const val PREF_UNIT = "com.amartya.weather.PREF_UNIT"
const val PREF_LOC_NAME = "com.amartya.weather.PREF_LOC_NAME"
const val UNIT_IMPERIAL = "Imperial (e.g, °F, miles etc.)"
const val UNIT_METRIC = "Metric (e.g, °C, kms etc.)"
const val DB_NAME = "app_database"
const val CHANNEL_ID = "com.amartya.weather.CHANNEL_ID"
const val CHANNEL_NAME = "com.amartya.weather.CHANNEL_NAME"
const val CHANNEL_DESC = "Weather App Channel"

/**
 * Prevent any view to be clicked twice accidentally withing one second
 */
fun View.clickWithDebounce(debounceTime: Long = CLICK_DEBOUNCE_DELAY, action: (View) -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action(v)

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

/**
 * Hide keyboard from any Fragment
 */
fun Fragment.hideKeyboard() {
    val imm: InputMethodManager? =
        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.hideSoftInputFromWindow(requireView().windowToken, 0)
}

/**
 * show Snackbar
 */
fun showSnackbar(view: View, msg: String, isPositive: Boolean) {
    val color = if (isPositive) R.color.positive_green else R.color.negative_red
    Snackbar
        .make(view, msg, BaseTransientBottomBar.LENGTH_LONG)
        .setBackgroundTint(view.context.getColor(color))
        .show()
}

/**
 * log debug
 */
fun logDebug(msg: String) {
    if (BuildConfig.DEBUG) Log.d(TAG, msg)
}

/**
 * log error
 */
fun logError(msg: String) {
    if (BuildConfig.DEBUG) Log.e(TAG, msg)
}

/**
 * normalize image url
 */
fun String.normalizeUrl(): String {
    return "https:$this"
}

/**
 * Get current temperature
 */
fun getCurrentTemp(current: Current?, unit: String?): String {
    return when (unit) {
        UNIT_IMPERIAL -> (current?.tempF ?: 0.0).toString() + "°F"
        else -> (current?.tempC ?: 0.0).toString() + "°C"
    }
}

/**
 * Get feels like temp
 */
fun getFeelsLikeTemp(current: Current?, unit: String?): String {
    return when (unit) {
        UNIT_IMPERIAL -> (current?.feelslikeF ?: 0.0).toString() + "°F"
        else -> (current?.feelslikeC ?: 0.0).toString() + "°C"
    }
}

/**
 * Get max temp
 */
fun getMaxTemp(day: Day?, unit: String): String {
    return when(unit) {
        UNIT_IMPERIAL -> "H " + (day?.maxtempF ?: 0.0).toString() + "°F"
        else -> "H " + (day?.maxtempC ?: 0.0).toString() + "°C"
    }
}

/**
 * Get min temp
 */
fun getMinTemp(day: Day?, unit: String): String {
    return when(unit) {
        UNIT_IMPERIAL -> "L " + (day?.mintempF ?: 0.0).toString() + "°F"
        else -> "L " + (day?.mintempC ?: 0.0).toString() + "°C"
    }
}

/**
 * Get UV desc
 */
fun getUvIndexDesc(uvIndex: Int?): String = when(uvIndex) {
    in 0..2 -> "Low"
    in 3..5 -> "Moderate"
    in 6..7 -> "High"
    in 8..10 -> "Very High"
    in 11..15 -> "Extreme"
    else -> "Not found"
}

/**
 * Get wind speed
 */
fun getWindSpeed(current: Current?, unit: String): String = when(unit) {
    UNIT_IMPERIAL -> (current?.windMph ?: 0.0).toString() + " mph"
    else -> (current?.windKph ?: 0.0).toString() + " kph"
}

/**
 * Get visibility
 */
fun getVisibility(current: Current?, unit: String): String = when(unit) {
    UNIT_IMPERIAL -> (current?.visMiles ?: 0.0).toString() + " mi"
    else -> (current?.visKm ?: 0.0).toString() + " km"
}

/**
 * Get time in millis for everyday at 6
 */
fun getTimeMillisForSix(): Long {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 6)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    if (cal.before(Calendar.getInstance())) {
        cal.add(Calendar.DATE, 1)
    }
    return cal.timeInMillis
}