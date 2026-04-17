package com.example.matestudy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val reviews: List<ReviewWithUser>
)

class AdminReviewViewModel(
    private val reviewRepo: ReviewRepository,
    private val scheduleRepo: ScheduleRepository
) : ViewModel() {

    private val _allReviews = reviewRepo.getAllReviewsForAdminWithUser()
    private val _filterStatus = MutableStateFlow("Tất cả")
    val filterStatus = _filterStatus.asStateFlow()
    private val _searchQuery = MutableStateFlow("")

    // ────────────────────────────────────────────────
    // 1. LOGIC XỬ LÝ DỮ LIỆU (COMBINE FLOWS)
    // ────────────────────────────────────────────────

    val filteredMonHocWithReviews = combine(
        _allReviews,
        _filterStatus,
        _searchQuery
    ) { reviewsWithUser, status, query ->

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

        val grouped = filteredReviews.groupBy { it.review.mon_hoc_id }

        grouped.mapNotNull { (monId, revs) ->
            if (monId == null) return@mapNotNull null

            val tenMon = scheduleRepo.getMonHocById(monId)?.tenMon
                ?: "Môn học không xác định"

            if (query.isNotBlank() && !tenMon.contains(query, ignoreCase = true)) {
                return@mapNotNull null
            }

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

    // ────────────────────────────────────────────────
    // 2. CÁC HÀM CẬP NHẬT TRẠNG THÁI UI
    // ────────────────────────────────────────────────

    fun setFilter(status: String) {
        _filterStatus.value = status
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // ────────────────────────────────────────────────
    // 3. CÁC THAO TÁC XỬ LÝ ĐÁNH GIÁ (REPOSITORY)
    // ────────────────────────────────────────────────

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