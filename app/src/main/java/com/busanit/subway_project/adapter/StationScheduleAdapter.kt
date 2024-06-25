package com.busanit.subway_project.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.busanit.subway_project.R
import com.busanit.subway_project.model.StationSchedule

class StationScheduleAdapter(private var stationSchedules: List<StationSchedule>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SCHEDULE = 0
        private const val VIEW_TYPE_STRING = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (stationSchedules[position]) {
            is StationSchedule.Schedule -> VIEW_TYPE_SCHEDULE
            is StationSchedule.Walking -> VIEW_TYPE_STRING
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            VIEW_TYPE_SCHEDULE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_station_schedule, parent, false)
                ScheduleViewHolder(view)
            }
            VIEW_TYPE_STRING -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_station_schedule_string, parent, false)
                StringViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is ScheduleViewHolder -> holder.bind(stationSchedules[position] as StationSchedule.Schedule)
            is StringViewHolder -> holder.bind(stationSchedules[position] as StationSchedule.Walking)
        }
    }

    inner class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textViewStationName: TextView = itemView.findViewById(R.id.textViewStationName)
        private val textViewSubwayTime: TextView = itemView.findViewById(R.id.textViewSubwayTime)

        fun bind(station: StationSchedule.Schedule) {
            textViewStationName.text = station.sname

            when (station.line.lineCd) {
                1 -> {
                    val textView = itemView.findViewById<TextView>(R.id.lineTextView)
                    textView.setBackgroundResource(R.drawable.image_line1_orange)
                    textView.setText("1")
                }
                2 -> {
                    val textView = itemView.findViewById<TextView>(R.id.lineTextView)
                    textView.setBackgroundResource(R.drawable.image_line2_green)
                    textView.setText("2")
                }
                3 -> {
                    val textView = itemView.findViewById<TextView>(R.id.lineTextView)
                    textView.setBackgroundResource(R.drawable.image_line3_brown)
                    textView.setText("3")
                }
                4 -> {
                    val textView = itemView.findViewById<TextView>(R.id.lineTextView)
                    textView.setBackgroundResource(R.drawable.image_line4_blue)
                    textView.setText("4")
                }
                8 -> {
                    val textView = itemView.findViewById<TextView>(R.id.lineTextView)
                    textView.setBackgroundResource(R.drawable.image_line8_sky)
                    textView.setText("동")
                }
                else -> {
                    val textView = itemView.findViewById<TextView>(R.id.lineTextView)
                    textView.setBackgroundResource(R.drawable.image_line9_purple)
                    textView.setText("김")
                }
            }

            textViewSubwayTime.text = station.time
        }
    }

    inner class StringViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val stringText: TextView = itemView.findViewById(R.id.textViewNotice)

        fun bind(string: StationSchedule.Walking) {
            stringText.text = string.notice
        }
    }

    override fun getItemCount(): Int = stationSchedules.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateStations(newStations: List<StationSchedule>) {

        stationSchedules = newStations

        notifyDataSetChanged()
    }
}