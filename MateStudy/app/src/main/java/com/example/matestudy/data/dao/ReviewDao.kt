package com.example.matestudy.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.matestudy.data.entity.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    // Lấy danh sách đánh giá theo môn học (chỉ 'da_duyet')
    @Query("SELECT * FROM danh_gia_mon_giang_vien WHERE mon_hoc_id = :monHocId AND trang_thai = 'da_duyet' ORDER BY ngay_dang DESC")
    fun getReviewsByMonHoc(monHocId: Long): Flow<List<ReviewEntity>>

    // Tính trung bình sao theo môn
    @Query("SELECT AVG(diem_sao) FROM danh_gia_mon_giang_vien WHERE mon_hoc_id = :monHocId AND trang_thai = 'da_duyet'")
    fun getAverageRating(monHocId: Long): Flow<Double?>

    // Số lượng đánh giá theo môn
    @Query("SELECT COUNT(*) FROM danh_gia_mon_giang_vien WHERE mon_hoc_id = :monHocId AND trang_thai = 'da_duyet'")
    fun getReviewCount(monHocId: Long): Flow<Int>
}