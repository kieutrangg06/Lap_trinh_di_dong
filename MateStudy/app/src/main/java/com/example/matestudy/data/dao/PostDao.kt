package com.example.matestudy.data.dao

data class PostWithLikeCount(
    val id: Long,
    val tac_gia_id: Long,
    val tieu_de: String,
    val noi_dung: String,
    val file_dinh_kem: String?,
    val trang_thai: String,
    val ngay_dang: Long?,
    val category: String,
    val likeCount: Int
)