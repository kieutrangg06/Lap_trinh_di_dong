package com.example.matestudy.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "thong_bao")
data class ThongBaoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val sinhVienId: Long = 0,
    val tieuDe: String = "",
    val noiDung: String = "",
    val loai: String = "bai_viet",          // "bai_viet" hoặc "danh_gia" (thay vì enum để dễ mở rộng)
    val daDoc: Boolean = false,
    val idLienQuan: Long? = null,           // id bài viết hoặc id đánh giá
    val loaiLienQuan: String? = null,       // "post", "comment", "like", "review", ...
    val ngayTao: Long = System.currentTimeMillis()
)