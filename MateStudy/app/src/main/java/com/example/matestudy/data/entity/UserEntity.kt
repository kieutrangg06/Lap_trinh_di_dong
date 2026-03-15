package com.example.matestudy.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nguoi_dung")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val tenDangNhap: String = "",           // default empty string
    val email: String = "",                 // default empty
    val matKhau: String = "",               // default empty (dù không nên lưu plain text)

    val nienKhoa: Int? = null,              // nullable → default null
    val vaiTro: String = "sinh_vien",       // default là sinh_vien (phổ biến nhất)
    val trangThai: String = "hoat_dong",    // default hoạt động

    val anhDaiDien: String? = null          // nullable → default null
)