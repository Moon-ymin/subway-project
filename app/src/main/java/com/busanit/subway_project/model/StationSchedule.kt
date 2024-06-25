package com.busanit.subway_project.model

sealed class StationSchedule {

    data class Schedule (
        val scode: Int,
        val sname: String,
        val line: Line,
    ): StationSchedule()

    data class Walking (
        val notice: String
    ): StationSchedule()
}