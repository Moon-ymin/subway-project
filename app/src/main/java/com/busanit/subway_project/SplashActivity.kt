package com.busanit.subway_project

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 일정 시간 이후 MainActivity로 전환
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // 스플래시 화면을 종료하여 뒤로가기 버튼을 눌렀을 때 스플래시 화면이 다시 나타나지 않도록 합니다.
        }, 3000) // 3초 대기
    }
}
