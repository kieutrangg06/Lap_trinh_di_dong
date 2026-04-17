package com.example.matestudy.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "danh_gia_mon_giang_vien")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mon_hoc_id: Long? = null,
    val sinh_vien_id: Long = 0,
    val diem_sao: Int? = null,
    val noi_dung: String = "",
    val trang_thai: String = "cho_duyet",
    val ngay_dang: Long = System.currentTimeMillis()
)

data class ReviewWithUser(
    val review: ReviewEntity,
    val tenDangNhap: String
)