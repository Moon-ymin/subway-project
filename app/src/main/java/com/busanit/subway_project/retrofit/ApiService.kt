package com.busanit.subway_project.retrofit

import com.busanit.subway_project.model.LocationData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/location")   // 상대 경로만 지정
    fun sendLocationData(@Body requestData: LocationData): Call<ResponseBody>
}
