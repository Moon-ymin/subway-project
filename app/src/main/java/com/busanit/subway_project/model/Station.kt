package com.busanit.subway_project.model;

data class Station (
    val scode: Int,
    val sname: String,
    val line: Line,
    val exchange: Int
)