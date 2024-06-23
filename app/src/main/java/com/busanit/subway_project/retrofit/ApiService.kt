package com.busanit.subway_project.retrofit

import com.busanit.subway_project.model.LocationData
import com.busanit.subway_project.model.ResultWrapper
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/location")   // 상대 경로만 지정
    fun sendLocationData(@Body requestData: LocationData): Call<ResponseBody>

    @POST("api/result")     // 출발지 ~ 도착지의 최소환승, 최단시간 경로 가져오기
    fun getResultWrapper(@Body locationData: LocationData): Call<ResultWrapper>
}
