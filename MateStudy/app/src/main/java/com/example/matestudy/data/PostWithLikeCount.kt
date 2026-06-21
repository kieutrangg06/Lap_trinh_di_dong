package com.example.matestudy.data

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