package com.busanit.subway_project.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.busanit.subway_project.R
import com.busanit.subway_project.databinding.ItemStationBinding
import com.busanit.subway_project.model.Station

class StationAdapter(private var stations: List<Station>) : RecyclerView.Adapter<StationAdapter.StationViewHolder>() {

    inner class StationViewHolder(private val binding: ItemStationBinding) : RecyclerView.ViewHolder(binding.root) {

        private val textViewStationName: TextView = itemView.findViewById(R.id.textViewStationName)

        fun bind(station: Station) {
            textViewStationName.text = station.sname
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {

        val binding = ItemStationBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return StationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        val station = stations[position]

        when (station.line.lineCd) {
            1 -> {
                val textView = holder.itemView.findViewById<TextView>(R.id.lineTextView)
                textView.setBackgroundResource(R.drawable.image_line1_orange)
                textView.setText("1")
            }
            2 -> {
                val textView = holder.itemView.findViewById<TextView>(R.id.lineTextView)
                textView.setBackgroundResource(R.drawable.image_line2_green)
                textView.setText("2")
            }
            3 -> {
                val textView = holder.itemView.findViewById<TextView>(R.id.lineTextView)
                textView.setBackgroundResource(R.drawable.image_line3_brown)
                textView.setText("3")
            }
            4 -> {
                val textView = holder.itemView.findViewById<TextView>(R.id.lineTextView)
                textView.setBackgroundResource(R.drawable.image_line4_blue)
                textView.setText("4")
            }
            8 -> {
                val textView = holder.itemView.findViewById<TextView>(R.id.lineTextView)
                textView.setBackgroundResource(R.drawable.image_line8_sky)
                textView.setText("동")
            }
            else -> {
                val textView = holder.itemView.findViewById<TextView>(R.id.lineTextView)
                textView.setBackgroundResource(R.drawable.image_line9_purple)
                textView.setText("김")
            }
        }

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