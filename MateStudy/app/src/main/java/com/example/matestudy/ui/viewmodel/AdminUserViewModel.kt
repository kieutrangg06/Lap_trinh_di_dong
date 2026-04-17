package com.example.matestudy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matestudy.data.entity.UserEntity
import com.example.matestudy.data.repository.AuthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminUserViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _allUsers = repository.getAllUsersForAdmin()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _filterStatus = MutableStateFlow("Tất cả")
    val filterStatus = _filterStatus.asStateFlow()

    private val _filterNienKhoa = MutableStateFlow("Tất cả")
    val filterNienKhoa = _filterNienKhoa.asStateFlow()

    // ────────────────────────────────────────────────
    // 1. LOGIC LỌC DỮ LIỆU (COMBINE FLOWS)
    // ────────────────────────────────────────────────

    val filteredUsers = combine(
        _allUsers,
        _searchQuery,
        _filterStatus,
        _filterNienKhoa
    ) { users, query, status, nienKhoa ->
        users.filter { user ->
            val matchQuery = user.tenDangNhap.contains(query, ignoreCase = true) ||
                    user.email.contains(query, ignoreCase = true)

            val matchStatus = if (status == "Tất cả") true
            else (if (status == "Hoạt động") user.trangThai == "hoat_dong" else user.trangThai == "bi_khoa")

            val matchNienKhoa = if (nienKhoa == "Tất cả") true
            else user.nienKhoa.toString() == nienKhoa.removePrefix("K")

            matchQuery && matchStatus && matchNienKhoa && user.vaiTro != "admin"
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ────────────────────────────────────────────────
    // 2. CÁC HÀM CẬP NHẬT TRẠNG THÁI UI
    // ────────────────────────────────────────────────

    fun onSearchChange(query: String) {
        _searchQuery.value = query
    }

    // ────────────────────────────────────────────────
    // 3. CÁC THAO TÁC NGƯỜI DÙNG (REPOSITORY)
    // ────────────────────────────────────────────────

    fun toggleLock(user: UserEntity) = viewModelScope.launch {
        repository.toggleUserLock(user.id, user.trangThai)
    }

    fun deleteUser(userId: Long) = viewModelScope.launch {
        repository.deleteUser(userId)
    }
}