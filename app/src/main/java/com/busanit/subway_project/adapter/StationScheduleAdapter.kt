package com.busanit.subway_project.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.busanit.subway_project.R
import com.busanit.subway_project.databinding.ItemStationScheduleBinding
import com.busanit.subway_project.model.StationSchedule

class StationScheduleAdapter(private var stationSchedules: List<StationSchedule>) : RecyclerView.Adapter<StationScheduleAdapter.StationScheduleViewHolder>() {

    inner class StationScheduleViewHolder(private val binding: ItemStationScheduleBinding) : RecyclerView.ViewHolder(binding.root) {

        private val textViewStationName: TextView = itemView.findViewById(R.id.textViewStationName)

        fun bind(station: StationSchedule) {
            textViewStationName.text = station.sname
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationScheduleViewHolder {

        val binding = ItemStationScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return StationScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StationScheduleViewHolder, position: Int) {
        val station = stationSchedules[position]

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

        holder.bind(stationSchedules[position])
    }

    override fun getItemCount(): Int {

        return stationSchedules.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateStations(newStations: List<StationSchedule>) {

        stationSchedules = newStations

        notifyDataSetChanged()
    }
}