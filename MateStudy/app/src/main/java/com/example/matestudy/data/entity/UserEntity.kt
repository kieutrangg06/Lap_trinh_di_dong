package com.example.matestudy.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nguoi_dung")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tenDangNhap: String,
    val email: String,
    val matKhau: String, // Lưu ý: Trong thực tế KHÔNG nên lưu mật khẩu plain text!
    val nienKhoa: Int?, // year(4) → Int?
    val vaiTro: String, // "sinh_vien" hoặc "admin"
    val trangThai: String, // "hoat_dong", "bi_khoa", "da_xoa"
    val anhDaiDien: String? // url hoặc path
)