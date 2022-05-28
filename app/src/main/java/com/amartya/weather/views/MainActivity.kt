package com.amartya.weather.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.amartya.weather.R
import com.amartya.weather.UiState
import com.amartya.weather.databinding.ActivityMainBinding
import com.amartya.weather.viewmodels.MainViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

// home, cities, search, settings
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavMain.setupWithNavController(
            Navigation.findNavController(
                this,
                R.id.fragment_nav_host_main
            )
        )

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weatherFlow.collect {
                    when (it) {
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
        }
    }
}