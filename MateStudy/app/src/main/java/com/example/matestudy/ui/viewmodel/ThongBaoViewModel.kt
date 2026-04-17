package com.example.matestudy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matestudy.data.entity.ThongBaoEntity
import com.example.matestudy.data.repository.AuthRepository
import com.example.matestudy.data.repository.ThongBaoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ThongBaoViewModel(
    private val thongBaoRepo: ThongBaoRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    // ────────────────────────────────────────────────
    // 1. STATE & USER CONTEXT
    // ────────────────────────────────────────────────

    private val currentUserId = authRepo.getCurrentUserFlow()
        .map { it?.id ?: 0L }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val thongBaos: StateFlow<List<ThongBaoEntity>> = currentUserId
        .flatMapLatest { uid ->
            if (uid == 0L) flowOf(emptyList())
            else thongBaoRepo.getThongBaoFlow(uid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadCount: StateFlow<Int> = currentUserId
        .flatMapLatest { uid ->
            if (uid == 0L) {
                flowOf(0)
            } else {
                flowOf(thongBaoRepo.getUnreadCount(uid))
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // ────────────────────────────────────────────────
    // 2. CÁC THAO TÁC XỬ LÝ THÔNG BÁO (REPOSITORY)
    // ────────────────────────────────────────────────

    fun markAsRead(id: Long) {
        viewModelScope.launch {
            thongBaoRepo.markAsRead(id)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            val uid = currentUserId.value
            if (uid > 0) thongBaoRepo.markAllAsRead(uid)
        }
    }
}