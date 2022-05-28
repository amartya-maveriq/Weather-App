package com.amartya.weather.utils

import android.app.Activity
import android.os.SystemClock
import android.util.Log
import android.view.View
import com.amartya.weather.BuildConfig
import com.amartya.weather.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar


// list of constants used in the app
private const val CLICK_DEBOUNCE_DELAY = 1000L
const val API_KEY = "c39bba18a6e9481f80f163931222705"
const val BASE_URL = "https://api.weatherapi.com/v1/"

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
fun Activity.logd(msg: String) {
    if (BuildConfig.DEBUG) Log.d(this::class.java.name, msg)
}

/**
 * log error
 */
fun Activity.loge(msg: String) {
    if (BuildConfig.DEBUG) Log.e(this::class.java.name, msg)
}