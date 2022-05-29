package com.amartya.weather.views

import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
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
        val units = arrayOf(UNIT_METRIC, UNIT_IMPERIAL)
        view.findViewById<Spinner>(R.id.spinner_app_unit).apply {
            adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, units)
            setSelection(units.indexOf(sharedPreferences.getString(PREF_UNIT, "") ?: UNIT_METRIC))
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, i: Int, p3: Long) {
                    sharedPreferences.edit().putString(PREF_UNIT, units[i]).apply()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    // nothing to do
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.onDismissed()
    }
}