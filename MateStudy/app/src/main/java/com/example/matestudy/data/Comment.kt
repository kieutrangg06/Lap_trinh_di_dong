package com.example.matestudy.data

import com.example.matestudy.data.entity.CommentEntity

data class Comment(
    val id: Long = 0,
    val baiVietId: Long,
    val tacGiaId: Long,
    val tacGiaTen: String = "",
    val tacGiaAvatar: String? = null,
    val noiDung: String,
    val ngayTao: Long = System.currentTimeMillis()
)

fun CommentEntity.toComment(
    tacGiaTen: String = "",
    tacGiaAvatar: String? = null
): Comment = Comment(
    id = id,
    baiVietId = bai_viet_id,
    tacGiaId = tac_gia_id,
    tacGiaTen = tacGiaTen,
    tacGiaAvatar = tacGiaAvatar,
    noiDung = noi_dung,
    ngayTao = ngay_tao
)