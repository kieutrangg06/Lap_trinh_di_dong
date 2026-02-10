package com.example.matestudy.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bai_viet_dien_dan")
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tac_gia_id: Long,
    val tieu_de: String,
    val noi_dung: String,
    val file_dinh_kem: String? = null,
    val trang_thai: String = "cho_duyet",  // "cho_duyet", "da_duyet", "bi_an"
    val ngay_dang: Long? = System.currentTimeMillis(),
    val category: String = "forum"  // "forum", "news", "market", "job", "lost", "docs"
)

@Entity(tableName = "binh_luan_dien_dan")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bai_viet_id: Long,
    val tac_gia_id: Long,
    val noi_dung: String,
    val ngay_tao: Long = System.currentTimeMillis()
)

@Entity(tableName = "luot_thich_bai_viet", primaryKeys = ["bai_viet_id", "sinh_vien_id"])
data class LikeEntity(
    val bai_viet_id: Long,
    val sinh_vien_id: Long,
    val ngay_thich: Long = System.currentTimeMillis()
)