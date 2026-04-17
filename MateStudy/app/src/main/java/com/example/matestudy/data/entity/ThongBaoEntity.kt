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
    val loai: String = "bai_viet",
    val daDoc: Boolean = false,
    val idLienQuan: Long? = null,
    val loaiLienQuan: String? = null,
    val ngayTao: Long = System.currentTimeMillis()
)