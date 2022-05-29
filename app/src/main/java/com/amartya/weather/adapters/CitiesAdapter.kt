package com.amartya.weather.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amartya.weather.databinding.ItemLocationBinding
import com.amartya.weather.models.Location
import com.amartya.weather.utils.clickWithDebounce

class CitiesAdapter(
    private val locations: MutableList<Location> = mutableListOf(),
    private val listener: LocationInteractionListener
) : RecyclerView.Adapter<CitiesAdapter.CitiesViewHolder>() {

    interface LocationInteractionListener {
        fun onLocationClicked(location: Location)
        fun onLocationDelete(location: Location)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addLocations(locationList: List<Location>) {
        this.locations.apply {
            clear()
            addAll(locationList)
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeLocation(location: Location) {
        this.locations.remove(location)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitiesViewHolder {
        return CitiesViewHolder(
            ItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CitiesViewHolder, position: Int) {
        holder.bind(locations[position])
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    inner class CitiesViewHolder(private val binding: ItemLocationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(location: Location) {
            binding.btnDelete.clickWithDebounce {
                listener.onLocationDelete(location)
            }
            binding.root.clickWithDebounce {
                listener.onLocationClicked(location)
            }
            binding.tvLocationName.text = location.name
            binding.tvLocationCountry.text = location.country
        }
    }
}