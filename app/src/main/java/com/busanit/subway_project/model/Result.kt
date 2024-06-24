package com.busanit.subway_project.model

data class Result(
    val transfers: Int,
    val totalTime: Int,
    val path: List<String>
)
