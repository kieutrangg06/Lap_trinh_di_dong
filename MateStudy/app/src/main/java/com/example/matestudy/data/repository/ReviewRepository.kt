package com.example.matestudy.data.repository

import com.example.matestudy.data.AppDatabase
import com.example.matestudy.data.entity.ReviewEntity
import kotlinx.coroutines.flow.Flow

class ReviewRepository(private val db: AppDatabase) {
    private val reviewDao = db.reviewDao()

    suspend fun insertReview(review: ReviewEntity) {
        reviewDao.insertReview(review)
    }

    fun getReviewsByMonHoc(monHocId: Long): Flow<List<ReviewEntity>> =
        reviewDao.getReviewsByMonHoc(monHocId)

    fun getAverageRating(monHocId: Long): Flow<Double?> =
        reviewDao.getAverageRating(monHocId)

    fun getReviewCount(monHocId: Long): Flow<Int> =
        reviewDao.getReviewCount(monHocId)
}