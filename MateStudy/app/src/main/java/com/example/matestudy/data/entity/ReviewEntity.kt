package com.example.matestudy.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "danh_gia_mon_giang_vien")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mon_hoc_id: Long? = null, // Liên kết với MonHocEntity
    val sinh_vien_id: Long, // ID sinh viên từ UserEntity
    val diem_sao: Int? = null, // 1-5, nullable theo SQL
    val noi_dung: String, // Nội dung đánh giá
    val trang_thai: String = "cho_duyet", // Enum: 'cho_duyet', 'da_duyet', 'bi_an'
    val ngay_dang: Long = System.currentTimeMillis() // Timestamp as Long để dễ xử lý
)