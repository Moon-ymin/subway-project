package com.busanit.subway_project.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://192.168.45.165:8080/"

    val stationService: StationAPIService by lazy {
        // Retrofit 객체 생성
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON 변환을 위한 컨버터
            .build()

        // API 인터페이스를 레트로핏을 사용하여 구현
        retrofit.create(StationAPIService::class.java)
    }
}