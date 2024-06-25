package com.busanit.subway_project.model

sealed class StationSchedule {

    data class Schedule (
        val scode: Int,
        val sname: String,
        val line: Line,
        val time: String
    ): StationSchedule()

    data class Walking (
        val notice: String
    ): StationSchedule()
}