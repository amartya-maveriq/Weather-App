package com.amartya.weather.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.amartya.weather.R
import com.amartya.weather.databinding.FragmentCitiesBinding

class CitiesFragment: Fragment(R.layout.fragment_cities) {

    private lateinit var binding: FragmentCitiesBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentCitiesBinding.bind(view)
    }
}