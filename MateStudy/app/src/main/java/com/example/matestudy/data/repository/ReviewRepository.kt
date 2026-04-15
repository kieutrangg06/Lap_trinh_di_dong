package com.example.matestudy.data.repository

import com.example.matestudy.data.entity.ReviewEntity
import com.example.matestudy.data.entity.ReviewWithUser
import com.example.matestudy.data.entity.ThongBaoEntity
import com.example.matestudy.data.remote.FirestoreDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class ReviewRepository(private val firestore: FirestoreDataSource,private val thongBaoRepo: ThongBaoRepository) {

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

    suspend fun approveReviewWithNotify(ngayDang: Long) {
        firestore.updateReviewStatus(ngayDang.toString(), "da_duyet")

        // Lấy thông tin review để biết tác giả là ai
        val query = firestore.getAllReviewsForAdmin().firstOrNull()?.find { it.ngay_dang == ngayDang }
        query?.let {
            thongBaoRepo.createThongBao(
                ThongBaoEntity(
                    sinhVienId = it.sinh_vien_id,
                    tieuDe = "Đánh giá được phê duyệt",
                    noiDung = "Đánh giá môn học của bạn đã được Admin phê duyệt.",
                    loai = "danh_gia",
                    idLienQuan = it.mon_hoc_id // Hoặc ID review nếu có
                )
            )
        }
    }

    suspend fun hideReview(ngayDang: Long) {
        firestore.updateReviewStatus(ngayDang.toString(), "bi_an")
    }

    suspend fun deleteReview(ngayDang: Long) {
        firestore.deleteReview(ngayDang)
    }

    fun getAllReviewsForAdminWithUser(): Flow<List<ReviewWithUser>> =
        firestore.getAllReviewsForAdminWithUser()
}