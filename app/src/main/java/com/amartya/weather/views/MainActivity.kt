package com.amartya.weather.views

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.amartya.weather.databinding.ActivityMainBinding
import com.amartya.weather.sealed.UiState
import com.amartya.weather.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.weatherFlow.collect {
                        handleProgress(it)
                    }
                }
                launch {
                    viewModel.citiesFlow.collect {
                        handleProgress(it)
                    }
                }
            }
        }
    }

    private fun handleProgress(uiState: UiState) {
        when (uiState) {
            is UiState.Idle -> {
                binding.progressBar.isVisible = false
            }
            is UiState.Loading -> {
                binding.progressBar.isVisible = true
            }
            else -> {
                // added to avoid warning
            }
        }
    }
}