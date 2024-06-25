package com.busanit.subway_project

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.busanit.subway_project.adapter.RoutePagerAdapter
import com.busanit.subway_project.alarm.AlarmReceiver
import com.busanit.subway_project.alarm.TimerCallback
import com.busanit.subway_project.databinding.ActivityRouteCheckBinding
import com.busanit.subway_project.model.LocationData
import com.busanit.subway_project.model.ResultWrapper
import com.busanit.subway_project.model.SubwayResult
import com.busanit.subway_project.retrofit.RetrofitClient
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RouteCheckActivity : AppCompatActivity(), TimerCallback {

    private lateinit var binding: ActivityRouteCheckBinding
    private var isTimerRunning = false

    // 알림 및 알람 설정
    private val CHANNEL_ID = "TimerChannel1"                // 채널 아이디(AlarmReceiver)
    private val REQUEST_PERMISSIONS_CODE = 1                // 승인 코드
    private val REQUIRED_PERMISSIONS = arrayOf(             // 권한 요청 목록
        android.Manifest.permission.POST_NOTIFICATIONS,
        android.Manifest.permission.USE_EXACT_ALARM,
        android.Manifest.permission.VIBRATE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRouteCheckBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // 메인 화면에서 넘어온 데이터 가져오기!
        val minTransferData: SubwayResult? = intent.getParcelableExtra("minTransferResult")
        val minTimeData: SubwayResult? = intent.getParcelableExtra("minTimeResult")

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val backToMainButton = findViewById<Button>(R.id.backToMainButton)

        val adapter = RoutePagerAdapter.RoutePagerAdapter(this, minTimeData, minTransferData, from, via, to)
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

        // 알림 구현 채널
        createNotificationChannel()
        requestPermissionsIfNecessary()
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

    // 타이머 & 알림 및 알람 연결하는 인터페이스 오버라이드
    override fun onTimerFinished() {
        setAlarm()
    }

    // 타이머가 끝났을 경우 알림 및 알람 설정 메서드
    @SuppressLint("ScheduleExactAlarm", "MissingPermission")
    private fun setAlarm() {

        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent)

        // 상단바 알림
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.bsp_app_icon_round)
            .setContentTitle("타이머 종료")
            .setContentText("역에 도착했습니다!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(this).notify(1, notification)
    }

    // 타이머 & 알림 및 알람 구현 채널
    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "TimerChannel1",
                "Timer Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Timer Notifications 채널"
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // 필요한 경우 권한을 요청하는 메서드
    private fun requestPermissionsIfNecessary(): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionsToRequest = REQUIRED_PERMISSIONS.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }

            if (permissionsToRequest.isNotEmpty()) {
                requestPermissions(permissionsToRequest.toTypedArray(), REQUEST_PERMISSIONS_CODE)
                return false // 권한 요청이 필요하면 false 반환
            }
        }

        return true // 모든 권한이 허용되었으면 true 반환
    }

    // 권한 요청 결과 메서드
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            for (i in permissions.indices) {
                // 권한 승인이 되지 않았을 경우 토스트 메시지 띄우기
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "${permissions[i]} : 권한이 필요합니다.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}