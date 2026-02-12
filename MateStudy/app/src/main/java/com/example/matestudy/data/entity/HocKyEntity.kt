package com.example.matestudy.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hoc_ky")
data class HocKyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ten_hoc_ky: String
)

@Entity(tableName = "mon_hoc")
data class MonHocEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ten_mon: String,
    val ten_gv: String,
    val dia_diem: String?,
    val thu: String?, // "2", "3", ..., "CN"
    val gio_bat_dau: String?, // "HH:mm"
    val gio_ket_thuc: String?, // "HH:mm"
    val ngay_bat_dau: String, // "yyyy-MM-dd"
    val ngay_ket_thuc: String, // "yyyy-MM-dd"
    val hoc_ky_id: Long?
)

@Entity(tableName = "lich_ca_nhan")
data class LichCaNhanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sinh_vien_id: Long,
    val loai: String, // "lop_chinh_thuc" or "su_kien_rieng"
    val mon_hoc_id: Long?,
    val sk_id: Long?,
    val mau_sac: String = "#3788d8"
)

@Entity(tableName = "sk_ca_nhan")
data class SkCaNhanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sinh_vien_id: Long,
    val tieu_de: String,
    val dia_diem: String?,
    val thu: String?, // "2", "3", ..., "CN" nếu lặp tuần
    val lap_lai: String = "khong", // "khong", "hang_ngay", "hang_tuan"
    val gio_bat_dau: String?, // "HH:mm"
    val gio_ket_thuc: String?, // "HH:mm"
    val ngay_bat_dau: String, // "yyyy-MM-dd"
    val ngay_ket_thuc: String // "yyyy-MM-dd"
)