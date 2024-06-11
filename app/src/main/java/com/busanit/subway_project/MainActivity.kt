package com.busanit.subway_project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.busanit.subway_project.databinding.ActivityMainBinding
import com.github.chrisbanes.photoview.PhotoView

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val photoView: PhotoView = findViewById(R.id.photo_view)
        photoView.setImageResource(R.drawable.busan_metro_kor)  // 지하철 노선도 이미지 설정

    }
}
