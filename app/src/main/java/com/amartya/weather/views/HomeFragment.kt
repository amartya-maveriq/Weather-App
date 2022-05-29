package com.amartya.weather.views

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.amartya.weather.R
import com.amartya.weather.adapters.ForecastAdapter
import com.amartya.weather.databinding.FragmentHomeBinding
import com.amartya.weather.models.Current
import com.amartya.weather.models.Forecast
import com.amartya.weather.models.Weather
import com.amartya.weather.sealed.UiState
import com.amartya.weather.utils.ERR_GENERIC
import com.amartya.weather.utils.getCurrentTemp
import com.amartya.weather.utils.getFeelsLikeTemp
import com.amartya.weather.utils.getUvIndexDesc
import com.amartya.weather.utils.getVisibility
import com.amartya.weather.utils.getWindSpeed
import com.amartya.weather.utils.logDebug
import com.amartya.weather.utils.logError
import com.amartya.weather.utils.normalizeUrl
import com.amartya.weather.utils.showSnackbar
import com.amartya.weather.viewmodels.MainViewModel
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel by activityViewModels<MainViewModel>()

    @Inject
    lateinit var appUnit: String

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
                                    setCurrentWeather(weather)
                                    setForecast(weather.forecast)
                                    setUvIndex(weather.current?.uv)
                                    setHumidity(weather.current?.humidity)
                                    setVisibility(weather.current)
                                    setWind(weather.current)
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

    private fun setCurrentWeather(weather: Weather) {
        with(binding.layoutCurrentWeather) {
            tvLocationName.text = weather.location?.name ?: "--"
            tvLocationTemp.text = getCurrentTemp(
                weather.current, appUnit
            )
            Glide.with(requireContext())
                .load(weather.current?.condition?.icon?.normalizeUrl())
                .into(ivWeatherIcon)
                .onLoadFailed(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_error_24
                    )
                )
            tvLocationForecast.text = weather.current?.condition?.text ?: "--"
            tvFeelsLike.text = "Feels like " + getFeelsLikeTemp(
                weather.current, appUnit
            )
        }
    }

    private fun setForecast(forecast: Forecast?) {
        with(binding.layoutForecast) {
            forecast?.forecastday?.let { days ->
                logDebug("size: ${days.size}")
                rvForecast.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = ForecastAdapter(
                        forecastDays = days,
                        requestManager = Glide.with(requireContext()),
                        appUnit = appUnit
                    )
                }
            }

        }
    }

    private fun setUvIndex(uvIndex: Int?) {
        with(binding.layoutUv) {
            tvUvIndex.text = uvIndex?.toString() ?: "--"
            tvUvIndexDesc.text = getUvIndexDesc(uvIndex)
        }
    }

    private fun setHumidity(humidity: Int?) {
        with(binding.layoutHumidity) {
            tvUvIndex.text = "${humidity?.toString() ?: "--"}%"
        }
    }

    private fun setWind(current: Current?) {
        with(binding.layoutWind) {
            tvWind.text = getWindSpeed(current, appUnit)
            tvWindDir.text = "Direction ${current?.windDir ?: "N/A"}"
        }
    }

    private fun setVisibility(current: Current?) {
        binding.layoutVisibility.tvVisibility.text = getVisibility(current, appUnit)
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
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
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