package com.amartya.weather.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.amartya.weather.R
import com.amartya.weather.databinding.ActivityMainBinding

// home, cities, search, settings
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
    }
}