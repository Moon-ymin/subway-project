package com.busanit.subway_project.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.busanit.subway_project.fragment.MinimumTransferFragment
import com.busanit.subway_project.fragment.ShortestTimeFragment

class RoutePagerAdapter {

    class RoutePagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return 2 // 최소환승과 최단시간 두 개의 페이지
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> MinimumTransferFragment()
                1 -> ShortestTimeFragment()
                else -> throw IllegalStateException("Unexpected position $position")
            }
        }
    }
}