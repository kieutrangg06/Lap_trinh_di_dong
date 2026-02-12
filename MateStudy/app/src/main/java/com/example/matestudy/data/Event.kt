package com.example.matestudy.data

import java.time.LocalDate
import java.time.LocalTime

data class Event(
    val id: Long, // id của lich_ca_nhan
    val title: String,
    val location: String?,
    val teacher: String? = null, // Chỉ cho lớp
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val date: LocalDate, // Ngày cụ thể (sau khi generate lặp)
    val color: String,
    val loai: String, // "lop_chinh_thuc" or "su_kien_rieng"
    val repeat: String? = null, // Nếu su_kien_rieng
    val originalId: Long // id của mon_hoc hoặc sk
)