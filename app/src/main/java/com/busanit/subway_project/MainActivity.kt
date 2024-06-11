package com.busanit.subway_project

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.busanit.subway_project.databinding.ActivityMainBinding
import com.github.chrisbanes.photoview.PhotoView


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val photoView = findViewById<View>(R.id.photo_view) as PhotoView
        photoView.setImageResource(R.drawable.busan_metro)
    }
}