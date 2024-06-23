package com.busanit.subway_project.model

data class ResultWrapper(
    val minTransferResult: SubwayResult,    // 최소환승 Result
    val minTimeResult: SubwayResult         // 최단시간 Result
)
