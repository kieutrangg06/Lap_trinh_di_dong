package com.example.matestudy.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nguoi_dung")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tenDangNhap: String = "",
    val email: String = "",
    val matKhau: String = "",
    val nienKhoa: Int? = null,
    val vaiTro: String = "sinh_vien",
    val trangThai: String = "hoat_dong",
    val anhDaiDien: String? = null
)