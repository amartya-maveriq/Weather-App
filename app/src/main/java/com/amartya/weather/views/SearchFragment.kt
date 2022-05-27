package com.amartya.weather.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.amartya.weather.R
import com.amartya.weather.databinding.FragmentSearchBinding

class SearchFragment: Fragment(R.layout.fragment_search) {

    private lateinit var binding: FragmentSearchBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentSearchBinding.bind(view)
    }
}