package com.busanit.subway_project.fragment

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.busanit.subway_project.R
import com.busanit.subway_project.adapter.StationAdapter
import com.busanit.subway_project.databinding.FragmentMinimumTransferBinding
import com.busanit.subway_project.model.Line
import com.busanit.subway_project.model.Station
import java.util.Calendar

class ShortestTimeFragment : Fragment() {

    private lateinit var binding: FragmentMinimumTransferBinding
    private lateinit var stations: List<Station>
    private lateinit var allStations: List<Station>
    private lateinit var intermediateStations: List<Station>
    private lateinit var adapter: StationAdapter

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding = FragmentMinimumTransferBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        // "00분 소요" 텍스트 뷰
        val time = calculateTime()
        binding.timeInfoTextView1.text = "${time}분"

        // "00개 역 이동" 텍스트 뷰
        val stations = calculateTotalStations()
        binding.totalStationTextView.text = "${stations}개 역 이동"

        // 시간 설정 버튼 → 사용자가 직접 시간 설정
        binding.setTime.setOnClickListener {

            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                requireContext(),
                { _, selectedHour, selectedMinute ->
                    val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                    binding.setTime.text = "출발 $selectedTime"
                },
                hour,
                minute,
                true
            )
            timePickerDialog.show()
        }

        // 알람 설정 버튼
        binding.setAlarm.setOnClickListener {


        }

        setUpRecyclerView()
        setupToggleButton()
    }

    // 총 소요 시간 계산 메서드
    private fun calculateTime(): Int {
        return 6;
    }

    // 총 경유 역 개수 계산 메서드
    private fun calculateTotalStations(): Int {
        return 4;
    }

    private fun setUpRecyclerView() {

        allStations = listOf(
            Station(100, "금련산역", Line(1, "1호선"), 0L),
            Station(101, "양산역", Line(1, "1호선"), 0L),
            Station(102, "남천역", Line(1, "1호선"), 0L),
            Station(103, "경성대/부경대역", Line(1, "1호선"), 0L),
            Station(104, "대연역", Line(1, "1호선"), 0L),
            Station(105, "못골역", Line(1, "1호선"), 0L)
        )

        // 출발역 설정
        binding.startStationTextView.text = allStations.first().sname

        intermediateStations = allStations.subList(1, allStations.size - 1)

        stations = listOf()

        adapter = StationAdapter(stations)

        binding.recyclerViewStations.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewStations.adapter = adapter

        // 도착역 설정
        binding.endStationText.text = allStations.last().sname
    }

    private fun setupToggleButton() {

        binding.toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 중간 역 보이기
                stations = intermediateStations
                binding.recyclerViewStations.visibility = View.VISIBLE
            } else {
                // 중간 역 숨기기
                stations = listOf()
                binding.recyclerViewStations.visibility = View.GONE
            }
            adapter.updateStations(stations)
        }
    }

    // 중간 역 길이에 따라 왼쪽 바 길이 조정
    private fun adjustLineViewHeight(itemCount: Int) {

        val params = binding.lineView.layoutParams
        params.height = itemCount * 200

        binding.lineView.layoutParams = params
    }
}