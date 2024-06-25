package com.busanit.subway_project.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.busanit.subway_project.fragment.MinimumTransferFragment
import com.busanit.subway_project.fragment.ShortestTimeFragment
import com.busanit.subway_project.model.SubwayResult

class RoutePagerAdapter {

    class RoutePagerAdapter(fa: FragmentActivity,
                            private val minTimeResultData: SubwayResult?,
                            private val minTransferData: SubwayResult?,
                            private val from: Int,
                            private val via: Int,
                            private val to: Int)
        : FragmentStateAdapter(fa) {

        override fun getItemCount(): Int = 2    // "최소환승"과 "최단시간" 두 개의 페이지

        override fun createFragment(position: Int): Fragment {
            val fragment = if (position == 0) {
                ShortestTimeFragment()
            } else {
                MinimumTransferFragment()
            }

            val bundle = Bundle().apply {
                if (position == 0) {
                    putParcelable("minTimeResult", minTimeResultData)
                    putInt("from", from)
                    putInt("via", via)
                    putInt("to", to)
                } else {
                    putParcelable("minTransferResult", minTransferData)
                    putInt("from", from)
                    putInt("via", via)
                    putInt("to", to)
                }
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}