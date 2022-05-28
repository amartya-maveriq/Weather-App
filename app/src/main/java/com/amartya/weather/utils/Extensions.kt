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
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar


// list of constants used in the app
private const val CLICK_DEBOUNCE_DELAY = 1000L
private const val TAG = "MyWeatherAppLog"
const val API_KEY = "c39bba18a6e9481f80f163931222705"
const val BASE_URL = "https://api.weatherapi.com/v1/"
const val ERR_GENERIC = "An unknown error has occurred"
const val PREF_UNIT = "com.amartya.weather.PREF_UNIT"
const val UNIT_IMPERIAL = "com.amartya.weather.UNIT_IMPERIAL"
const val UNIT_METRIC = "com.amartya.weather.UNIT_METRIC"

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
    return "https:/$this"
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