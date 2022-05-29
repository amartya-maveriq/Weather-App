package com.amartya.weather.views

import android.Manifest
import android.content.SharedPreferences
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
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.amartya.weather.R
import com.amartya.weather.adapters.ForecastAdapter
import com.amartya.weather.databinding.FragmentHomeBinding
import com.amartya.weather.models.Current
import com.amartya.weather.models.Forecast
import com.amartya.weather.models.Weather
import com.amartya.weather.sealed.UiState
import com.amartya.weather.utils.ERR_GENERIC
import com.amartya.weather.utils.PREF_LOC_NAME
import com.amartya.weather.utils.PREF_UNIT
import com.amartya.weather.utils.UNIT_METRIC
import com.amartya.weather.utils.clickWithDebounce
import com.amartya.weather.utils.getCurrentTemp
import com.amartya.weather.utils.getFeelsLikeTemp
import com.amartya.weather.utils.getUvIndexDesc
import com.amartya.weather.utils.getVisibility
import com.amartya.weather.utils.getWindSpeed
import com.amartya.weather.utils.logError
import com.amartya.weather.utils.normalizeUrl
import com.amartya.weather.utils.showSnackbar
import com.amartya.weather.viewmodels.MainViewModel
import com.bumptech.glide.RequestManager
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), SettingsBottomSheet.DismissListener {

    private lateinit var binding: FragmentHomeBinding

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var requestManager: RequestManager

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private val viewModel by activityViewModels<MainViewModel>()
    private val forecastAdapter by lazy {
        ForecastAdapter(
            requestManager = requestManager,
            appUnit = sharedPreferences.getString(PREF_UNIT, "") ?: ""
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentHomeBinding.bind(view)

        if (hasLocationPermission()) {
            getLastKnownLocation()
        } else {
            locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
        }

        binding.layoutForecast.rvForecast.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = forecastAdapter
        }

        binding.ivList.clickWithDebounce {
            requireView().findNavController()
                .navigate(HomeFragmentDirections.actionHomeFragmentToCitiesFragment())
        }

        binding.ivSettings.clickWithDebounce {
            SettingsBottomSheet(this).show(
                requireActivity().supportFragmentManager,
                SettingsBottomSheet::class.java.simpleName
            )
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
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
                                    saveToPref(weather.location?.name)
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
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    private fun saveToPref(locationName: String?) {
        locationName?.let {
            sharedPreferences.edit().putString(PREF_LOC_NAME, it).apply()
        }
    }

    private fun setCurrentWeather(weather: Weather) {
        with(binding.layoutCurrentWeather) {
            tvLocationName.text = weather.location?.name ?: "--"
            tvLocationCountry.text = weather.location?.country ?: "--"
            tvLocationTemp.text = getCurrentTemp(
                weather.current, sharedPreferences.getString(PREF_UNIT, "") ?: ""
            )
            val url = weather.current?.condition?.icon?.normalizeUrl()
            requestManager
                .load(url)
                .into(ivWeatherIcon)
                .onLoadFailed(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_error_24
                    )
                )
            tvLocationForecast.text = weather.current?.condition?.text ?: "--"
            tvFeelsLike.text = "Feels like " + getFeelsLikeTemp(
                weather.current, sharedPreferences.getString(PREF_UNIT, "") ?: ""
            )
        }
    }

    private fun setForecast(forecast: Forecast?) {
        forecast?.forecastday?.let { days ->
            forecastAdapter.updateUnit(sharedPreferences.getString(PREF_UNIT, "") ?: UNIT_METRIC)
            forecastAdapter.setDays(days)
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
            tvWind.text = getWindSpeed(current, sharedPreferences.getString(PREF_UNIT, "") ?: "")
            tvWindDir.text = "Direction ${current?.windDir ?: "N/A"}"
        }
    }

    private fun setVisibility(current: Current?) {
        binding.layoutVisibility.tvVisibility.text =
            getVisibility(current, sharedPreferences.getString(PREF_UNIT, "") ?: "")
    }

    private fun getLastKnownLocation() {
        viewModel.getUserLocation(fusedLocationClient)
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

    override fun onDismissed() {
        getLastKnownLocation()
    }
}