package com.example.matestudy.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


// bai_viet_dien_dan
@Entity(tableName = "bai_viet_dien_dan")
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tac_gia_id: Long = 0,
    val tieu_de: String = "",
    val noi_dung: String = "",
    val file_dinh_kem: String? = null,
    val trang_thai: String = "cho_duyet",
    val ngay_dang: Long? = System.currentTimeMillis(),
    val category: String = "forum"
)

// binh_luan_dien_dan
@Entity(tableName = "binh_luan_dien_dan")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bai_viet_id: Long = 0,
    val tac_gia_id: Long = 0,
    val noi_dung: String = "",
    val ngay_tao: Long = System.currentTimeMillis()
)

// luot_thich_bai_viet (composite key, không cần sửa nhiều)
@Entity(tableName = "luot_thich_bai_viet", primaryKeys = ["bai_viet_id", "sinh_vien_id"])
data class LikeEntity(
    val bai_viet_id: Long = 0,
    val sinh_vien_id: Long = 0,
    val ngay_thich: Long = System.currentTimeMillis()
)