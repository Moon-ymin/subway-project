package com.busanit.subway_project.model

import java.time.LocalTime

data class LocationData(    // 역의 scode 담고 있을 것
    val from: Int,
    val via: Int,
    val to: Int,
    val settingTime: String
)
