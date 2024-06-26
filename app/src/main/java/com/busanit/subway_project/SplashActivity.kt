package com.busanit.subway_project

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val imageView = findViewById<ImageView>(R.id.splash_logo)

        Glide.with(this)
            .asGif()
            .load(R.drawable.splash_gif)
            .into(imageView)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // 스플래시 화면을 종료하여 뒤로가기 버튼을 눌렀을 때 스플래시 화면이 다시 나타나지 않도록
        }, 5000) // 3초 대기
    }
}
