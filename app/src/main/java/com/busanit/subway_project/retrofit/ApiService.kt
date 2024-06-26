package com.busanit.subway_project.retrofit

import com.busanit.subway_project.model.LocationData
import com.busanit.subway_project.model.ResultWrapper
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/location")   // 상대 경로만 지정
    fun sendLocationData(@Body requestData: LocationData): Call<ResultWrapper>

    // 타이머 업데이트를 서버에 전송하는 엔드포인트와 메서드 정의
    @POST("api/timer/update")
    fun sendTimerUpdate(@Body timeRemaining: Long): Call<ResponseBody>
}
