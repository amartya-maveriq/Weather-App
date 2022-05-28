package com.amartya.weather.sealed

sealed class UiState {
    data class Success(val obj: Any?): UiState()
    data class Error(val throwable: Throwable): UiState()
    object Loading: UiState()
    object Idle: UiState()
}
