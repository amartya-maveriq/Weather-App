package com.amartya.weather.views

import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.view.children
import com.amartya.weather.R
import com.amartya.weather.utils.PREF_UNIT
import com.amartya.weather.utils.UNIT_IMPERIAL
import com.amartya.weather.utils.UNIT_METRIC
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsBottomSheet(
    private val listener: DismissListener
) : BottomSheetDialogFragment() {

    interface DismissListener {
        fun onDismissed()
    }

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rgUnits = view.findViewById<RadioGroup>(R.id.rg_units)
        view.findViewById<RadioButton>(R.id.rb_unit_metric).also {
            it.text = UNIT_METRIC
        }
        view.findViewById<RadioButton>(R.id.rb_unit_imperial).also {
            it.text = UNIT_IMPERIAL
        }
        when (sharedPreferences.getString(PREF_UNIT, UNIT_METRIC)) {
            UNIT_METRIC -> (rgUnits.children.first() as RadioButton).isChecked = true
            UNIT_IMPERIAL -> (rgUnits.children.last() as RadioButton).isChecked = true
        }
        rgUnits.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.rb_unit_metric -> sharedPreferences.edit().putString(PREF_UNIT, UNIT_METRIC).apply()
                R.id.rb_unit_imperial -> sharedPreferences.edit().putString(PREF_UNIT, UNIT_IMPERIAL).apply()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.onDismissed()
    }
}