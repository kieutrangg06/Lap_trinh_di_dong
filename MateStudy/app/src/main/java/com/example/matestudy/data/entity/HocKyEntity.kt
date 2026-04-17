package com.example.matestudy.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hoc_ky")
data class HocKyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ten_hoc_ky: String = ""
)

@Entity(tableName = "mon_hoc")
data class MonHocEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tenMon: String = "",
    val tenGv: String = "",
    val diaDiem: String? = null,
    val thu: String? = null,
    val gioBatDau: String? = null,
    val gioKetThuc: String? = null,
    val ngayBatDau: String = "",
    val ngayKetThuc: String = "",
    val hocKyId: Long? = null
)

@Entity(tableName = "lich_ca_nhan")
data class LichCaNhanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sinhVienId: Long = 0,
    val loai: String = "",
    val monHocId: Long? = null,
    val skId: Long? = null,
    val mauSac: String = "#3788d8"
)

// sk_ca_nhan
@Entity(tableName = "sk_ca_nhan")
data class SkCaNhanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sinhVienId: Long = 0,
    val tieuDe: String = "",
    val diaDiem: String? = null,
    val thu: String? = null,
    val lapLai: String = "khong",
    val gioBatDau: String? = null,
    val gioKetThuc: String? = null,
    val ngayBatDau: String = "",
    val ngayKetThuc: String = ""
)