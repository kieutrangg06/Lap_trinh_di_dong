package com.example.matestudy.data

import java.time.LocalDate
import java.time.LocalTime

data class Event(
    val id: Long,
    val title: String,
    val location: String?,
    val teacher: String? = null,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val date: LocalDate,
    val color: String,
    val loai: String,
    val repeat: String? = null,
    val originalId: Long
)