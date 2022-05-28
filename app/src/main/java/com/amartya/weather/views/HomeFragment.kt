package com.amartya.weather.views

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.amartya.weather.R
import com.amartya.weather.sealed.UiState
import com.amartya.weather.databinding.FragmentHomeBinding
import com.amartya.weather.models.Weather
import com.amartya.weather.utils.ERR_GENERIC
import com.amartya.weather.utils.logDebug
import com.amartya.weather.utils.logError
import com.amartya.weather.utils.showSnackbar
import com.amartya.weather.viewmodels.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel by activityViewModels<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentHomeBinding.bind(view)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (hasLocationPermission()) {
            getLastKnownLocation()
        } else {
            locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.location.observe(requireActivity()) { location ->
                        location?.let {
                            viewModel.fetchWeather(it)
                        }
                    }
                }

                launch {
                    viewModel.weatherFlow.collect { uiState ->
                        when (uiState) {
                            is UiState.Success -> {
                                (uiState.obj as? Weather)?.let { weather ->
                                    logDebug(weather.toString())
                                }
                                viewModel.resetWeatherFlow()
                            }
                            is UiState.Error -> {
                                showSnackbar(
                                    binding.root,
                                    uiState.throwable.message ?: ERR_GENERIC,
                                    false
                                )
                                viewModel.resetWeatherFlow()
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

    private fun getLastKnownLocation() {
        if (this::fusedLocationClient.isInitialized) {
            viewModel.getUserLocation(fusedLocationClient)
        }
    }

    private fun hasLocationPermission(): Boolean = ActivityCompat.checkSelfPermission(
        requireActivity(),
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // location perm granted
                getLastKnownLocation()
            }
            else -> {
                // location perm NOT granted
                val message = "Permission not granted by user"
                logError(message)
                showSnackbar(view = binding.root, msg = message, false)
            }
        }
    }
}