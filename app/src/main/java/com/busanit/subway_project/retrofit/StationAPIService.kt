package com.busanit.subway_project.retrofit

import com.busanit.subway_project.model.Station
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface StationAPIService {
    // 해당 API로 get요청을 하면 Call 객체로 반환
    @GET("/station/get-all")    // 다 가져오기
    fun getAllStation(): Call<List<Station>>

    @GET("/station/{scode}")    // scode로 sname 가져오기
    fun getStationById(@Path("scode") scode: Long): Call<Station>

}