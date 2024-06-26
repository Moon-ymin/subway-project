package com.busanit.subway_project_watch.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.busanit.subway_project_watch.databinding.ActivityTimerBinding

class TimerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 바인딩 객체를 사용하여 UI 요소에 접근할 수 있음
        binding.setTimer.text = "00 : 00 : 00"  // 예시: 초기 타이머 값 설정
    }
}
