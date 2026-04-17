package com.example.matestudy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matestudy.data.entity.HocKyEntity
import com.example.matestudy.data.entity.MonHocEntity
import com.example.matestudy.data.entity.ReviewEntity
import com.example.matestudy.data.repository.AuthRepository
import com.example.matestudy.data.repository.ReviewRepository
import com.example.matestudy.data.repository.ScheduleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val reviewRepository: ReviewRepository,
    private val authRepository: AuthRepository,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    // ────────────────────────────────────────────────
    // 1. STATE & FLOWS DỮ LIỆU
    // ────────────────────────────────────────────────

    private val _selectedMonHoc = MutableStateFlow<MonHocEntity?>(null)
    val selectedMonHoc: StateFlow<MonHocEntity?> = _selectedMonHoc.asStateFlow()

    private val _rating = MutableStateFlow(0)
    val rating: StateFlow<Int> = _rating.asStateFlow()

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val monHocList: StateFlow<List<MonHocEntity>> = scheduleRepository.getAllMonHoc()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hocKyList: StateFlow<List<HocKyEntity>> = scheduleRepository.getAllHocKy()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ────────────────────────────────────────────────
    // 2. CÁC HÀM CẬP NHẬT TRẠNG THÁI UI
    // ────────────────────────────────────────────────

    fun setSelectedMonHoc(monHoc: MonHocEntity?) {
        _selectedMonHoc.value = monHoc
    }

    fun setRating(value: Int) {
        if (value in 1..5) _rating.value = value
    }

    fun setContent(value: String) {
        _content.value = value
    }

    private fun resetForm() {
        _selectedMonHoc.value = null
        _rating.value = 0
        _content.value = ""
        _error.value = null
    }

    // ────────────────────────────────────────────────
    // 3. THAO TÁC GỬI ĐÁNH GIÁ (SUBMIT)
    // ────────────────────────────────────────────────

    fun submitReview(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val sinhVienId = authRepository.getCurrentUserFlow().first()?.id ?: run {
                _error.value = "Bạn cần đăng nhập để đánh giá"
                return@launch
            }

            val monHoc = _selectedMonHoc.value ?: run {
                _error.value = "Vui lòng chọn môn học"
                return@launch
            }

            if (_rating.value == 0 || _content.value.isBlank()) {
                _error.value = "Vui lòng đánh giá sao và nhập nội dung"
                return@launch
            }

            val review = ReviewEntity(
                mon_hoc_id = monHoc.id,
                sinh_vien_id = sinhVienId,
                diem_sao = _rating.value,
                noi_dung = _content.value.trim(),
                trang_thai = "cho_duyet",
                ngay_dang = System.currentTimeMillis()
            )

            try {
                reviewRepository.insertReview(review)
                resetForm()
                onSuccess()
            } catch (e: Exception) {
                _error.value = "Lỗi khi gửi đánh giá: ${e.message}"
            }
        }
    }

    fun submitReviewNew(
        monHoc: MonHocEntity?,
        rating: Int,
        content: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val sinhVienId = authRepository.getCurrentUserFlow().first()?.id ?: run {
                _error.value = "Bạn cần đăng nhập để đánh giá"
                return@launch
            }

            if (monHoc == null || rating == 0 || content.isBlank()) {
                _error.value = "Vui lòng chọn môn học, đánh giá sao và nhập nội dung"
                return@launch
            }

            val review = ReviewEntity(
                mon_hoc_id = monHoc.id,
                sinh_vien_id = sinhVienId,
                diem_sao = rating,
                noi_dung = content.trim(),
                trang_thai = "cho_duyet",
                ngay_dang = System.currentTimeMillis()
            )

            try {
                reviewRepository.insertReview(review)
                _error.value = null
                onSuccess()
            } catch (e: Exception) {
                _error.value = "Lỗi khi gửi đánh giá: ${e.message}"
            }
        }
    }

    // ────────────────────────────────────────────────
    // 4. TRUY VẤN THÔNG TIN ĐÁNH GIÁ (QUERY)
    // ────────────────────────────────────────────────

    fun getReviewsByMonHoc(monHocId: Long): Flow<List<ReviewEntity>> =
        reviewRepository.getReviewsByMonHoc(monHocId)

    fun getAverageRating(monHocId: Long): Flow<Double?> =
        reviewRepository.getAverageRating(monHocId)

    fun getReviewCount(monHocId: Long): Flow<Int> =
        reviewRepository.getReviewCount(monHocId)
}