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
}