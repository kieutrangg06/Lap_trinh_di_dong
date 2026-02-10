package com.example.matestudy.data

data class User(
    val id: Long = 0,
    val tenDangNhap: String = "",
    val email: String = "",
    val nienKhoa: Int? = null, // year(4) -> Int?
    val vaiTro: String = "sinh_vien",
    val trangThai: String = "hoat_dong",
    val anhDaiDien: String? = null
)