package com.busanit.subway_project

import android.app.SearchManager
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.busanit.subway_project.adapter.RoutePagerAdapter
import com.busanit.subway_project.databinding.ActivityRouteCheckBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class RouteCheckActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRouteCheckBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRouteCheckBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val backToMainButton = findViewById<Button>(R.id.backToMainButton)

        val adapter = RoutePagerAdapter.RoutePagerAdapter(this)
        viewPager.adapter = adapter

        // 최단시간 | 최소환승 탭 구현
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "최단시간"
                1 -> "최소환승"
                else -> null
            }
        }.attach()

        backToMainButton.setOnClickListener {
            finish()
        }

        // 상단바 구현
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) // 툴바를 액션바로 설정
    }

    // 상단바 구현
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        return false
    }
}