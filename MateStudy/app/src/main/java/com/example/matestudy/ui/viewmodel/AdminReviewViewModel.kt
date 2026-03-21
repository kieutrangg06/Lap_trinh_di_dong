package com.example.matestudy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matestudy.data.entity.ReviewEntity
import com.example.matestudy.data.repository.ReviewRepository
import com.example.matestudy.data.repository.ScheduleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminReviewViewModel(
    private val reviewRepo: ReviewRepository,
    private val scheduleRepo: ScheduleRepository
) : ViewModel() {

    private val _allReviews = reviewRepo.getAllReviewsForAdmin()
    private val _filterStatus = MutableStateFlow("Tất cả")
    val filterStatus = _filterStatus.asStateFlow()

    val filteredReviews = combine(_allReviews, _filterStatus) { reviews, status ->
        if (status == "Tất cả") reviews
        else reviews.filter { 
            when(status) {
                "Chờ duyệt" -> it.trang_thai == "cho_duyet"
                "Đã duyệt" -> it.trang_thai == "da_duyet"
                "Bị ẩn" -> it.trang_thai == "bi_an"
                else -> true
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(status: String) { _filterStatus.value = status }

    fun approveReview(ngayDang: Long) = viewModelScope.launch { reviewRepo.approveReview(ngayDang) }
    fun hideReview(ngayDang: Long) = viewModelScope.launch { reviewRepo.hideReview(ngayDang) }
    fun deleteReview(ngayDang: Long) = viewModelScope.launch { reviewRepo.deleteReview(ngayDang) }
    
    // Hàm bổ trợ lấy tên môn học (Optional)
    suspend fun getMonHocName(id: Long) = scheduleRepo.getMonHocById(id)?.tenMon ?: "N/A"
}