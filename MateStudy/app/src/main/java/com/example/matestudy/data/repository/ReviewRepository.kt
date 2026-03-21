package com.example.matestudy.data.repository

import com.example.matestudy.data.entity.ReviewEntity
import com.example.matestudy.data.remote.FirestoreDataSource
import kotlinx.coroutines.flow.Flow

class ReviewRepository(private val firestore: FirestoreDataSource) {

    suspend fun insertReview(review: ReviewEntity) {
        firestore.insertReview(review)
    }

    fun getReviewsByMonHoc(monHocId: Long): Flow<List<ReviewEntity>> =
        firestore.getReviewsByMonHoc(monHocId)

    fun getAverageRating(monHocId: Long): Flow<Double?> =
        firestore.getAverageRating(monHocId)

    fun getReviewCount(monHocId: Long): Flow<Int> =
        firestore.getReviewCount(monHocId)

    fun getAllReviewsForAdmin(): Flow<List<ReviewEntity>> = firestore.getAllReviewsForAdmin()

    suspend fun approveReview(ngayDang: Long) {
        // Tìm và update dựa trên timestamp (ngay_dang) làm định danh tạm thời nếu ID không có sẵn
        val query = firestore.updateReviewStatus(ngayDang.toString(), "da_duyet")
    }

    suspend fun hideReview(ngayDang: Long) {
        firestore.updateReviewStatus(ngayDang.toString(), "bi_an")
    }

    suspend fun deleteReview(ngayDang: Long) {
        firestore.deleteReview(ngayDang)
    }
}