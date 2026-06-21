package com.example.matestudy.data

import com.example.matestudy.data.entity.ReviewEntity

data class ReviewWithUser(
    val review: ReviewEntity,
    val tenDangNhap: String
)