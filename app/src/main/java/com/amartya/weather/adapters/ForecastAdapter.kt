package com.amartya.weather.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amartya.weather.databinding.ItemForecastBinding
import com.amartya.weather.models.Forecastday
import com.amartya.weather.utils.getMaxTemp
import com.amartya.weather.utils.getMinTemp
import com.amartya.weather.utils.normalizeUrl
import com.bumptech.glide.RequestManager

class ForecastAdapter(
    private val forecastDays: MutableList<Forecastday> = mutableListOf(),
    private val requestManager: RequestManager,
    private var appUnit: String
) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setDays(daysList: List<Forecastday>) {
        forecastDays.apply {
            clear()
            addAll(daysList)
        }
        notifyDataSetChanged()
    }

    fun updateUnit(unit: String) {
        this.appUnit = unit
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        return ForecastViewHolder(
            ItemForecastBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(forecastDays[position])
    }

    override fun getItemCount(): Int {
        return forecastDays.size
    }

    inner class ForecastViewHolder(
        private val binding: ItemForecastBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(forecastDay: Forecastday) {
            binding.tvDay.text = forecastDay.date
            requestManager.load(forecastDay.day?.condition?.icon?.normalizeUrl())
                .into(binding.ivForecast)
            binding.tvMaxDay.text = getMaxTemp(forecastDay.day, appUnit)
            binding.tvMinDay.text = getMinTemp(forecastDay.day, appUnit)
        }
    }
}