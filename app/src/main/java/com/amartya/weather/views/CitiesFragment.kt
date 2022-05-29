package com.amartya.weather.views

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.amartya.weather.R
import com.amartya.weather.databinding.FragmentCitiesBinding
import com.amartya.weather.models.Location
import com.amartya.weather.sealed.UiState
import com.amartya.weather.utils.ERR_GENERIC
import com.amartya.weather.utils.clickWithDebounce
import com.amartya.weather.utils.showSnackbar
import com.amartya.weather.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Marked as `Favorites` in the app
 */
@AndroidEntryPoint
class CitiesFragment : Fragment(R.layout.fragment_cities), SearchFragment.SearchCompleteListener {

    private lateinit var binding: FragmentCitiesBinding
    private val viewModel by activityViewModels<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentCitiesBinding.bind(view)

        viewModel.getFavoriteCities()

        binding.fabAddNewLocation.clickWithDebounce {
            SearchFragment(this).show(
                requireActivity().supportFragmentManager,
                SearchFragment::class.java.simpleName
            )
        }

        binding.ivBack.clickWithDebounce {
            requireActivity().onBackPressed()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.citiesFlow.collect { uiState ->
                    when (uiState) {
                        is UiState.Success -> {
                            val locations = (uiState.obj as? List<*>)?.filterIsInstance<Location>()
                            binding.tvNoLocation.isVisible = locations.isNullOrEmpty()
                            viewModel.resetCityFlow()
                        }
                        is UiState.Error -> {
                            showSnackbar(
                                binding.root,
                                uiState.throwable.message ?: ERR_GENERIC,
                                false
                            )
                            viewModel.resetCityFlow()
                        }
                        else -> {
                            //
                        }
                    }
                }
            }
        }
    }

    override fun onSearchCompleted(location: Location) {
        CityDetailFragment(location).show(
            requireActivity().supportFragmentManager,
            CityDetailFragment::class.java.simpleName
        )
    }
}