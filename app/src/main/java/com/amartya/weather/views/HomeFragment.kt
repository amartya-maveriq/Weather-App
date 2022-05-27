package com.amartya.weather.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.amartya.weather.R
import com.amartya.weather.databinding.FragmentHomeBinding

class HomeFragment: Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentHomeBinding.bind(view)
    }
}