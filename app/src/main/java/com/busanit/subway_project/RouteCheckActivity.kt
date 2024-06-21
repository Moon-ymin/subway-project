package com.busanit.subway_project

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.busanit.subway_project.adapter.RoutePagerAdapter
import com.busanit.subway_project.alarm.AlarmReceiver
import com.busanit.subway_project.alarm.TimerListener
import com.busanit.subway_project.databinding.ActivityRouteCheckBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class RouteCheckActivity : AppCompatActivity(), TimerListener {

    private lateinit var binding: ActivityRouteCheckBinding
    private var isTimerRunning = false

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
                0 -> {
                    if (isEng) {
                        "Fastest"
                    } else {
                        "최단시간"
                    }
                }
                1 -> if (isEng) {
                    "Minimum"
                } else {
                    "최소환승"
                }
                else -> null
            }
        }.attach()

        // "메인으로 돌아가기" 클릭 시 해당 액티비티를 종료하고 메인으로 이동(이전으로 이동)
        backToMainButton.setOnClickListener {
            finish()
        }

        // 상단바 구현
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 알람 설정
        createNotificationChannel()
    }

    // 상단바 구현
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        return false
    }

    // 타이머가 다른 탭에서 작동 중이라면 타이머를 설정하지 못하게 막음
    fun isTimerRunning(): Boolean {
        return isTimerRunning
    }

    fun setTimerRunning(running: Boolean) {
        isTimerRunning = running
    }

    // 알람 설정
    private fun setAlarm() {
        val context = applicationContext
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 1000, // 1초 후에 알람 설정 (테스트용)
            pendingIntent
        )
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "TimerChannel1",
                "Timer Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for timer notifications"
            }

            val notificationManager: NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // TimerListener 인터페이스 구현으로 Fragment에서 알람을 불러올 수 있도록 유도
    override fun onTimerFinished() {
        setAlarm()
    }
}