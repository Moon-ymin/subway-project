package com.busanit.subway_project.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.busanit.subway_project.databinding.ItemStationBinding
import com.busanit.subway_project.model.Station

class StationAdapter(private var stations: List<Station>) : RecyclerView.Adapter<StationAdapter.StationViewHolder>() {

    inner class StationViewHolder(private val binding: ItemStationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(station: Station) {
            binding.textViewStationName.text = station.sname
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {

        val binding = ItemStationBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return StationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {

        holder.bind(stations[position])
    }

    override fun getItemCount(): Int {

        return stations.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateStations(newStations: List<Station>) {

        stations = newStations

        notifyDataSetChanged()
    }
}