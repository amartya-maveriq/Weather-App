package com.amartya.weather.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.amartya.weather.R
import com.amartya.weather.models.Location
import com.amartya.weather.utils.clickWithDebounce
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CityDetailFragment(
    private val location: Location
): BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_city_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.tv_close).clickWithDebounce {
            this.dismiss()
        }
    }
}