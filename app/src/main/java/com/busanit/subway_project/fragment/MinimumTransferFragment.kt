package com.busanit.subway_project.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.busanit.subway_project.R
import com.busanit.subway_project.databinding.ActivityRouteCheckBinding
import com.busanit.subway_project.databinding.FragmentMinimumTransferBinding

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

        setTextView()
    }

    private fun calculateTime(): Int {
        return 6;
    }

    private fun calculateTotalStations(): Int {
        return 4;
    }

    private fun setTextView() {

        val time = calculateTime()
        binding.timeInfoTextView.text = "${time}분"

        val stations = calculateTotalStations()
        binding.totalStationTextView.text = "${stations}개 역 이동"
    }
}