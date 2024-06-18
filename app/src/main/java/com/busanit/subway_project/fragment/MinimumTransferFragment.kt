package com.busanit.subway_project.fragment

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.busanit.subway_project.R
import com.busanit.subway_project.databinding.ActivityRouteCheckBinding
import com.busanit.subway_project.databinding.FragmentMinimumTransferBinding
import java.util.Calendar

class MinimumTransferFragment : Fragment() {

    private lateinit var binding: FragmentMinimumTransferBinding

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

//      "00분 소요" 텍스트 뷰
        val time = calculateTime()
        binding.timeInfoTextView1.text = "${time}분"
        binding.timeInfoTextView2.text = "${time}분 소요"

//      "00개 역 이동" 텍스트 뷰
        val stations = calculateTotalStations()
        binding.totalStationTextView.text = "${stations}개 역 이동"

//      시간 설정 버튼 → 사용자가 직접 시간 설정
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
    }

    private fun calculateTime(): Int {
        return 6;
    }

    private fun calculateTotalStations(): Int {
        return 4;
    }
}