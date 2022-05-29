package com.amartya.weather.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amartya.weather.R
import com.amartya.weather.adapters.LocationSearchResultAdapter
import com.amartya.weather.models.Location
import com.amartya.weather.utils.hideKeyboard
import com.amartya.weather.viewmodels.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment(
    private val listener: SearchCompleteListener
) : BottomSheetDialogFragment(),
    LocationSearchResultAdapter.SearchResultClickListener {

    interface SearchCompleteListener {
        fun onSearchCompleted(location: Location)
    }

    private val viewModel by activityViewModels<MainViewModel>()
    private val locationSearchResultAdapter: LocationSearchResultAdapter by lazy {
        LocationSearchResultAdapter(listener = this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tilSearch = view.findViewById<TextInputLayout>(R.id.til_search_location)
        val etSearch = tilSearch.editText
        etSearch?.setOnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_SEARCH) {
                val searchText = etSearch.text.toString()
                if (searchText.isNotBlank()) {
                    viewModel.searchCity(searchText)
                }
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }
        view.findViewById<RecyclerView>(R.id.rv_locations).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = locationSearchResultAdapter
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchResults.observe(viewLifecycleOwner) { locations ->
                    locations?.let { locationSearchResultAdapter.addLocations(locations) }
                }
            }
        }
    }

    override fun onSearchResultClicked(location: Location) {
        this.dismiss()
        listener.onSearchCompleted(location)
    }
}