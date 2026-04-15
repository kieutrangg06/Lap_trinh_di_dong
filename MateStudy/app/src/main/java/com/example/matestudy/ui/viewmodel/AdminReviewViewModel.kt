package com.example.matestudy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matestudy.data.entity.ReviewEntity
import com.example.matestudy.data.entity.ReviewWithUser
import com.example.matestudy.data.repository.ReviewRepository
import com.example.matestudy.data.repository.ScheduleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MonHocWithReviews(
    val monHocId: Long,
    val tenMonHoc: String,
    val averageRating: Double?,
    val reviewCount: Int,
    val reviews: List<ReviewWithUser>   // ← Đổi từ ReviewEntity thành ReviewWithUser
)

class AdminReviewViewModel(
    private val reviewRepo: ReviewRepository,
    private val scheduleRepo: ScheduleRepository
) : ViewModel() {

    // Thành:
    private val _allReviews = reviewRepo.getAllReviewsForAdminWithUser()
    private val _filterStatus = MutableStateFlow("Tất cả")
    val filterStatus = _filterStatus.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    val filteredMonHocWithReviews = combine(
        _allReviews,      // Flow<List<ReviewWithUser>>
        _filterStatus,
        _searchQuery
    ) { reviewsWithUser, status, query ->

        // 1. Lọc theo trạng thái
        val filteredReviews = if (status == "Tất cả") {
            reviewsWithUser
        } else {
            reviewsWithUser.filter {
                when (status) {
                    "Chờ duyệt" -> it.review.trang_thai == "cho_duyet"
                    "Đã duyệt" -> it.review.trang_thai == "da_duyet"
                    "Bị ẩn"   -> it.review.trang_thai == "bi_an"
                    else -> true
                }
            }
        }

        // 2. Nhóm theo môn học
        val grouped = filteredReviews.groupBy { it.review.mon_hoc_id }

        grouped.mapNotNull { (monId, revs) ->
            if (monId == null) return@mapNotNull null

            // Lấy tên môn học
            val tenMon = scheduleRepo.getMonHocById(monId)?.tenMon
                ?: "Môn học không xác định"

            // Lọc theo từ khóa tìm kiếm (tên môn)
            if (query.isNotBlank() && !tenMon.contains(query, ignoreCase = true)) {
                return@mapNotNull null
            }

            // Tính điểm trung bình chỉ từ các đánh giá "Đã duyệt"
            val avg = revs.filter { it.review.trang_thai == "da_duyet" && it.review.diem_sao != null }
                .mapNotNull { it.review.diem_sao?.toDouble() }
                .average()
                .takeIf { !it.isNaN() }

            MonHocWithReviews(
                monHocId = monId,
                tenMonHoc = tenMon,
                averageRating = avg,
                reviewCount = revs.size,
                reviews = revs.sortedByDescending { it.review.ngay_dang }
            )
        }.sortedBy { it.tenMonHoc }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun setFilter(status: String) {
        _filterStatus.value = status
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun approveReview(ngayDang: Long) = viewModelScope.launch {
        reviewRepo.approveReviewWithNotify(ngayDang)
    }

    fun hideReview(ngayDang: Long) = viewModelScope.launch {
        reviewRepo.hideReview(ngayDang)
    }

    fun deleteReview(ngayDang: Long) = viewModelScope.launch {
        reviewRepo.deleteReview(ngayDang)
    }
}