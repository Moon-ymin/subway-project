package com.busanit.subway_project.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {


    // private const val BASE_URL = "http://192.168.45.165:8080/"   // 영민 집에서 테스트 할 때
    const val BASE_URL = "http://10.100.203.36:8080/"   // 에스더 학원에서 테스트 할 떄
    //private const val BASE_URL = "http://10.100.203.36:8080/"    // 영민 학원에서 테스트 할 떄

    val stationService: StationAPIService by lazy {
        // Retrofit 객체 생성
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON 변환을 위한 컨버터
            .build()

        // API 인터페이스를 레트로핏을 사용하여 구현
        retrofit.create(StationAPIService::class.java)
    }
    val apiService: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}