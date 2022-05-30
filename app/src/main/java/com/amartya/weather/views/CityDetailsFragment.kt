package com.amartya.weather.views

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.amartya.weather.R
import com.amartya.weather.adapters.ForecastAdapter
import com.amartya.weather.databinding.FragmentCityDetailsBinding
import com.amartya.weather.models.Current
import com.amartya.weather.models.Forecast
import com.amartya.weather.models.Location
import com.amartya.weather.models.Weather
import com.amartya.weather.sealed.UiState
import com.amartya.weather.utils.ERR_GENERIC
import com.amartya.weather.utils.clickWithDebounce
import com.amartya.weather.utils.getCurrentTemp
import com.amartya.weather.utils.getFeelsLikeTemp
import com.amartya.weather.utils.getUvIndexDesc
import com.amartya.weather.utils.getVisibility
import com.amartya.weather.utils.getWindSpeed
import com.amartya.weather.utils.normalizeUrl
import com.amartya.weather.utils.showSnackbar
import com.amartya.weather.viewmodels.MainViewModel
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CityDetailsFragment : Fragment(R.layout.fragment_city_details) {

    private val viewModel by activityViewModels<MainViewModel>()
    private lateinit var binding: FragmentCityDetailsBinding

    @Inject
    lateinit var appUnit: String

    @Inject
    lateinit var requestManager: RequestManager

    private lateinit var location: Location

    private val forecastAdapter by lazy {
        ForecastAdapter(
            requestManager = requestManager,
            appUnit = appUnit
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        location = viewModel.selectedLocation
        binding = FragmentCityDetailsBinding.bind(view)
        binding.fabAddFav.clickWithDebounce {
            viewModel.addToFavoriteCities(location)
            requireActivity().onBackPressed()
        }
        binding.ivBack.clickWithDebounce {
            requireActivity().onBackPressed()
        }
        binding.layoutForecast.rvForecast.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = forecastAdapter
        }
        viewModel.checkForCity(location.name)
        viewModel.fetchWeather(locationName = location.name)
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
                                    requireView(),
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
                launch {
                    viewModel.exists.observe(viewLifecycleOwner) { exists ->
                        binding.fabAddFav.isVisible = !exists
                    }
                }
            }
        }
    }

    private fun setCurrentWeather(weather: Weather) {
        with(binding.layoutCurrentWeather) {
            tvLocationName.text = weather.location?.name ?: "--"
            tvLocationCountry.text = weather.location?.country ?: "--"
            tvLocationTemp.text = getCurrentTemp(
                weather.current, appUnit
            )
            requestManager
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
        forecast?.forecastday?.let { days ->
            forecastAdapter.setDays(days)
        }
    }

    private fun setUvIndex(uvIndex: Int?) {
        with(binding.layoutUv) {
            tvUvIndex.text = uvIndex?.toString() ?: "--"
            tvUvIndexDesc.text = getUvIndexDesc(uvIndex)
        }
        binding.layoutUv.root.findViewById<TextView>(R.id.tv_uv_index).text =
            uvIndex?.toString() ?: "--"
        binding.layoutUv.root.findViewById<TextView>(R.id.tv_uv_index_desc).text =
            getUvIndexDesc(uvIndex)
    }

    private fun setHumidity(humidity: Int?) {
        with(binding.layoutHumidity) {
            tvHumidity.text = "${humidity?.toString() ?: "--"}%"
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
}