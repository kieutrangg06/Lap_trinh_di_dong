package com.example.matestudy.data

import com.example.matestudy.data.entity.CommentEntity
import com.example.matestudy.data.entity.PostEntity
import com.example.matestudy.data.dao.PostWithLikeCount

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

fun PostWithLikeCount.toPost(
    isLiked: Boolean = false,
    tacGiaTen: String = "",
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