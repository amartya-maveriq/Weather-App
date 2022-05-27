package com.amartya.weather.utils

import android.os.SystemClock
import android.view.View


// list of constants used in the app
private const val CLICK_DEBOUNCE_DELAY = 1000L


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