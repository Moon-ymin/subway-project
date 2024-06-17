package com.busanit.subway_project.retrofit

import com.busanit.subway_project.model.Line
import com.busanit.subway_project.model.Station
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface LineAPIService {
    // 해당 API로 get요청을 하면 Call 객체로 반환
    @GET("/line/{line_cd}")    // line_cd line_name 가져오기
    fun getLineByCode(@Path("line_cd") lineCd: Long): Call<Line>

}