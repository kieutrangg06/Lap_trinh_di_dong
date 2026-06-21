package com.example.matestudy.data

data class MonHocWithReviews(
    val monHocId: Long,
    val tenMonHoc: String,
    val averageRating: Double?,
    val reviewCount: Int,
    val reviews: List<ReviewWithUser>
)