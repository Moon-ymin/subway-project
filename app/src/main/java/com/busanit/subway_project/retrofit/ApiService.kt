package com.busanit.subway_project.retrofit

import com.busanit.subway_project.model.LocationData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("http://10.100.203.36:8080/")
    fun sendLocationData(@Body requestData: LocationData): Call<ResponseBody>
}
