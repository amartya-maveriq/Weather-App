package com.amartya.weather.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amartya.weather.databinding.ItemLocationSearchResultBinding
import com.amartya.weather.models.Location
import com.amartya.weather.utils.clickWithDebounce

/**
 * For recyclerview that shows search result in search for location screen
 */
class LocationSearchResultAdapter(
    private val locations: MutableList<Location> = mutableListOf(),
    private val listener: SearchResultClickListener
) : RecyclerView.Adapter<LocationSearchResultAdapter.LocationSearchResultViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun addLocations(locations: List<Location>) {
        this.locations.clear()
        this.locations.addAll(locations)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LocationSearchResultViewHolder {
        return LocationSearchResultViewHolder(
            ItemLocationSearchResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LocationSearchResultViewHolder, position: Int) {
        holder.bind(locations[position])
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    inner class LocationSearchResultViewHolder(
        private val binding: ItemLocationSearchResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(location: Location) {
            binding.tvLocationName.text = location.name
            binding.root.clickWithDebounce {
                listener.onSearchResultClicked(location)
            }
        }
    }

    interface SearchResultClickListener {
        fun onSearchResultClicked(location: Location)
    }
}