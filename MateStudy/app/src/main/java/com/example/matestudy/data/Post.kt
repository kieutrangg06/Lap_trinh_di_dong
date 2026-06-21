package com.example.matestudy.data

import com.example.matestudy.data.entity.PostEntity

data class Post(
    val id: Long = 0,
    val tacGiaId: Long,
    val tacGiaTen: String = "",
    val tacGiaAvatar: String? = null,
    val tieuDe: String,
    val noiDung: String,
    val fileDinhKem: String? = null,
    val trangThai: String = "cho_duyet",
    val ngayDang: Long? = null,
    val category: String = "forum",
    val likeCount: Int = 0,
    val isLiked: Boolean = false
)

fun PostEntity.toPost(
    likeCount: Int = 0,
    isLiked: Boolean = false,
    tacGiaTen: String = "" ,
    tacGiaAvatar: String? = null
): Post = Post(
    id = id,
    tacGiaId = tac_gia_id,
    tacGiaTen = tacGiaTen,
    tacGiaAvatar = tacGiaAvatar,
    tieuDe = tieu_de,
    noiDung = noi_dung,
    fileDinhKem = file_dinh_kem,
    trangThai = trang_thai,
    ngayDang = ngay_dang,
    category = category,
    likeCount = likeCount,
    isLiked = isLiked
)