package com.busanit.subway_project.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubwayResult(    // Result 에 담겨있는 내용
    val path: List<String>, // 지나가는 경로에 대한 String 형태("scode|sname|line_cd|schedule") List
    val transfers: Int,     // 경로의 환승 횟수
    val totalTime: Int           // 총 소요 시간
) : Parcelable